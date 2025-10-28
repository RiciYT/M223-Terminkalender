package com.example.reservations.web.dto;

import com.example.reservations.model.Reservation;
import com.example.reservations.model.ReservationAccess;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReservationFormTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void participantsListMustContainAtLeastOneName() {
        ReservationForm form = buildValidForm();
        form.setParticipantsText(", , ,");

        Set<ConstraintViolation<ReservationForm>> violations = validator.validate(form);

        assertTrue(
                violations.stream()
                        .anyMatch(violation -> violation.getMessage().equals("At least one participant is required")),
                "Validation should fail when no participant names are provided"
        );
    }

    @Test
    void toReservationParsesParticipantsFromText() {
        ReservationForm form = buildValidForm();
        form.setParticipantsText("Alice Smith, Bob Jones");

        Reservation reservation = form.toReservation();

        assertEquals(2, reservation.getParticipants().size());
        assertEquals("Alice Smith", reservation.getParticipants().get(0).getName());
        assertEquals("Bob Jones", reservation.getParticipants().get(1).getName());
    }

    private ReservationForm buildValidForm() {
        ReservationForm form = new ReservationForm();
        form.setTitle("Team Sync");
        form.setLocation("Conference Room");
        form.setRoomNumber(101);
        form.setDescription("Valid remarks for meeting");
        form.setStartTime(LocalDateTime.now().plusDays(1));
        form.setEndTime(LocalDateTime.now().plusDays(1).plusHours(1));
        form.setAccessType(ReservationAccess.PUBLIC);
        form.setParticipantsText("Alice Smith");
        return form;
    }

    @Test
    void privateReservationsRequireAccessCode() {
        ReservationForm form = buildValidForm();
        form.setAccessType(ReservationAccess.PRIVATE);
        form.setAccessCode(" ");

        assertFalse(form.isAccessCodeProvidedForPrivateReservations());

        Set<ConstraintViolation<ReservationForm>> violations = validator.validate(form);

        assertTrue(
                violations.stream()
                        .anyMatch(violation -> violation.getMessage().contains("Access code is required for private reservations")),
                "Validation should fail when private reservations are missing an access code"
        );
    }

    @Test
    void participantNamesMustContainOnlyLetters() {
        ReservationForm form = buildValidForm();
        form.setParticipantsText("Alice Smith, Bob123");

        assertFalse(form.isParticipantNamesValid());

        Set<ConstraintViolation<ReservationForm>> violations = validator.validate(form);

        assertTrue(
                violations.stream()
                        .anyMatch(violation -> violation.getMessage().contains("Participant names may only contain letters and spaces")),
                "Validation should fail when participant names contain invalid characters"
        );
    }

    @Test
    void endTimeMustBeAfterStartTime() {
        ReservationForm form = buildValidForm();
        form.setEndTime(form.getStartTime().minusHours(1));

        assertFalse(form.isEndAfterStart());

        Set<ConstraintViolation<ReservationForm>> violations = validator.validate(form);

        assertTrue(
                violations.stream()
                        .anyMatch(violation -> violation.getMessage().contains("End time must be after the start time")),
                "Validation should fail when end time precedes start time"
        );
    }
}
