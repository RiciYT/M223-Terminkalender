package com.example.reservations.service;

import com.example.reservations.model.Participant;
import com.example.reservations.model.Reservation;
import com.example.reservations.model.ReservationAccess;
import com.example.reservations.repository.ReservationRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;

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
        return reservationRepository.save(reservation);
    }

    private void validateReservation(Reservation reservation) {
        LocalDateTime start = reservation.getStartTime();
        LocalDateTime end = reservation.getEndTime();

        if (start == null || end == null || !end.isAfter(start)) {
            throw new IllegalArgumentException("End time must be after the start time");
        }

        boolean conflict = reservationRepository.existsByStartTimeLessThanAndEndTimeGreaterThan(end, start);
        if (conflict) {
            throw new IllegalStateException("The selected time slot conflicts with an existing reservation");
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
}
