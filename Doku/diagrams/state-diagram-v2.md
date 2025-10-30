# Zustandsdiagramm Version 2 - M223 Terminkalender Navigation

## Detaillierte Version mit Composite States

```mermaid
stateDiagram-v2
    [*] --> Startseite
    
    state Startseite {
        [*] --> Übersicht
        Übersicht: Reservationsliste
        Übersicht: - Alle Reservationen anzeigen
        Übersicht: - Schlüsseleingabe-Formular
        Übersicht: - Link "Create Reservation"
    }
    
    state ReservationsFormular {
        [*] --> Eingabe
        Eingabe: Neue Reservation erstellen
        Eingabe: - Titel, Location, Zimmer (101-105)
        Eingabe: - Startzeit, Endzeit
        Eingabe: - Teilnehmer (kommagetrennt)
        Eingabe: - Access Type (PUBLIC/PRIVATE)
        Eingabe --> Validierung : Submit
        Validierung --> Eingabe : Fehler
        Validierung --> [*] : Erfolgreich
    }
    
    state Bestätigung {
        [*] --> KeyAnzeige
        KeyAnzeige: Schlüssel anzeigen
        KeyAnzeige: - Public Key (für Teilnehmer)
        KeyAnzeige: - Private Key (für Verwaltung)
        KeyAnzeige: - Links zu Public/Private View
    }
    
    state SchlüsselZugriff {
        [*] --> KeyPrüfung
        KeyPrüfung: /access?key={key}
        KeyPrüfung --> PublicKeyRoute : Public Key erkannt
        KeyPrüfung --> PrivateKeyRoute : Private Key erkannt
        KeyPrüfung --> Fehler : Ungültiger Key
    }
    
    state ÖffentlicheAnsicht {
        [*] --> ReadOnly
        ReadOnly: Public View
        ReadOnly: - Reservationsdetails anzeigen
        ReadOnly: - Teilnehmerliste
        ReadOnly: - Keine Bearbeitungsfunktionen
    }
    
    state PrivateAnsicht {
        [*] --> Management
        Management: Private View (mit Verwaltung)
        Management: - Alle Details anzeigen
        Management: - Edit Button
        Management: - Delete Button (mit Bestätigung)
    }
    
    state EditFormular {
        [*] --> Bearbeitung
        Bearbeitung: Reservation bearbeiten
        Bearbeitung: - Vorausgefülltes Formular
        Bearbeitung: - Alle Felder editierbar
        Bearbeitung --> Validation : Update
        Validation --> Bearbeitung : Fehler
        Validation --> [*] : Erfolgreich
    }
    
    Startseite --> ReservationsFormular : "Create Reservation" klicken
    Startseite --> SchlüsselZugriff : Key eingeben
    
    ReservationsFormular --> Bestätigung : Reservation erfolgreich erstellt
    
    Bestätigung --> Startseite : "Back to overview"
    Bestätigung --> ÖffentlicheAnsicht : "Open public page"
    Bestätigung --> PrivateAnsicht : "Open private page"
    
    SchlüsselZugriff --> ÖffentlicheAnsicht : Route zu Public View
    SchlüsselZugriff --> PrivateAnsicht : Route zu Private View
    SchlüsselZugriff --> Startseite : Fehlermeldung
    
    ÖffentlicheAnsicht --> Startseite : "Back" klicken
    
    PrivateAnsicht --> EditFormular : "Edit" klicken
    PrivateAnsicht --> Startseite : "Delete" (nach Bestätigung)
    PrivateAnsicht --> Startseite : "Back" klicken
    
    EditFormular --> Bestätigung : Update erfolgreich
    EditFormular --> PrivateAnsicht : "Cancel"
    
    note right of SchlüsselZugriff
        Automatisches Routing:
        Public Key → Read-only Ansicht
        Private Key → Management Ansicht
        Ungültig → Fehlermeldung
    end note
    
    note right of PrivateAnsicht
        Nur zugänglich mit:
        - Private Key (volle Verwaltung)
        Funktionen: Edit, Delete
    end note
    
    note right of Bestätigung
        Zeigt beide Keys nach Erstellung:
        Public + Private
        Mit direkten Links
    end note
```

## Beschreibung der Zustände

### Hauptzustände:

1. **Startseite** (`/`)
   - Übersicht aller Reservationen in Tabellenform
   - Schlüsseleingabe-Formular für Zugriff
   - Button "Create Reservation"

2. **ReservationsFormular** (`/reservations/new`)
   - Eingabefelder: Titel, Location, Zimmer (101-105), Beschreibung
   - Datum/Zeit: Startzeit und Endzeit (muss in Zukunft liegen)
   - Teilnehmer: Kommagetrennte Liste
   - Access Type: PUBLIC oder PRIVATE
   - Validierung: Client- und Server-seitig

3. **Bestätigung** (`/reservations/{id}/confirm`)
   - Zeigt beide generierten Schlüssel (16 Zeichen, kryptografisch sicher)
   - Public Key: Für Teilnehmer (Read-Only)
   - Private Key: Für Ersteller (Edit/Delete)
   - Direkte Links zu beiden Views

4. **SchlüsselZugriff** (`/access?key={key}`)
   - Automatische Key-Typ-Erkennung
   - Routing basierend auf Key-Typ
   - Fehlerbehandlung bei ungültigen Keys

5. **ÖffentlicheAnsicht** (`/reservations/{id}/public`)
   - Read-Only Ansicht für Teilnehmer
   - Zeigt: Titel, Location, Zimmer, Zeiten, Teilnehmer
   - Keine Bearbeitungsfunktionen

6. **PrivateAnsicht** (`/reservations/{id}/private`)
   - Volle Verwaltungsrechte mit Private Key
   - Edit Button: Öffnet vorausgefülltes Formular
   - Delete Button: Mit Bestätigungsdialog

7. **EditFormular** (`/reservations/{id}/edit`)
   - Vorausgefülltes Formular mit allen aktuellen Werten
   - Alle Felder editierbar (außer Keys)
   - Validierung wie bei Erstellung
   - Private Key erforderlich für Zugriff

## Routing-Logik

```
/access?key={key} Endpoint:
├── Key-Typ prüfen
├── Public Key → redirect zu /reservations/{id}/public
├── Private Key → redirect zu /reservations/{id}/private?authorized=true&key={key}
└── Ungültig → redirect zu / mit Fehlermeldung
```

## Unterschiede zur Original-Version:

✅ **Composite States**: Verschachtelte Zustände mit Unterzuständen  
✅ **Detaillierte Beschreibungen**: Jeder Zustand zeigt seine Funktionen  
✅ **Klarere Übergänge**: Aktionen sind explizit beschriftet  
✅ **Routing-Dokumentation**: Technische Details des /access Endpoints  
✅ **Stil ähnlich zum Referenzbild**: Farbige Bereiche mit strukturierten Inhalten