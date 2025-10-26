package com.example.reservations.service;

import com.example.reservations.model.Participant;
import com.example.reservations.model.Reservation;
import com.example.reservations.model.ReservationAccess;
import com.example.reservations.repository.ReservationRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private static final SecureRandom secureRandom = new SecureRandom();

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public List<Reservation> findAll() {
        return reservationRepository.findAll(Sort.by("startTime"));
    }

    public Optional<Reservation> findById(Long id) {
        return reservationRepository.findById(id);
    }

    public List<Reservation> findByAccessType(ReservationAccess accessType) {
        return reservationRepository.findByAccessType(accessType);
    }

    @Transactional
    public Reservation createReservation(@Valid @NotNull Reservation reservation) {
        validateReservation(reservation);
        generateKeys(reservation);
        return reservationRepository.save(reservation);
    }

    private void generateKeys(Reservation reservation) {
        reservation.setPublicKey(generateSecureKey());
        reservation.setPrivateKey(generateSecureKey());
    }

    private String generateSecureKey() {
        byte[] randomBytes = new byte[12];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    private void validateReservation(Reservation reservation) {
        LocalDateTime start = reservation.getStartTime();
        LocalDateTime end = reservation.getEndTime();

        if (start == null || end == null || !end.isAfter(start)) {
            throw new IllegalArgumentException("End time must be after the start time");
        }

        if (start.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Start time must be in the future");
        }

        Integer roomNumber = reservation.getRoomNumber();
        if (roomNumber != null) {
            boolean conflict = reservationRepository.existsByRoomNumberAndStartTimeLessThanAndEndTimeGreaterThan(
                    roomNumber, end, start);
            if (conflict) {
                throw new IllegalStateException("The selected room and time slot conflicts with an existing reservation");
            }
        }

        if (reservation.getAccessType() == ReservationAccess.PRIVATE) {
            String accessCode = reservation.getAccessCode();
            if (accessCode == null || accessCode.isBlank()) {
                throw new IllegalArgumentException("Private reservations require an access code");
            }
        }

        for (Participant participant : reservation.getParticipants()) {
            participant.setReservation(reservation);
        }
    }

    public Optional<Reservation> findByPublicKey(String publicKey) {
        return reservationRepository.findByPublicKey(publicKey);
    }

    public Optional<Reservation> findByPrivateKey(String privateKey) {
        return reservationRepository.findByPrivateKey(privateKey);
    }
}
