# UML-Klassendiagramm - M223 Terminkalender

```mermaid
classDiagram
    class Reservation {
        -Long id
        -String title
        -String location
        -Integer roomNumber
        -String description
        -LocalDateTime startTime
        -LocalDateTime endTime
        -ReservationAccess accessType
        -String accessCode
        -String publicKey
        -String privateKey
        -List~Participant~ participants
        +getId() Long
        +getTitle() String
        +setTitle(String) void
        +getLocation() String
        +setLocation(String) void
        +getRoomNumber() Integer
        +setRoomNumber(Integer) void
        +getDescription() String
        +setDescription(String) void
        +getStartTime() LocalDateTime
        +setStartTime(LocalDateTime) void
        +getEndTime() LocalDateTime
        +setEndTime(LocalDateTime) void
        +getAccessType() ReservationAccess
        +setAccessType(ReservationAccess) void
        +getAccessCode() String
        +setAccessCode(String) void
        +getPublicKey() String
        +setPublicKey(String) void
        +getPrivateKey() String
        +setPrivateKey(String) void
        +getParticipants() List~Participant~
        +setParticipants(List~Participant~) void
        +addParticipant(Participant) void
    }
    
    class Participant {
        -Long id
        -String name
        -Reservation reservation
        +getId() Long
        +getName() String
        +setName(String) void
        +getReservation() Reservation
        +setReservation(Reservation) void
    }
    
    class ReservationAccess {
        <<enumeration>>
        PUBLIC
        PRIVATE
    }
    
    class ReservationRepository {
        <<interface>>
        +findByPublicKey(String) Optional~Reservation~
        +findByPrivateKey(String) Optional~Reservation~
        +existsByRoomNumberAndStartTimeLessThanAndEndTimeGreaterThan(Integer, LocalDateTime, LocalDateTime) boolean
        +existsByStartTimeLessThanAndEndTimeGreaterThan(LocalDateTime, LocalDateTime) boolean
        +findByAccessType(ReservationAccess) List~Reservation~
        +findByRoomNumber(Integer) List~Reservation~
    }
    
    class ReservationService {
        -ReservationRepository reservationRepository
        -SecureRandom secureRandom$
        +findAll() List~Reservation~
        +findById(Long) Optional~Reservation~
        +findByAccessType(ReservationAccess) List~Reservation~
        +createReservation(Reservation) Reservation
        +updateReservation(Long, String, Reservation) Reservation
        +deleteReservation(Long, String) void
        +findByPublicKey(String) Optional~Reservation~
        +findByPrivateKey(String) Optional~Reservation~
        -generateKeys(Reservation) void
        -generateSecureKey() String
        -validateReservation(Reservation) void
    }
    
    class ReservationController {
        -ReservationService reservationService
        +index(Model) String
        +accessByKey(String, Model) String
        +newReservation(Model) String
        +createReservation(ReservationForm, BindingResult) String
        +editReservation(Long, String, Model) String
        +updateReservation(Long, String, ReservationForm, BindingResult, Model) String
        +deleteReservation(Long, String, Model) String
        +confirmation(Long, Model) String
        +publicView(Long, Model) String
        +privateView(Long, String, Boolean, String, Model) String
        +accessTypes() ReservationAccess[]
    }
    
    class ReservationForm {
        -String title
        -String location
        -Integer roomNumber
        -String description
        -LocalDateTime startTime
        -LocalDateTime endTime
        -ReservationAccess accessType
        -String accessCode
        -String participantsText
        +toReservation() Reservation
        +fromReservation(Reservation)$ ReservationForm
        +getters and setters...
    }
    
    Reservation "1" *-- "*" Participant : contains
    Reservation --> ReservationAccess : uses
    ReservationRepository ..> Reservation : manages
    ReservationService --> ReservationRepository : uses
    ReservationService ..> Reservation : creates/updates
    ReservationController --> ReservationService : uses
    ReservationController ..> ReservationForm : uses
    ReservationForm ..> Reservation : converts
```

## Beschreibung

### Model Layer
- **Reservation**: Hauptentität für Terminreservierungen mit allen Validierungen
- **Participant**: Teilnehmer einer Reservation (Komposition-Beziehung)
- **ReservationAccess**: Enum für Zugangstypen (PUBLIC/PRIVATE)

### Repository Layer
- **ReservationRepository**: JPA Repository für Datenbankzugriff
  - Benutzerdefinierte Queries für Key-Lookup und Konfliktprüfung

### Service Layer
- **ReservationService**: Geschäftslogik
  - CRUD-Operationen (Create, Read, Update, Delete)
  - Key-Generierung (kryptographisch sicher mit SecureRandom)
  - Validierung (Zeitbereich, Raum-Konflikte, Zugangscode)

### Web Layer
- **ReservationController**: Spring MVC Controller
  - HTTP-Endpunkte für alle Operationen
  - Form-Handling und Validierung
  - Navigation und Zugriffskontrolle

### DTO Layer
- **ReservationForm**: Data Transfer Object
  - Formular-Binding für Thymeleaf
  - Conversion zwischen Entity und Form
  - Teilnehmer als kommaseparierter String
