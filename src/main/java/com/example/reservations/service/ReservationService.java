package com.example.reservations.service;

import com.example.reservations.model.Participant;
import com.example.reservations.model.Reservation;
import com.example.reservations.model.ReservationAccess;
import com.example.reservations.repository.ReservationRepository;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Pattern PARTICIPANT_NAME_PATTERN =
            Pattern.compile("^[A-Za-zÄÖÜäöüß]+(?:\\s+[A-Za-zÄÖÜäöüß]+)*$");

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
    public Reservation createReservation(Reservation reservation) {
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
        validateReservation(reservation, null);
    }

    private void validateReservation(Reservation reservation, Long excludeId) {
        LocalDateTime start = reservation.getStartTime();
        LocalDateTime end = reservation.getEndTime();
        LocalDateTime now = LocalDateTime.now();

        if (start == null || end == null) {
            throw new IllegalArgumentException("Start- und Endzeit sind erforderlich");
        }

        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("Endzeit muss nach der Startzeit liegen");
        }

        if (!start.isAfter(now)) {
            throw new IllegalArgumentException("Startzeit muss in der Zukunft liegen");
        }

        if (!end.isAfter(now)) {
            throw new IllegalArgumentException("Endzeit muss in der Zukunft liegen");
        }

        Integer roomNumber = reservation.getRoomNumber();
        if (roomNumber != null) {
            List<Reservation> sameRoomReservations = reservationRepository.findByRoomNumber(roomNumber);
            boolean conflict = sameRoomReservations.stream()
                    .filter(existing -> excludeId == null || !excludeId.equals(existing.getId()))
                    .anyMatch(existing -> start.isBefore(existing.getEndTime()) && end.isAfter(existing.getStartTime()));

            if (conflict) {
                throw new IllegalStateException("Der ausgewählte Raum und Zeitslot kollidiert mit einer bestehenden Reservierung");
            }
        }

        if (reservation.getAccessType() == ReservationAccess.PRIVATE) {
            String accessCode = reservation.getAccessCode();
            if (accessCode == null || accessCode.isBlank()) {
                throw new IllegalArgumentException("Private Reservierungen erfordern einen Zugangscode");
            }
        }

        if (reservation.getParticipants() == null || reservation.getParticipants().isEmpty()) {
            throw new IllegalArgumentException("Mindestens ein Teilnehmer ist erforderlich");
        }

        for (Participant participant : reservation.getParticipants()) {
            String rawName = participant.getName() == null ? "" : participant.getName().trim();
            if (rawName.isEmpty()) {
                throw new IllegalArgumentException("Teilnehmernamen dürfen nur Buchstaben und Leerzeichen enthalten");
            }
            if (!PARTICIPANT_NAME_PATTERN.matcher(rawName).matches()) {
                throw new IllegalArgumentException("Teilnehmernamen dürfen nur Buchstaben und Leerzeichen enthalten");
            }
            participant.setName(rawName);
            participant.setReservation(reservation);
        }
    }

    public Optional<Reservation> findByPublicKey(String publicKey) {
        return reservationRepository.findByPublicKey(publicKey);
    }

    public Optional<Reservation> findByPrivateKey(String privateKey) {
        return reservationRepository.findByPrivateKey(privateKey);
    }

    @Transactional
    public Reservation updateReservation(Long id, String privateKey, Reservation updatedData) {
        Reservation existing = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservierung nicht gefunden"));

        // Autorisierung
        if (!privateKey.equals(existing.getPrivateKey())) {
            throw new IllegalArgumentException("Ungültiger privater Schlüssel");
        }

        // Daten aktualisieren
        existing.setTitle(updatedData.getTitle());
        existing.setLocation(updatedData.getLocation());
        existing.setRoomNumber(updatedData.getRoomNumber());
        existing.setDescription(updatedData.getDescription());
        existing.setStartTime(updatedData.getStartTime());
        existing.setEndTime(updatedData.getEndTime());
        existing.setAccessType(updatedData.getAccessType());
        existing.setAccessCode(updatedData.getAccessCode());

        // Teilnehmer ersetzen
        existing.getParticipants().clear();
        updatedData.getParticipants().forEach(existing::addParticipant);

        // Erneut validieren (exclude current reservation from conflict check)
        validateReservation(existing, id);
        return reservationRepository.save(existing);
    }

    @Transactional
    public void deleteReservation(Long id, String privateKey) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservierung nicht gefunden"));

        if (!privateKey.equals(reservation.getPrivateKey())) {
            throw new IllegalArgumentException("Ungültiger privater Schlüssel");
        }

        reservationRepository.delete(reservation);
    }
}
