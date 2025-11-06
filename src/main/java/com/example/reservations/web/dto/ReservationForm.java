package com.example.reservations.web.dto;

import com.example.reservations.model.Participant;
import com.example.reservations.model.Reservation;
import com.example.reservations.model.ReservationAccess;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ReservationForm {

    private static final Pattern PARTICIPANT_NAME_PATTERN =
            Pattern.compile("^[A-Za-zÄÖÜäöüß]+(?:\\s+[A-Za-zÄÖÜäöüß]+)*$");

    @NotBlank(message = "Titel ist erforderlich")
    private String title;

    @NotBlank(message = "Ort ist erforderlich")
    private String location;

    @NotNull(message = "Raumnummer ist erforderlich")
    @Min(value = 101, message = "Raumnummer muss zwischen 101 und 105 liegen")
    @Max(value = 105, message = "Raumnummer muss zwischen 101 und 105 liegen")
    private Integer roomNumber;

    @NotBlank(message = "Bemerkungen sind erforderlich")
    @Size(min = 10, max = 200, message = "Bemerkungen müssen zwischen 10 und 200 Zeichen lang sein")
    private String description;

    @NotNull(message = "Startzeit ist erforderlich")
    @Future(message = "Startzeit muss in der Zukunft liegen")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startTime;

    @NotNull(message = "Endzeit ist erforderlich")
    @Future(message = "Endzeit muss in der Zukunft liegen")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endTime;

    @NotNull(message = "Zugriffstyp ist erforderlich")
    private ReservationAccess accessType = ReservationAccess.PUBLIC;

    private String accessCode;

    @NotBlank(message = "Teilnehmerliste ist erforderlich")
    private String participantsText;

    public ReservationForm() {
    }

    public Reservation toReservation() {
        Reservation reservation = new Reservation();
        reservation.setTitle(title != null ? title.trim() : null);
        reservation.setLocation(location != null ? location.trim() : null);
        reservation.setRoomNumber(roomNumber);
        reservation.setDescription(description != null ? description.trim() : null);
        reservation.setStartTime(startTime);
        reservation.setEndTime(endTime);
        reservation.setAccessType(accessType);
        reservation.setAccessCode(accessCode);

        reservation.setParticipants(parseParticipants());
        return reservation;
    }

    private List<Participant> parseParticipants() {
        List<String> names = parseParticipantNames();
        return names.stream()
                .map(Participant::new)
                .collect(Collectors.toList());
    }

    private List<String> parseParticipantNames() {
        if (participantsText == null || participantsText.isBlank()) {
            return List.of();
        }

        return Arrays.stream(participantsText.split(","))
                .map(String::trim)
                .filter(name -> !name.isEmpty())
                .collect(Collectors.toList());
    }

    @AssertTrue(message = "Mindestens ein Teilnehmer ist erforderlich")
    public boolean hasAtLeastOneParticipant() {
        return !parseParticipantNames().isEmpty();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public ReservationAccess getAccessType() {
        return accessType;
    }

    public void setAccessType(ReservationAccess accessType) {
        this.accessType = accessType;
    }

    public String getAccessCode() {
        return accessCode;
    }

    public void setAccessCode(String accessCode) {
        this.accessCode = accessCode;
    }

    public String getParticipantsText() {
        return participantsText;
    }

    public void setParticipantsText(String participantsText) {
        this.participantsText = participantsText;
    }

    public Integer getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(Integer roomNumber) {
        this.roomNumber = roomNumber;
    }

    public static ReservationForm fromReservation(Reservation reservation) {
        ReservationForm form = new ReservationForm();
        form.setTitle(reservation.getTitle());
        form.setLocation(reservation.getLocation());
        form.setRoomNumber(reservation.getRoomNumber());
        form.setDescription(reservation.getDescription());
        form.setStartTime(reservation.getStartTime());
        form.setEndTime(reservation.getEndTime());
        form.setAccessType(reservation.getAccessType());
        form.setAccessCode(reservation.getAccessCode());

        // Teilnehmer als Komma-String
        String participants = reservation.getParticipants().stream()
                .map(Participant::getName)
                .collect(Collectors.joining(", "));
        form.setParticipantsText(participants);

        return form;
    }
}
