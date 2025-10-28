package com.example.reservations.web;

import com.example.reservations.model.Reservation;
import com.example.reservations.model.ReservationAccess;
import com.example.reservations.service.ReservationService;
import com.example.reservations.web.dto.ReservationForm;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @ModelAttribute("accessTypes")
    public ReservationAccess[] accessTypes() {
        return ReservationAccess.values();
    }

    @GetMapping({"/", "/reservations"})
    public String index(Model model) {
        List<Reservation> reservations = reservationService.findAll();
        model.addAttribute("reservations", reservations);
        return "index";
    }

    @GetMapping("/access")
    public String accessByKey(@RequestParam(value = "key", required = false) String key, Model model) {
        if (key == null || key.isBlank()) {
            model.addAttribute("error", "Please enter a valid access key");
            model.addAttribute("reservations", reservationService.findAll());
            return "index";
        }

        // Try public key first
        Optional<Reservation> reservation = reservationService.findByPublicKey(key);
        if (reservation.isPresent()) {
            return "redirect:/reservations/" + reservation.get().getId() + "/public";
        }

        // Try private key - WICHTIG: key als Parameter weitergeben!
        reservation = reservationService.findByPrivateKey(key);
        if (reservation.isPresent()) {
            return "redirect:/reservations/" + reservation.get().getId() + "/private?authorized=true&key=" + key;
        }

        model.addAttribute("error", "Invalid access key");
        model.addAttribute("reservations", reservationService.findAll());
        return "index";
    }

    @GetMapping("/reservations/new")
    public String newReservation(Model model) {
        model.addAttribute("reservationForm", new ReservationForm());
        return "reservation-form";
    }

    @PostMapping("/reservations")
    public String createReservation(
            @Valid @ModelAttribute("reservationForm") ReservationForm form,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "reservation-form";
        }

        try {
            Reservation reservation = form.toReservation();
            Reservation saved = reservationService.createReservation(reservation);
            return "redirect:/reservations/" + saved.getId() + "/confirm";
        } catch (IllegalArgumentException | IllegalStateException ex) {
            bindingResult.reject("reservation.error", ex.getMessage());
            return "reservation-form";
        }
    }

    @GetMapping("/reservations/{id}/confirm")
    public String confirmation(@PathVariable Long id, Model model) {
        Reservation reservation = reservationService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
        model.addAttribute("reservation", reservation);
        return "reservation-confirmation";
    }

    @GetMapping("/reservations/{id}/public")
    public String publicView(@PathVariable Long id, Model model) {
        Reservation reservation = reservationService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
        if (reservation.getAccessType() != ReservationAccess.PUBLIC) {
            model.addAttribute("error", "This reservation is private.");
            return "reservation-public";
        }
        model.addAttribute("reservation", reservation);
        return "reservation-public";
    }

    @GetMapping("/reservations/{id}/edit")
    public String editReservation(@PathVariable Long id,
                                   @RequestParam String key,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        Reservation reservation = reservationService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));

        // Autorisierung prüfen
        if (!key.equals(reservation.getPrivateKey())) {
            redirectAttributes.addFlashAttribute("error", "Invalid private key");
            return "redirect:/";
        }

        // Formular vorbefüllen
        ReservationForm form = ReservationForm.fromReservation(reservation);
        model.addAttribute("reservationForm", form);
        model.addAttribute("reservationId", id);
        model.addAttribute("privateKey", key);
        model.addAttribute("editMode", true);
        return "reservation-form";
    }

    @PostMapping("/reservations/{id}")
    public String updateReservation(@PathVariable Long id,
                                     @RequestParam String key,
                                     @Valid @ModelAttribute("reservationForm") ReservationForm form,
                                     BindingResult bindingResult,
                                     Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("reservationId", id);
            model.addAttribute("privateKey", key);
            model.addAttribute("editMode", true);
            return "reservation-form";
        }

        try {
            Reservation updated = reservationService.updateReservation(id, key, form.toReservation());
            return "redirect:/reservations/" + updated.getId() + "/confirm";
        } catch (IllegalArgumentException | IllegalStateException ex) {
            bindingResult.reject("reservation.error", ex.getMessage());
            model.addAttribute("reservationId", id);
            model.addAttribute("privateKey", key);
            model.addAttribute("editMode", true);
            return "reservation-form";
        }
    }

    @PostMapping("/reservations/{id}/delete")
    public String deleteReservation(@PathVariable Long id,
                                     @RequestParam String key,
                                     RedirectAttributes redirectAttributes) {
        try {
            reservationService.deleteReservation(id, key);
            redirectAttributes.addFlashAttribute("success", "Reservation successfully deleted");
            return "redirect:/";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/reservations/" + id + "/private?authorized=true&key=" + key;
        }
    }

    @GetMapping("/reservations/{id}/private")
    public String privateView(@PathVariable Long id,
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "authorized", required = false) Boolean authorized,
            @RequestParam(value = "key", required = false) String key,
            Model model) {
        Reservation reservation = reservationService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));
        model.addAttribute("reservation", reservation);

        if (Boolean.TRUE.equals(authorized) && key != null) {
            model.addAttribute("authorized", true);
            model.addAttribute("privateKey", key);
            return "reservation-private";
        }

        if (reservation.getAccessType() != ReservationAccess.PRIVATE) {
            model.addAttribute("error", "This reservation is public.");
            return "reservation-private";
        }

        if (code == null) {
            model.addAttribute("error", "Please provide the private access code.");
            return "reservation-private";
        }

        if (!code.equals(reservation.getAccessCode())) {
            model.addAttribute("error", "Incorrect access code provided.");
            return "reservation-private";
        }

        model.addAttribute("authorized", true);
        return "reservation-private";
    }
}
