package com.example.reservations.web.dto;

import com.example.reservations.model.Reservation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
                        .anyMatch(violation -> violation.getMessage().equals("Mindestens ein Teilnehmer ist erforderlich")),
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
        return form;
    }
}
