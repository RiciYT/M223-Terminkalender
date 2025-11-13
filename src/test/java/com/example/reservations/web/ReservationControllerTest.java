package com.example.reservations.web;

import com.example.reservations.model.Participant;
import com.example.reservations.model.Reservation;
import com.example.reservations.model.ReservationAccess;
import com.example.reservations.service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReservationService reservationService;

    private Reservation publicReservation;
    private Reservation privateReservation;

    @BeforeEach
    void setUp() {
        // Create a public reservation
        publicReservation = new Reservation();
        publicReservation.setTitle("Public Meeting");
        publicReservation.setLocation("Conference Room A");
        publicReservation.setRoomNumber(101);
        publicReservation.setDescription("This is a public meeting accessible with public key");
        LocalDateTime startTime = LocalDateTime.now().plusDays(1).withNano(0);
        publicReservation.setStartTime(startTime);
        publicReservation.setEndTime(startTime.plusHours(2));
        publicReservation.setAccessType(ReservationAccess.PUBLIC);
        publicReservation.addParticipant(new Participant("John Doe"));
        publicReservation = reservationService.createReservation(publicReservation);

        // Create a private reservation
        privateReservation = new Reservation();
        privateReservation.setTitle("Private Meeting");
        privateReservation.setLocation("Conference Room B");
        privateReservation.setRoomNumber(102);
        privateReservation.setDescription("This is a private meeting accessible with private key or access code");
        LocalDateTime privateStartTime = LocalDateTime.now().plusDays(2).withNano(0);
        privateReservation.setStartTime(privateStartTime);
        privateReservation.setEndTime(privateStartTime.plusHours(1));
        privateReservation.setAccessType(ReservationAccess.PRIVATE);
        privateReservation.setAccessCode("secret123");
        privateReservation.addParticipant(new Participant("Jane Smith"));
        privateReservation = reservationService.createReservation(privateReservation);
    }

    @Test
    void testAccessWithPublicKey_RedirectsToPublicView() throws Exception {
        // Test accessing with public key redirects to public view page
        mockMvc.perform(get("/access")
                        .param("key", publicReservation.getPublicKey()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/reservations/" + publicReservation.getId() + "/public"));
    }

    @Test
    void testAccessWithPrivateKey_RedirectsToPrivateViewWithAuthorization() throws Exception {
        // Test accessing with private key redirects to private view with authorization
        mockMvc.perform(get("/access")
                        .param("key", publicReservation.getPrivateKey()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/reservations/" + publicReservation.getId() + 
                        "/private?authorized=true&key=" + publicReservation.getPrivateKey()));
    }

    @Test
    void testAccessWithInvalidKey_ShowsError() throws Exception {
        // Test accessing with invalid key shows error message
        mockMvc.perform(get("/access")
                        .param("key", "invalid-key-123"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", "Ungültiger Zugriffsschlüssel"));
    }

    @Test
    void testAccessWithBlankKey_ShowsError() throws Exception {
        // Test accessing with blank key shows error message
        mockMvc.perform(get("/access")
                        .param("key", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", "Bitte geben Sie einen gültigen Zugriffsschlüssel ein"));
    }

    @Test
    void testAccessWithNullKey_ShowsError() throws Exception {
        // Test accessing without key parameter shows error message
        mockMvc.perform(get("/access"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", "Bitte geben Sie einen gültigen Zugriffsschlüssel ein"));
    }

    @Test
    void testPublicView_DisplaysReservationDetails() throws Exception {
        // Test that public view displays reservation details
        mockMvc.perform(get("/reservations/" + publicReservation.getId() + "/public"))
                .andExpect(status().isOk())
                .andExpect(view().name("reservation-public"))
                .andExpect(model().attributeExists("reservation"))
                .andExpect(model().attribute("reservation", publicReservation));
    }

    @Test
    void testPrivateView_WithAuthorization_DisplaysManagementOptions() throws Exception {
        // Test that private view with authorization displays management options
        mockMvc.perform(get("/reservations/" + publicReservation.getId() + "/private")
                        .param("authorized", "true")
                        .param("key", publicReservation.getPrivateKey()))
                .andExpect(status().isOk())
                .andExpect(view().name("reservation-private"))
                .andExpect(model().attributeExists("reservation"))
                .andExpect(model().attribute("authorized", true))
                .andExpect(model().attribute("privateKey", publicReservation.getPrivateKey()));
    }

    @Test
    void testIndexPage_DisplaysKeyInputForm() throws Exception {
        // Test that index page displays the key input form
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("reservations"));
    }
}
