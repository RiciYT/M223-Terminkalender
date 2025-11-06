-- =====================================================
-- M223 Terminkalender - Datenbankschema
-- =====================================================
-- Dieses Schema wird automatisch von Hibernate generiert
-- Hier als Dokumentation und Referenz
-- =====================================================

-- Tabelle für Reservierungen
CREATE TABLE reservations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    location VARCHAR(255) NOT NULL,
    room_number INT NOT NULL CHECK (room_number BETWEEN 101 AND 105),
    description VARCHAR(1000) NOT NULL CHECK (LENGTH(description) BETWEEN 10 AND 200),
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    access_type VARCHAR(20) NOT NULL CHECK (access_type IN ('PUBLIC', 'PRIVATE')),
    access_code VARCHAR(255),
    public_key VARCHAR(255) UNIQUE,
    private_key VARCHAR(255) UNIQUE,
    CONSTRAINT chk_time CHECK (end_time > start_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabelle für Teilnehmer
CREATE TABLE participants (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    reservation_id BIGINT NOT NULL,
    FOREIGN KEY (reservation_id) REFERENCES reservations(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Indizes für bessere Performance
CREATE INDEX idx_reservations_room_time ON reservations(room_number, start_time, end_time);
CREATE INDEX idx_reservations_public_key ON reservations(public_key);
CREATE INDEX idx_reservations_private_key ON reservations(private_key);
CREATE INDEX idx_participants_reservation ON participants(reservation_id);

-- =====================================================
-- Constraints und Validierungen
-- =====================================================

-- Zusätzliche Constraints könnten hier definiert werden
-- z.B. für Zimmerkonfliktprüfung (wird in der Anwendungslogik behandelt)

-- =====================================================
-- Hinweise
-- =====================================================

-- 1. room_number: Nur Werte 101-105 erlaubt
-- 2. description: 10-200 Zeichen Pflicht
-- 3. end_time muss nach start_time liegen
-- 4. public_key und private_key sind unique für Zugriffskontrolle
-- 5. Zimmerkonfliktprüfung erfolgt in der Service-Layer
