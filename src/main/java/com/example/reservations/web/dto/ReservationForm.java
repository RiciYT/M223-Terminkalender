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

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Location is required")
    private String location;

    @NotNull(message = "Room number is required")
    @Min(value = 101, message = "Room number must be between 101 and 105")
    @Max(value = 105, message = "Room number must be between 101 and 105")
    private Integer roomNumber;

    @NotBlank(message = "Remarks are required")
    @Size(min = 10, max = 200, message = "Remarks must be between 10 and 200 characters")
    private String description;

    @NotNull(message = "Start time is required")
    @Future(message = "Start time must be in the future")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    @Future(message = "End time must be in the future")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endTime;

    @NotNull(message = "Access type is required")
    private ReservationAccess accessType = ReservationAccess.PUBLIC;

    private String accessCode;

    @NotBlank(message = "Participants list is required")
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
        reservation.setAccessCode(accessCode != null ? accessCode.trim() : null);

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

    @AssertTrue(message = "At least one participant is required")
    public boolean hasAtLeastOneParticipant() {
        return !parseParticipantNames().isEmpty();
    }

    @AssertTrue(message = "Participant names may only contain letters and spaces")
    public boolean isParticipantNamesValid() {
        return parseParticipantNames().stream()
                .allMatch(name -> PARTICIPANT_NAME_PATTERN.matcher(name).matches());
    }

    @AssertTrue(message = "Access code is required for private reservations")
    public boolean isAccessCodeProvidedForPrivateReservations() {
        if (accessType != ReservationAccess.PRIVATE) {
            return true;
        }
        return accessCode != null && !accessCode.isBlank();
    }

    @AssertTrue(message = "End time must be after the start time")
    public boolean isEndAfterStart() {
        if (startTime == null || endTime == null) {
            return true;
        }
        return endTime.isAfter(startTime);
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
