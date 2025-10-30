-- Initialize the reservations database
-- This script runs automatically when the MySQL container starts for the first time

-- Ensure we're using the correct database
USE reservations;

-- The application will create tables automatically using JPA (spring.jpa.hibernate.ddl-auto=update)
-- This file can be used for any additional initialization if needed

-- Example: Create an index for performance (optional)
-- CREATE INDEX idx_reservation_room ON reservation(room_number);
