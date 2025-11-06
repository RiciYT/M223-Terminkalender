-- =====================================================
-- M223 Terminkalender - Seed Daten Beispiel
-- =====================================================
-- Beispiel-SQL für die Testdaten, die normalerweise
-- über DataInitializer.java geladen werden
-- =====================================================

-- Hinweis: Die tatsächlichen Schlüssel (public_key, private_key)
-- werden zur Laufzeit generiert und sind hier nur Platzhalter

-- Reservierung 1: Team Sync Meeting
INSERT INTO reservations (
    title, 
    location, 
    room_number, 
    description, 
    start_time, 
    end_time, 
    access_type, 
    access_code,
    public_key,
    private_key
) VALUES (
    'Team Sync Meeting',
    'Conference Room A',
    101,
    'Weekly project status update for the product team.',
    DATE_ADD(NOW(), INTERVAL 1 DAY),
    DATE_ADD(DATE_ADD(NOW(), INTERVAL 1 DAY), INTERVAL 1 HOUR),
    'PUBLIC',
    NULL,
    'public-key-example-1',
    'private-key-example-1'
);

-- Teilnehmer für Reservierung 1
INSERT INTO participants (full_name, reservation_id) VALUES 
    ('Alice Johnson', LAST_INSERT_ID()),
    ('Bob Smith', LAST_INSERT_ID());

-- Reservierung 2: Client Demo Session
INSERT INTO reservations (
    title, 
    location, 
    room_number, 
    description, 
    start_time, 
    end_time, 
    access_type, 
    access_code,
    public_key,
    private_key
) VALUES (
    'Client Demo Session',
    'Online',
    102,
    'Demonstration of the latest product release for key clients.',
    DATE_ADD(NOW(), INTERVAL 2 DAY),
    DATE_ADD(DATE_ADD(NOW(), INTERVAL 2 DAY), INTERVAL 2 HOUR),
    'PRIVATE',
    'DEMO2024',
    'public-key-example-2',
    'private-key-example-2'
);

-- Teilnehmer für Reservierung 2
INSERT INTO participants (full_name, reservation_id) VALUES 
    ('Carol White', LAST_INSERT_ID()),
    ('David Brown', LAST_INSERT_ID());

-- Reservierung 3: Innovation Workshop
INSERT INTO reservations (
    title, 
    location, 
    room_number, 
    description, 
    start_time, 
    end_time, 
    access_type, 
    access_code,
    public_key,
    private_key
) VALUES (
    'Innovation Workshop',
    'Lab 2',
    103,
    'Hands-on workshop focusing on ideation and rapid prototyping.',
    DATE_ADD(NOW(), INTERVAL 5 DAY),
    DATE_ADD(DATE_ADD(NOW(), INTERVAL 5 DAY), INTERVAL 3 HOUR),
    'PUBLIC',
    NULL,
    'public-key-example-3',
    'private-key-example-3'
);

-- Teilnehmer für Reservierung 3
INSERT INTO participants (full_name, reservation_id) VALUES 
    ('Eve Black', LAST_INSERT_ID());

-- =====================================================
-- Abfrage-Beispiele
-- =====================================================

-- Alle Reservierungen anzeigen
-- SELECT * FROM reservations;

-- Reservierungen mit Teilnehmern
-- SELECT r.*, p.full_name 
-- FROM reservations r 
-- LEFT JOIN participants p ON r.id = p.reservation_id
-- ORDER BY r.start_time;

-- Verfügbare Zimmer zu bestimmter Zeit finden
-- SELECT DISTINCT room_number 
-- FROM reservations 
-- WHERE room_number BETWEEN 101 AND 105
-- AND (start_time < '2024-12-01 14:00:00' AND end_time > '2024-12-01 10:00:00');
