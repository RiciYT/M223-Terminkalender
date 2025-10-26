# M223 Terminkalender

Abschlussprojekt Modul 223 - Multiuser-Applikationen objektorientiert realisieren

## 📋 Übersicht

Reservationssystem für Sitzungs- und Veranstaltungsräume (Zimmer 101-105) mit vollständiger CRUD-Funktionalität und schlüsselbasierter Zugriffskontrolle.

## ✨ Features

### Kernfunktionalität
- ✅ **Reservierung erstellen**: Formular mit umfassender Validierung
- ✅ **Reservierung bearbeiten**: Mit Private Key autorisiert
- ✅ **Reservierung löschen**: Mit Confirmation Dialog
- ✅ **Reservierung anzeigen**: Public Key (Read-Only) oder Private Key (Full Access)

### Sicherheit
- 🔐 **Public Key**: Lesezugriff für Teilnehmer
- 🔑 **Private Key**: Vollzugriff zum Bearbeiten/Löschen
- 🔒 **Kryptographische Schlüsselgenerierung**: SecureRandom mit Base64 Encoding
- ✅ **Autorisierung**: Jeder Edit/Delete Request validiert Private Key

### Validierung
- ✅ Zimmernummer: 101-105
- ✅ Bemerkung: 10-200 Zeichen Pflicht
- ✅ Zeitbereich: Start < End, beide in Zukunft
- ✅ Zimmerkonfliktprüfung: Keine überlappenden Reservierungen
- ✅ Teilnehmer: Nur Buchstaben (inkl. Umlaute) erlaubt

## 🚀 Quick Start

### Voraussetzungen
- Java 17+
- Maven 3.6+

### Anwendung starten

```bash
./mvnw spring-boot:run
```

Die Anwendung läuft auf: http://localhost:8080

### Tests ausführen

```bash
./mvnw test
```

### Package erstellen

```bash
./mvnw clean package
java -jar target/reservation-0.0.1-SNAPSHOT.jar
```

## 🏗️ Technologie-Stack

- **Framework**: Spring Boot 3.4.2
- **Java Version**: 17
- **Build Tool**: Maven
- **Datenbank**: H2 (In-Memory)
- **Template Engine**: Thymeleaf
- **Persistenz**: Spring Data JPA
- **Validierung**: Jakarta Bean Validation
- **Testing**: JUnit 5

## 📂 Projektstruktur

```
M223-Terminkalender/
├── src/
│   ├── main/
│   │   ├── java/com/example/reservations/
│   │   │   ├── model/              # JPA Entities
│   │   │   ├── repository/         # Data Access Layer
│   │   │   ├── service/            # Business Logic
│   │   │   ├── web/                # Controllers
│   │   │   │   └── dto/            # Data Transfer Objects
│   │   │   └── config/             # Configuration
│   │   └── resources/
│   │       ├── templates/          # Thymeleaf Templates
│   │       └── application.properties
│   └── test/
│       └── java/                   # Unit Tests (14 Tests)
├── Doku/
│   ├── Projektdokumentation_M223.md  # Hauptdokumentation
│   ├── PDF_EXPORT_ANLEITUNG.md       # PDF Export Guide
│   └── diagrams/                     # UML Diagramme
│       ├── state-diagram.*           # Zustandsdiagramm
│       ├── erd-diagram.*             # Entity-Relationship
│       └── class-diagram.*           # Klassendiagramm
├── ABSCHLUSSBERICHT.md             # Implementierungsbericht
└── README.md                       # Diese Datei
```

## 🎯 API Endpoints

### Public Endpoints
- `GET /` - Startseite mit Reservationsübersicht
- `GET /access?key={key}` - Zugriff per Public/Private Key
- `GET /reservations/new` - Neues Reservationsformular
- `POST /reservations` - Reservation erstellen

### Protected Endpoints (Public Key)
- `GET /reservations/{id}/public` - Public View (Read-Only)

### Protected Endpoints (Private Key)
- `GET /reservations/{id}/private?key={privateKey}` - Private View
- `GET /reservations/{id}/edit?key={privateKey}` - Edit Formular
- `POST /reservations/{id}?key={privateKey}` - Reservation aktualisieren
- `POST /reservations/{id}/delete?key={privateKey}` - Reservation löschen

## 📚 Dokumentation

Die vollständige Projektdokumentation befindet sich in `Doku/Projektdokumentation_M223.md` und enthält:

1. Einleitung und Projektauftrag
2. Anforderungsanalyse
3. UML-Zustandsdiagramm
4. Entity-Relationship-Diagramm (ERD)
5. UML-Klassendiagramm
6. Implementierungsdetails
7. Testing und Validation
8. Code-Snippets

### PDF Export

Siehe `Doku/PDF_EXPORT_ANLEITUNG.md` für Anweisungen zum PDF-Export mit Pandoc, IntelliJ IDEA oder Online-Tools.

## 🧪 Testing

**Status**: ✅ Alle Tests bestanden (14/14)

```
Tests run: 14, Failures: 0, Errors: 0, Skipped: 0
```

**Test Coverage:**
- ✅ CRUD Operations (Create, Read, Update, Delete)
- ✅ Authorization (Valid/Invalid Private Key)
- ✅ Validation (Time, Room Conflict, Access Code)
- ✅ Key Generation (Uniqueness, Security)
- ✅ Error Handling (Not Found, Invalid Input)

## 🔑 Verwendung

### 1. Reservation erstellen

1. Navigiere zu http://localhost:8080
2. Klicke auf "Create Reservation"
3. Fülle das Formular aus:
   - Titel, Location, Zimmer (101-105)
   - Bemerkung (10-200 Zeichen)
   - Startzeit und Endzeit (in der Zukunft)
   - Teilnehmer (kommagetrennt)
   - Access Type (PUBLIC/PRIVATE)
4. Nach Erstellen erhältst du:
   - **Public Key**: Für Teilnehmer
   - **Private Key**: Für dich (Edit/Delete)

### 2. Reservation ansehen

**Mit Public Key:**
- Gib Public Key auf Startseite ein
- Zeigt Read-Only View

**Mit Private Key:**
- Gib Private Key auf Startseite ein
- Zeigt Management View mit Edit/Delete Buttons

### 3. Reservation bearbeiten

1. Öffne Private View mit Private Key
2. Klicke "Edit Reservation"
3. Ändere Daten im Formular
4. Speichere mit "Update Reservation"

### 4. Reservation löschen

1. Öffne Private View mit Private Key
2. Klicke "Delete Reservation"
3. Bestätige im Dialog
4. Reservation wird permanent gelöscht

## 👥 Team

Entwickelt für das Modul 223 - Multiuser-Applikationen objektorientiert realisieren

## 📄 Lizenz

Projekt für Bildungszwecke im Rahmen des Moduls 223.

## 🆘 Support

Bei Fragen oder Problemen:
1. Konsultiere die Dokumentation in `Doku/`
2. Prüfe den `ABSCHLUSSBERICHT.md`
3. Führe Tests aus: `./mvnw test`

## ✅ Status

**Projekt-Status**: ✅ ABGABEBEREIT

Alle Anforderungen des Projektauftrags wurden erfolgreich implementiert und getestet.

---

**Version**: 1.0  
**Datum**: Oktober 2025  
**Modul**: M223 – Multiuser-Applikationen objektorientiert realisieren

