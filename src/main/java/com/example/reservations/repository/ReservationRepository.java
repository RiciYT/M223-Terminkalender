package com.example.reservations.repository;

import com.example.reservations.model.Reservation;
import com.example.reservations.model.ReservationAccess;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    boolean existsByStartTimeLessThanAndEndTimeGreaterThan(LocalDateTime endTime, LocalDateTime startTime);

    List<Reservation> findByAccessType(ReservationAccess accessType);
}
