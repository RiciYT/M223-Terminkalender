package com.example.reservations.service;

import com.example.reservations.model.Participant;
import com.example.reservations.model.Reservation;
import com.example.reservations.model.ReservationAccess;
import com.example.reservations.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ReservationServiceTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationRepository reservationRepository;

    private Reservation testReservation;

    @BeforeEach
    void setUp() {
        // Clean database
        reservationRepository.deleteAll();

        // Create a test reservation
        testReservation = new Reservation();
        testReservation.setTitle("Test Meeting");
        testReservation.setLocation("Conference Room A");
        testReservation.setRoomNumber(101);
        testReservation.setDescription("This is a test meeting description with enough characters");
        LocalDateTime startTime = LocalDateTime.now().plusDays(1).withNano(0);
        testReservation.setStartTime(startTime);
        testReservation.setEndTime(startTime.plusHours(2));
        testReservation.setAccessType(ReservationAccess.PUBLIC);

        Participant participant = new Participant("John Doe");
        testReservation.addParticipant(participant);

        testReservation = reservationService.createReservation(testReservation);
    }

    @Test
    void testCreateReservation() {
        assertNotNull(testReservation.getId());
        assertNotNull(testReservation.getPublicKey());
        assertNotNull(testReservation.getPrivateKey());
        assertNotEquals(testReservation.getPublicKey(), testReservation.getPrivateKey());
        assertEquals(1, testReservation.getParticipants().size());
    }

    @Test
    void testFindByPublicKey() {
        Optional<Reservation> found = reservationService.findByPublicKey(testReservation.getPublicKey());
        assertTrue(found.isPresent());
        assertEquals(testReservation.getId(), found.get().getId());
    }

    @Test
    void testFindByPrivateKey() {
        Optional<Reservation> found = reservationService.findByPrivateKey(testReservation.getPrivateKey());
        assertTrue(found.isPresent());
        assertEquals(testReservation.getId(), found.get().getId());
    }

    @Test
    void testUpdateReservation() {
        // Create updated data
        Reservation updatedData = new Reservation();
        updatedData.setTitle("Updated Meeting");
        updatedData.setLocation("Conference Room B");
        updatedData.setRoomNumber(102);
        updatedData.setDescription("This is an updated meeting description with enough characters");
        LocalDateTime updatedStart = LocalDateTime.now().plusDays(2).withNano(0);
        updatedData.setStartTime(updatedStart);
        updatedData.setEndTime(updatedStart.plusHours(3));
        updatedData.setAccessType(ReservationAccess.PRIVATE);
        updatedData.setAccessCode("secret123");

        Participant newParticipant = new Participant("Jane Smith");
        updatedData.addParticipant(newParticipant);

        // Update
        Reservation updated = reservationService.updateReservation(
                testReservation.getId(),
                testReservation.getPrivateKey(),
                updatedData
        );

        // Verify
        assertEquals("Updated Meeting", updated.getTitle());
        assertEquals("Conference Room B", updated.getLocation());
        assertEquals(102, updated.getRoomNumber());
        assertEquals(ReservationAccess.PRIVATE, updated.getAccessType());
        assertEquals("secret123", updated.getAccessCode());
        assertEquals(1, updated.getParticipants().size());
        assertEquals("Jane Smith", updated.getParticipants().get(0).getName());
    }

    @Test
    void testUpdateReservationWithInvalidKey() {
        Reservation updatedData = new Reservation();
        updatedData.setTitle("Updated Meeting");
        updatedData.setLocation("Conference Room B");
        updatedData.setRoomNumber(102);
        updatedData.setDescription("This is an updated description");
        updatedData.setStartTime(LocalDateTime.now().plusDays(2));
        updatedData.setEndTime(LocalDateTime.now().plusDays(2).plusHours(3));
        updatedData.setAccessType(ReservationAccess.PUBLIC);

        Participant newParticipant = new Participant("Jane Smith");
        updatedData.addParticipant(newParticipant);

        // Try to update with wrong key
        assertThrows(IllegalArgumentException.class, () -> {
            reservationService.updateReservation(
                    testReservation.getId(),
                    "wrong-key",
                    updatedData
            );
        });
    }

    @Test
    void testDeleteReservation() {
        Long id = testReservation.getId();
        String privateKey = testReservation.getPrivateKey();

        // Delete
        reservationService.deleteReservation(id, privateKey);

        // Verify deletion
        Optional<Reservation> found = reservationService.findById(id);
        assertFalse(found.isPresent());
    }

    @Test
    void testDeleteReservationWithInvalidKey() {
        Long id = testReservation.getId();

        // Try to delete with wrong key
        assertThrows(IllegalArgumentException.class, () -> {
            reservationService.deleteReservation(id, "wrong-key");
        });

        // Verify reservation still exists
        Optional<Reservation> found = reservationService.findById(id);
        assertTrue(found.isPresent());
    }

    @Test
    void testDeleteReservationNotFound() {
        assertThrows(IllegalArgumentException.class, () -> {
            reservationService.deleteReservation(999L, "any-key");
        });
    }

    @Test
    void testRoomConflictValidation() {
        // Create a reservation with same room and overlapping time
        Reservation conflicting = new Reservation();
        conflicting.setTitle("Conflicting Meeting");
        conflicting.setLocation("Conference Room A");
        conflicting.setRoomNumber(101); // Same room as testReservation
        conflicting.setDescription("This is a conflicting meeting description");
        // Overlapping time with testReservation
        conflicting.setStartTime(testReservation.getStartTime().plusMinutes(30));
        conflicting.setEndTime(testReservation.getEndTime().plusMinutes(30));
        conflicting.setAccessType(ReservationAccess.PUBLIC);

        Participant participant = new Participant("Alice Brown");
        conflicting.addParticipant(participant);

        // Should throw exception due to room conflict
        assertThrows(IllegalStateException.class, () -> {
            reservationService.createReservation(conflicting);
        });
    }

    @Test
    void testStartTimeInPastValidation() {
        Reservation pastReservation = new Reservation();
        pastReservation.setTitle("Past Meeting");
        pastReservation.setLocation("Conference Room A");
        pastReservation.setRoomNumber(103);
        pastReservation.setDescription("This meeting is in the past");
        pastReservation.setStartTime(LocalDateTime.now().minusDays(1)); // Past
        pastReservation.setEndTime(LocalDateTime.now().minusHours(22));
        pastReservation.setAccessType(ReservationAccess.PUBLIC);

        Participant participant = new Participant("Bob Wilson");
        pastReservation.addParticipant(participant);

        // Should throw exception
        assertThrows(IllegalArgumentException.class, () -> {
            reservationService.createReservation(pastReservation);
        });
    }

    @Test
    void testEndTimeBeforeStartTimeValidation() {
        Reservation invalidReservation = new Reservation();
        invalidReservation.setTitle("Invalid Meeting");
        invalidReservation.setLocation("Conference Room A");
        invalidReservation.setRoomNumber(103);
        invalidReservation.setDescription("Invalid time range reservation");
        LocalDateTime invalidStart = LocalDateTime.now().plusDays(1).withNano(0);
        invalidReservation.setStartTime(invalidStart);
        invalidReservation.setEndTime(invalidStart.minusHours(1)); // Before start
        invalidReservation.setAccessType(ReservationAccess.PUBLIC);

        Participant participant = new Participant("Charlie Davis");
        invalidReservation.addParticipant(participant);

        // Should throw exception
        assertThrows(IllegalArgumentException.class, () -> {
            reservationService.createReservation(invalidReservation);
        });
    }

    @Test
    void testPrivateReservationRequiresAccessCode() {
        Reservation privateReservation = new Reservation();
        privateReservation.setTitle("Private Meeting");
        privateReservation.setLocation("Conference Room A");
        privateReservation.setRoomNumber(104);
        privateReservation.setDescription("This is a private meeting");
        LocalDateTime privateStart = LocalDateTime.now().plusDays(3).withNano(0);
        privateReservation.setStartTime(privateStart);
        privateReservation.setEndTime(privateStart.plusHours(1));
        privateReservation.setAccessType(ReservationAccess.PRIVATE);
        // Missing access code

        Participant participant = new Participant("David Evans");
        privateReservation.addParticipant(participant);

        // Should throw exception
        assertThrows(IllegalArgumentException.class, () -> {
            reservationService.createReservation(privateReservation);
        });
    }

    @Test
    void testKeyGeneration() {
        // Keys should be unique
        Reservation reservation1 = createValidReservation(105, LocalDateTime.now().plusDays(5).withNano(0));
        Reservation reservation2 = createValidReservation(105, LocalDateTime.now().plusDays(10).withNano(0));

        assertNotEquals(reservation1.getPublicKey(), reservation2.getPublicKey());
        assertNotEquals(reservation1.getPrivateKey(), reservation2.getPrivateKey());
        assertNotEquals(reservation1.getPublicKey(), reservation1.getPrivateKey());
    }

    private Reservation createValidReservation(int roomNumber, LocalDateTime startTime) {
        Reservation reservation = new Reservation();
        reservation.setTitle("Test Meeting");
        reservation.setLocation("Conference Room");
        reservation.setRoomNumber(roomNumber);
        reservation.setDescription("Test description with enough characters");
        reservation.setStartTime(startTime);
        reservation.setEndTime(startTime.plusHours(1));
        reservation.setAccessType(ReservationAccess.PUBLIC);

        Participant participant = new Participant("Test User");
        reservation.addParticipant(participant);

        return reservationService.createReservation(reservation);
    }

    @Test
    void testReservationRequiresParticipants() {
        Reservation reservation = new Reservation();
        reservation.setTitle("Solo Meeting");
        reservation.setLocation("Conference Room");
        reservation.setRoomNumber(104);
        reservation.setDescription("Meeting without participants should fail");
        LocalDateTime startTime = LocalDateTime.now().plusDays(4).withNano(0);
        reservation.setStartTime(startTime);
        reservation.setEndTime(startTime.plusHours(1));
        reservation.setAccessType(ReservationAccess.PUBLIC);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> reservationService.createReservation(reservation));
        assertEquals("At least one participant is required", exception.getMessage());
    }

    @Test
    void testStartTimeMustBeInTheFuture() {
        Reservation reservation = new Reservation();
        reservation.setTitle("Immediate Meeting");
        reservation.setLocation("Conference Room");
        reservation.setRoomNumber(103);
        reservation.setDescription("Attempting to create a meeting starting immediately should fail");
        LocalDateTime startTime = LocalDateTime.now().withNano(0);
        reservation.setStartTime(startTime);
        reservation.setEndTime(startTime.plusHours(1));
        reservation.setAccessType(ReservationAccess.PUBLIC);
        reservation.addParticipant(new Participant("Immediate User"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> reservationService.createReservation(reservation));
        assertEquals("Start time must be in the future", exception.getMessage());
    }

    @Test
    void testParticipantNamesMustBeLetters() {
        Reservation reservation = new Reservation();
        reservation.setTitle("Invalid Name Meeting");
        reservation.setLocation("Conference Room");
        reservation.setRoomNumber(105);
        reservation.setDescription("Participant names must only contain letters and spaces");
        LocalDateTime startTime = LocalDateTime.now().plusDays(6).withNano(0);
        reservation.setStartTime(startTime);
        reservation.setEndTime(startTime.plusHours(1));
        reservation.setAccessType(ReservationAccess.PUBLIC);

        Participant participant = new Participant("John Doe1");
        reservation.addParticipant(participant);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> reservationService.createReservation(reservation));
        assertEquals("Participant names may only contain letters and spaces", exception.getMessage());
    }
}
