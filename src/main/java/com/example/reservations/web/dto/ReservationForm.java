package com.example.reservations.web.dto;

import com.example.reservations.model.Participant;
import com.example.reservations.model.Reservation;
import com.example.reservations.model.ReservationAccess;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReservationForm {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Location is required")
    private String location;

    private String description;

    @NotNull(message = "Start time is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    @Future(message = "End time must be in the future")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endTime;

    @NotNull(message = "Access type is required")
    private ReservationAccess accessType = ReservationAccess.PUBLIC;

    private String accessCode;

    @Valid
    private List<ParticipantForm> participants = defaultParticipants();

    public ReservationForm() {
    }

    private static List<ParticipantForm> defaultParticipants() {
        List<ParticipantForm> defaults = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            defaults.add(new ParticipantForm());
        }
        return defaults;
    }

    public Reservation toReservation() {
        Reservation reservation = new Reservation();
        reservation.setTitle(title);
        reservation.setLocation(location);
        reservation.setDescription(description);
        reservation.setStartTime(startTime);
        reservation.setEndTime(endTime);
        reservation.setAccessType(accessType);
        reservation.setAccessCode(accessCode);

        List<Participant> participantEntities = new ArrayList<>();
        if (participants != null) {
            for (ParticipantForm participantForm : participants) {
                if (participantForm.getName() != null && !participantForm.getName().isBlank()
                        && participantForm.getEmail() != null && !participantForm.getEmail().isBlank()) {
                    participantEntities.add(new Participant(participantForm.getName(), participantForm.getEmail()));
                }
            }
        }
        reservation.setParticipants(participantEntities);
        return reservation;
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

    public List<ParticipantForm> getParticipants() {
        return participants;
    }

    public void setParticipants(List<ParticipantForm> participants) {
        this.participants = participants;
    }
}
