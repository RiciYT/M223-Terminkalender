# SQL Dokumentation

Dieser Ordner enthält SQL-bezogene Dokumentation für das M223 Terminkalender Projekt.

## Datenbankstruktur

Die Anwendung verwendet JPA/Hibernate mit automatischer Schema-Generierung (`spring.jpa.hibernate.ddl-auto=update`).

### Generierte Tabellen

Die Anwendung erstellt automatisch folgende Tabellen:

1. **reservations** - Haupttabelle für Reservierungen
2. **participants** - Tabelle für Teilnehmer

## Schema-Dokumentation

Siehe `schema-example.sql` für die detaillierte Tabellenstruktur.

## Seed-Daten

Initiale Testdaten werden über die Klasse `DataInitializer.java` geladen.
Siehe `seed-data-example.sql` für SQL-Äquivalente der Seed-Daten.

## Konfiguration

Die Datenbankverbindung wird in `src/main/resources/application.properties` konfiguriert:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/reservations
spring.datasource.username=reservation_user
spring.datasource.password=change-me
spring.jpa.hibernate.ddl-auto=update
```

## Migrationen

Das Projekt nutzt Hibernate DDL Auto für Schema-Management.
Für Produktionsumgebungen wird empfohlen, auf Flyway oder Liquibase zu migrieren.
