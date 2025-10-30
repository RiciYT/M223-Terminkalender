# Docker Compose Setup für MySQL Datenbank

Dieses Projekt enthält eine vorkonfigurierte Docker Compose Setup für eine MySQL 9.5 Datenbank.

## 📋 Übersicht

Die Docker Compose Konfiguration stellt eine MySQL Datenbank bereit, die von der Spring Boot Anwendung verwendet werden kann. Die Konfiguration ist production-ready und umfasst:

- MySQL 9.5 Server
- Persistente Datenspeicherung mit Docker Volumes
- Health Checks für Container-Überwachung
- Konfigurierbare Umgebungsvariablen
- Automatische Datenbank-Initialisierung

## 🚀 Schnellstart

### 1. Umgebungsvariablen konfigurieren (Optional)

```bash
cp .env.example .env
```

Bearbeite die `.env` Datei nach Bedarf:

```env
MYSQL_ROOT_PASSWORD=rootpassword
MYSQL_DATABASE=reservations
MYSQL_USER=reservation_user
MYSQL_PASSWORD=change-me
MYSQL_PORT=3306
```

**Hinweis**: Die `.env` Datei ist in `.gitignore` und wird nicht ins Repository commitet.

### 2. MySQL Container starten

```bash
docker compose up -d
```

Dieser Befehl:
- Lädt das MySQL 9.5 Image herunter (beim ersten Mal)
- Startet den Container im Hintergrund (`-d` flag)
- Erstellt die Datenbank `reservations` automatisch
- Erstellt den Benutzer `reservation_user`
- Führt `init.sql` Script aus

### 3. Container Status prüfen

```bash
# Status aller Container anzeigen
docker compose ps

# Logs anzeigen
docker compose logs mysql

# Live Logs folgen
docker compose logs -f mysql
```

### 4. Spring Boot Anwendung starten

Die Anwendung ist bereits für MySQL konfiguriert (`src/main/resources/application.properties`):

```bash
./mvnw spring-boot:run
```

Die Anwendung verbindet sich automatisch mit der MySQL Datenbank auf `localhost:3306`.

## 🔧 Konfiguration

### Docker Compose Services

#### MySQL Service
- **Image**: mysql:9.5.0
- **Container Name**: reservations-mysql
- **Port**: 3306 (konfigurierbar via `MYSQL_PORT`)
- **Restart Policy**: unless-stopped
- **Health Check**: Prüft MySQL Verfügbarkeit alle 10 Sekunden

### Umgebungsvariablen

| Variable | Standard | Beschreibung |
|----------|----------|--------------|
| `MYSQL_ROOT_PASSWORD` | rootpassword | Root Passwort für MySQL |
| `MYSQL_DATABASE` | reservations | Name der Datenbank |
| `MYSQL_USER` | reservation_user | Datenbank Benutzer |
| `MYSQL_PASSWORD` | change-me | Passwort für Benutzer |
| `MYSQL_PORT` | 3306 | Externer Port |

### Volumes

- **mysql_data**: Persistente Speicherung der Datenbankdaten
- **init.sql**: Initialisierungsskript (gemountet als read-only)

### Netzwerk

- **reservation-network**: Bridge-Netzwerk für Service-Kommunikation

## 📝 Nützliche Befehle

### Container Management

```bash
# Container starten
docker compose up -d

# Container stoppen
docker compose down

# Container stoppen und alle Daten löschen (VORSICHT!)
docker compose down -v

# Container neu starten
docker compose restart

# Container Status
docker compose ps
```

### Logs und Debugging

```bash
# Alle Logs anzeigen
docker compose logs

# Logs eines Services
docker compose logs mysql

# Live Logs folgen
docker compose logs -f mysql

# Letzte 100 Zeilen
docker compose logs --tail=100 mysql
```

### Datenbank Zugriff

```bash
# MySQL Console öffnen
docker compose exec mysql mysql -u reservation_user -p reservations

# Als Root
docker compose exec mysql mysql -u root -p

# SQL Datei ausführen
docker compose exec -T mysql mysql -u reservation_user -p reservations < backup.sql

# Datenbank Backup erstellen
docker compose exec mysql mysqldump -u reservation_user -p reservations > backup.sql
```

### Container Informationen

```bash
# Container Ressourcen-Nutzung
docker stats reservations-mysql

# Container Details
docker inspect reservations-mysql

# Container Netzwerk inspizieren
docker network inspect m223-terminkalender_reservation-network
```

## 🔍 Troubleshooting

### Container startet nicht

```bash
# Logs prüfen
docker compose logs mysql

# Container entfernen und neu erstellen
docker compose down
docker compose up -d
```

### Verbindungsfehler von Spring Boot

1. **Prüfe ob Container läuft**:
   ```bash
   docker compose ps
   ```

2. **Prüfe Health Status**:
   ```bash
   docker inspect reservations-mysql | grep -i health
   ```

3. **Prüfe application.properties**:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/reservations
   spring.datasource.username=reservation_user
   spring.datasource.password=change-me
   ```

4. **Teste MySQL Verbindung**:
   ```bash
   docker compose exec mysql mysql -u reservation_user -p -e "SELECT 1;"
   ```

### Port bereits belegt

Wenn Port 3306 bereits verwendet wird, ändere in `.env`:

```env
MYSQL_PORT=3307
```

Und aktualisiere `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3307/reservations
```

### Datenbank zurücksetzen

```bash
# VORSICHT: Löscht alle Daten!
docker compose down -v
docker compose up -d
```

## 🔐 Sicherheit

### Produktions-Deployment

Für Produktionsumgebungen solltest du:

1. **Starke Passwörter verwenden**:
   ```env
   MYSQL_ROOT_PASSWORD=<stark-zufällig-generiert>
   MYSQL_PASSWORD=<stark-zufällig-generiert>
   ```

2. **Keine Default-Ports verwenden**:
   ```env
   MYSQL_PORT=33061
   ```

3. **Secrets Management nutzen** (z.B. Docker Secrets, Vault)

4. **Netzwerk-Isolation konfigurieren**

5. **Regular Backups einrichten**

## 📦 Docker Compose File Struktur

```yaml
version: '3.8'

services:
  mysql:                          # Service Name
    image: mysql:9.5.0            # MySQL Version
    container_name: ...           # Container Name
    restart: unless-stopped       # Restart Policy
    environment:                  # Umgebungsvariablen
      MYSQL_ROOT_PASSWORD: ...
      MYSQL_DATABASE: ...
      MYSQL_USER: ...
      MYSQL_PASSWORD: ...
    ports:                        # Port Mapping
      - "3306:3306"
    volumes:                      # Persistent Storage
      - mysql_data:/var/lib/mysql
      - ./init.sql:/docker-entrypoint-initdb.d/init.sql
    networks:                     # Netzwerk
      - reservation-network
    healthcheck:                  # Health Check
      test: [...]
      interval: 10s
      timeout: 5s
      retries: 5

volumes:
  mysql_data:                     # Named Volume

networks:
  reservation-network:            # Custom Network
```

## 🧪 Tests

Die Tests der Anwendung verwenden H2 In-Memory Datenbank (`src/test/resources/application.properties`), um unabhängig von Docker zu sein.

```bash
# Tests ausführen (verwendet H2, nicht MySQL)
./mvnw test
```

## 📚 Weitere Informationen

- [Docker Compose Dokumentation](https://docs.docker.com/compose/)
- [MySQL Docker Hub](https://hub.docker.com/_/mysql)
- [Spring Boot mit Docker](https://spring.io/guides/gs/spring-boot-docker/)

## ❓ FAQ

**Q: Warum MySQL 9.5?**  
A: Version entspricht der in `pom.xml` spezifizierten MySQL Connector Version (9.5.0).

**Q: Kann ich eine andere MySQL Version verwenden?**  
A: Ja, ändere in `docker compose.yml`: `image: mysql:8.0` oder `mysql:9.1`.

**Q: Wo werden die Daten gespeichert?**  
A: In einem Docker Volume `mysql_data`. Daten bleiben auch nach `docker compose down` erhalten.

**Q: Wie kann ich die Datenbank zurücksetzen?**  
A: `docker compose down -v` löscht alle Volumes inklusive Daten.

**Q: Funktioniert das auf Windows/Mac?**  
A: Ja, Docker Compose funktioniert auf allen Betriebssystemen mit Docker Desktop.

---

**Version**: 1.0  
**Stand**: Oktober 2024
