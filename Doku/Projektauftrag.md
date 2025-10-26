# Abschlussprojekt "Terminkalender"

## 1. Einleitung und Vorbereitung

Dieses Abschlussprojekt vertieft die Inhalte des **Moduls 223 – Multiuser-Applikationen objektorientiert realisieren**. Arbeiten Sie in Teams von drei Personen und halten Sie die folgenden Punkte ein:

* Legen Sie einen eindeutigen Projektnamen fest (z. B. *Reservationssystem*) und initialisieren Sie damit ein neues Spring-Boot-Projekt mit IntelliJ IDEA Ultimate oder über den [Spring Initializr](https://start.spring.io). Verwenden Sie Maven als Build-Tool. Die Abhängigkeiten **Spring Web (MVC)** und **Spring Data JPA** müssen in der `pom.xml` vorhanden sein.
* Ergänzen Sie die Standard-Projektstruktur (`src/main/java`, `src/main/resources`, …) um den Ordner `Doku`. In diesem Ordner liegt stets die aktuellste Projektdokumentation, die am Ende als PDF exportiert und abgegeben wird.
* Richten Sie eine strukturierte Ablage für Teilresultate ein (z. B. UML-Diagramme, ERM, SQL-Skripte). Versionieren Sie die Artefakte nachvollziehbar.
* Definieren Sie ein gemeinsames Repository (z. B. GitHub) für Ihr Team. Stellen Sie sicher, dass Quellcode und Dokumentation jederzeit in der aktuellsten Version vorhanden sind. Ergänzen Sie bei Bedarf einen Workflow für Branching, Reviews und Issues.

## 2. Ausgangslage

Das Unternehmen verfügt über mehrere Sitzungs- und Veranstaltungsräume (Zimmer **101–105**). Die bestehende Webapplikation soll die Verwaltung von Räumen und Terminen inklusive Reservationen unterstützen.

Auf der Hauptseite (`index.html`) können Besucherinnen und Besucher ohne Benutzerkonto über einen Link oder Button eine Reservation starten. Der Link **„Reservationen erfassen“** führt zu einem Formular, in dem die organisierende Person folgende Angaben macht:

| Feld | Typ | Format / Vorgaben |
| --- | --- | --- |
| Datum | `Date` | Format `TT.MM.JJJJ`, muss in der Zukunft liegen |
| Von | `Time` | Format `HH:MM`, muss vor "Bis" liegen |
| Bis | `Time` | Format `HH:MM`, muss nach "Von" liegen |
| Zimmer | `int` | Erlaubte Werte: 101, 102, 103, 104, 105 |
| Bemerkung | `String` | 10–200 alphanumerische Zeichen plus Satzzeichen |
| Teilnehmerliste | `String` | Eine oder mehrere Kombinationen aus Vor- und Nachnamen. Zeichenklasse `[A-Za-zÄÖÜäöüß]` plus Leerzeichen. Trennzeichen: Komma |

### Validierungsregeln

1. Kein Feld darf leer bleiben.
2. Das Datum muss heute oder in der Zukunft liegen; Vergangenheitsdaten werden abgelehnt.
3. Die Zeitspanne muss konsistent sein (`Von < Bis`).
4. Reservationen dürfen sich für dasselbe Zimmer nicht überschneiden. Vergleichen Sie dabei Datum sowie die Zeitfenster.
5. Die Teilnehmerliste muss mindestens eine Person enthalten. Entfernen Sie führende oder doppelte Leerzeichen und validieren Sie jede Person einzeln.

### Schlüsselverwaltung

Nach erfolgreicher Reservation erhält die organisierende Person eine Bestätigung mit zwei eindeutigen Schlüsseln (`publicKey` und `privateKey`).

* **Generierung:** Verwenden Sie kryptografisch sichere Zufallswerte (z. B. 16 Zeichen alphanumerisch).
* **Speicherung:** Persistieren Sie beide Schlüssel in der Datenbank und verknüpfen Sie sie mit der Reservation.
* **Verwendung:**
  * `privateKey`: ermöglicht Ändern oder Löschen der Reservation.
  * `publicKey`: ermöglicht Teilnehmerinnen und Teilnehmern das Einsehen der Reservation (z. B. per E-Mail versendeter Link).
* Auf der Startseite können beide Schlüssel in einem Textfeld eingegeben werden. Abhängig vom Schlüsseltyp leitet die Anwendung auf die Bearbeitungs- bzw. Einsichtsseite weiter.

Eine Übersicht aller Reservationen wird dauerhaft in der Datenbank gespeichert und kann – mindestens intern – eingesehen werden.

## 3. Aufgaben

1. **Projektdokumentation** erstellen, die alle Ergebnisse ausser dem Sourcecode umfasst. Legen Sie die finale PDF-Version im Ordner `Doku` ab.
2. **UML-Zustandsdiagramm** für die Navigation der Webapplikation modellieren. Zeichnen Sie alle Seiten als Zustände, Signale/Aktionen als Transitionen und berücksichtigen Sie Entscheidungen (z. B. gültiger/ungültiger Schlüssel). Nutzen Sie das im Unterricht verteilte Referenzbeispiel oder erstellen Sie eine gleichwertige Darstellung.
3. **ERM/ERD** für die Datenbank `Reservationen` mit allen Tabellen, Beziehungen und relevanten Attributen modellieren. Dokumentieren Sie Primär- und Fremdschlüssel sowie Kardinalitäten.
4. **UML-Klassendiagramm** für Controller- und Model-Schicht:
   * Erstellen Sie je Seite einen eigenen Controller.
   * Führen Sie alle relevanten Model-Klassen (z. B. `Reservation`, `Participant`, `Room`, `Invitation`).
   * Notieren Sie Attribute und Methoden (inkl. Sichtbarkeit) sowie Paketstrukturen.
5. Verwenden Sie die Diagramme als Basis für die Umsetzung. Fügen Sie sämtliche Diagramme in die Projektdokumentation ein.
6. Implementieren Sie die Model-Klassen. Legen Sie im Konstruktor oder per `DataInitializer` 2–3 Beispielreservationen mit je 1–2 Personen an.
7. Implementieren Sie die Views und Controller. Sie können entweder Thymeleaf oder React (inkl. API-Backend) nutzen. Dokumentieren Sie die Wahl in der Projektdokumentation.

## 4. Erweiterungen (optional)

Wählen Sie mindestens eine Erweiterung und dokumentieren Sie die Umsetzung:

* Kalenderansicht mit Tages-/Wochen-/Monatsfilter.
* E-Mail-Benachrichtigung mit dem Public Key für alle Teilnehmerinnen und Teilnehmer.
* Export der Reservationen als ICS-Datei.
* Verwaltung zusätzlicher Raumattribute (Kapazität, Ausstattung) inklusive Validierung.

## 5. Abgabe

* Laden Sie sämtliche Ergebnisse (Quellcode, Dokumentation, Artefakte) in das gemeinsame Repository hoch.
* Exportieren Sie die vollständige Dokumentation als PDF und legen Sie sie im Ordner `Doku` ab.
* Geben Sie das Projekt gebündelt als ZIP-Datei im vorgesehenen Teams-Kanal ab. Stellen Sie sicher, dass `README.md` und `pom.xml` auf dem neuesten Stand sind.
