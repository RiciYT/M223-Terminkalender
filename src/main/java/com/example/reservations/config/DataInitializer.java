package com.example.reservations.config;

import com.example.reservations.model.Participant;
import com.example.reservations.model.Reservation;
import com.example.reservations.model.ReservationAccess;
import com.example.reservations.repository.ReservationRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    CommandLineRunner seedReservations(ReservationRepository reservationRepository) {
        return args -> {
            if (reservationRepository.count() > 0) {
                log.info("Skipping reservation seeding because records already exist");
                return;
            }

            Reservation teamMeeting = new Reservation();
            teamMeeting.setTitle("Team Sync Meeting");
            teamMeeting.setLocation("Conference Room A");
            teamMeeting.setDescription("Weekly project status update for the product team.");
            teamMeeting.setStartTime(LocalDateTime.now().plusDays(1).withHour(9).withMinute(0).withSecond(0).withNano(0));
            teamMeeting.setEndTime(teamMeeting.getStartTime().plusHours(1));
            teamMeeting.setAccessType(ReservationAccess.PUBLIC);
            teamMeeting.setParticipants(List.of(
                    new Participant("Alice Johnson", "alice@example.com"),
                    new Participant("Bob Smith", "bob@example.com")));

            Reservation clientDemo = new Reservation();
            clientDemo.setTitle("Client Demo Session");
            clientDemo.setLocation("Online");
            clientDemo.setDescription("Demonstration of the latest product release for key clients.");
            clientDemo.setStartTime(LocalDateTime.now().plusDays(2).withHour(14).withMinute(30).withSecond(0).withNano(0));
            clientDemo.setEndTime(clientDemo.getStartTime().plusHours(2));
            clientDemo.setAccessType(ReservationAccess.PRIVATE);
            clientDemo.setAccessCode("DEMO2024");
            clientDemo.setParticipants(List.of(
                    new Participant("Carol White", "carol@example.com"),
                    new Participant("David Brown", "david@example.com")));

            Reservation workshop = new Reservation();
            workshop.setTitle("Innovation Workshop");
            workshop.setLocation("Lab 2");
            workshop.setDescription("Hands-on workshop focusing on ideation and rapid prototyping.");
            workshop.setStartTime(LocalDateTime.now().plusDays(5).withHour(11).withMinute(0).withSecond(0).withNano(0));
            workshop.setEndTime(workshop.getStartTime().plusHours(3));
            workshop.setAccessType(ReservationAccess.PUBLIC);
            workshop.setParticipants(List.of(
                    new Participant("Eve Black", "eve@example.com")));

            reservationRepository.saveAll(List.of(teamMeeting, clientDemo, workshop));
            log.info("Seeded {} reservations", reservationRepository.count());
        };
    }
}
