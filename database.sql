-- ============================================================================
-- TRANSITX: Smart Campus Transportation and Bus Tracking Management System
-- Database Setup Script for MySQL
-- Database: campus_bus_tracking
-- ============================================================================

CREATE DATABASE IF NOT EXISTS campus_bus_tracking CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE campus_bus_tracking;

-- ----------------------------------------------------------------------------
-- 1. Table: users
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL, -- ROLE_ADMIN, ROLE_STUDENT, ROLE_DRIVER
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 2. Table: buses
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS buses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bus_number VARCHAR(50) NOT NULL UNIQUE,
    capacity INT NOT NULL DEFAULT 40,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, MAINTENANCE, INACTIVE
    model_plate VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 3. Table: routes
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS routes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    route_name VARCHAR(100) NOT NULL,
    route_code VARCHAR(50) NOT NULL UNIQUE,
    start_point VARCHAR(100) NOT NULL,
    end_point VARCHAR(100) NOT NULL,
    departure_time VARCHAR(20) NOT NULL,
    arrival_time VARCHAR(20) NOT NULL,
    est_duration VARCHAR(50) NOT NULL,
    distance_km DECIMAL(6,2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 4. Table: bus_stops
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS bus_stops (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    stop_name VARCHAR(100) NOT NULL,
    latitude DECIMAL(10,7) NOT NULL,
    longitude DECIMAL(10,7) NOT NULL,
    landmark VARCHAR(150),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 5. Table: route_stops
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS route_stops (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    route_id BIGINT NOT NULL,
    stop_id BIGINT NOT NULL,
    stop_sequence INT NOT NULL,
    pickup_time VARCHAR(20) NOT NULL,
    FOREIGN KEY (route_id) REFERENCES routes(id) ON DELETE CASCADE,
    FOREIGN KEY (stop_id) REFERENCES bus_stops(id) ON DELETE CASCADE,
    UNIQUE KEY uk_route_sequence (route_id, stop_sequence)
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 6. Table: drivers
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS drivers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNIQUE,
    driver_code VARCHAR(50) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    license_no VARCHAR(50) NOT NULL,
    experience_years INT DEFAULT 5,
    assigned_bus_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (assigned_bus_id) REFERENCES buses(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 7. Table: students
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS students (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNIQUE,
    reg_no VARCHAR(50) NOT NULL UNIQUE,
    department VARCHAR(100) NOT NULL,
    year_of_study VARCHAR(20) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL,
    assigned_bus_id BIGINT,
    assigned_route_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (assigned_bus_id) REFERENCES buses(id) ON DELETE SET NULL,
    FOREIGN KEY (assigned_route_id) REFERENCES routes(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 8. Table: bus_assignments
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS bus_assignments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bus_id BIGINT NOT NULL,
    driver_id BIGINT NOT NULL,
    route_id BIGINT NOT NULL,
    assigned_date DATE NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (bus_id) REFERENCES buses(id) ON DELETE CASCADE,
    FOREIGN KEY (driver_id) REFERENCES drivers(id) ON DELETE CASCADE,
    FOREIGN KEY (route_id) REFERENCES routes(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 9. Table: student_bus_pass
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS student_bus_pass (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pass_code VARCHAR(64) NOT NULL UNIQUE,
    student_id BIGINT NOT NULL,
    route_id BIGINT NOT NULL,
    bus_id BIGINT,
    boarding_stop_id BIGINT NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING', -- PENDING, APPROVED, REJECTED
    validity_start DATE,
    validity_end DATE,
    id_proof_path VARCHAR(255),
    qr_code_data VARCHAR(255),
    rejection_reason VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    FOREIGN KEY (route_id) REFERENCES routes(id) ON DELETE RESTRICT,
    FOREIGN KEY (bus_id) REFERENCES buses(id) ON DELETE SET NULL,
    FOREIGN KEY (boarding_stop_id) REFERENCES bus_stops(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 10. Table: bus_locations
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS bus_locations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bus_id BIGINT NOT NULL UNIQUE,
    latitude DECIMAL(10,7) NOT NULL,
    longitude DECIMAL(10,7) NOT NULL,
    speed DECIMAL(5,2) DEFAULT 0.00,
    status VARCHAR(30) DEFAULT 'NOT_STARTED', -- NOT_STARTED, ON_ROUTE, DELAYED, REACHED_DESTINATION
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_simulated BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (bus_id) REFERENCES buses(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 11. Table: trips
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS trips (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bus_id BIGINT NOT NULL,
    driver_id BIGINT NOT NULL,
    route_id BIGINT NOT NULL,
    start_time TIMESTAMP NULL,
    end_time TIMESTAMP NULL,
    trip_status VARCHAR(30) DEFAULT 'NOT_STARTED', -- NOT_STARTED, ON_ROUTE, DELAYED, COMPLETED, CANCELLED
    current_stop_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (bus_id) REFERENCES buses(id) ON DELETE CASCADE,
    FOREIGN KEY (driver_id) REFERENCES drivers(id) ON DELETE CASCADE,
    FOREIGN KEY (route_id) REFERENCES routes(id) ON DELETE CASCADE,
    FOREIGN KEY (current_stop_id) REFERENCES bus_stops(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ============================================================================
-- INITIAL SEED DATA FOR DEMONSTRATION & COLLEGE REVIEW
-- ============================================================================

INSERT INTO users (id, username, email, password, role, full_name, phone, enabled) VALUES
(1, 'admin', 'admin@transitx.edu', '$2a$10$C8qR7b5UuKzLqTsm92.HkuwQkO7KzS63T.4lM4XpZ.3Z6jK1zF2Uq', 'ROLE_ADMIN', 'Chief Transport Administrator', '9876543210', TRUE),
(2, 'DRV001', 'ramesh.kumar@transitx.edu', '$2a$10$C8qR7b5UuKzLqTsm92.HkuwQkO7KzS63T.4lM4XpZ.3Z6jK1zF2Uq', 'ROLE_DRIVER', 'Ramesh Kumar', '9845123456', TRUE),
(3, 'DRV002', 'suresh.patel@transitx.edu', '$2a$10$C8qR7b5UuKzLqTsm92.HkuwQkO7KzS63T.4lM4XpZ.3Z6jK1zF2Uq', 'ROLE_DRIVER', 'Suresh Patel', '9845789123', TRUE),
(4, 'REG2024001', 'aaron.sharma@transitx.edu', '$2a$10$C8qR7b5UuKzLqTsm92.HkuwQkO7KzS63T.4lM4XpZ.3Z6jK1zF2Uq', 'ROLE_STUDENT', 'Aaron Sharma', '9123456780', TRUE),
(5, 'REG2024002', 'priya.nair@transitx.edu', '$2a$10$C8qR7b5UuKzLqTsm92.HkuwQkO7KzS63T.4lM4XpZ.3Z6jK1zF2Uq', 'ROLE_STUDENT', 'Priya Nair', '9123456781', TRUE)
ON DUPLICATE KEY UPDATE full_name=VALUES(full_name);

INSERT INTO buses (id, bus_number, capacity, status, model_plate) VALUES
(1, 'BUS-01', 52, 'ACTIVE', 'KA-01-TX-1001 (Ashok Leyland Viking)'),
(2, 'BUS-02', 48, 'ACTIVE', 'KA-01-TX-1002 (Tata Starbus)'),
(3, 'BUS-03', 50, 'ACTIVE', 'KA-01-TX-1003 (Eicher Skyline Pro)'),
(4, 'BUS-04', 40, 'MAINTENANCE', 'KA-01-TX-1004 (Tata Ultra)')
ON DUPLICATE KEY UPDATE bus_number=VALUES(bus_number);

INSERT INTO drivers (id, user_id, driver_code, full_name, phone, license_no, experience_years, assigned_bus_id) VALUES
(1, 2, 'DRV001', 'Ramesh Kumar', '9845123456', 'DL-KA-2015-008912', 9, 1),
(2, 3, 'DRV002', 'Suresh Patel', '9845789123', 'DL-KA-2012-004321', 12, 2)
ON DUPLICATE KEY UPDATE full_name=VALUES(full_name);

INSERT INTO routes (id, route_name, route_code, start_point, end_point, departure_time, arrival_time, est_duration, distance_km) VALUES
(1, 'City Central - Campus Express', 'RT-101', 'Majestic Central Terminus', 'Main Campus Gate 1', '07:30 AM', '08:35 AM', '65 mins', 24.50),
(2, 'Metro Corridor Link', 'RT-102', 'Indiranagar Metro Station', 'Engineering Block 3', '07:45 AM', '08:40 AM', '55 mins', 18.20),
(3, 'Green Hills Suburb Connector', 'RT-103', 'Whitefield Circle', 'Main Campus Gate 2', '07:20 AM', '08:45 AM', '85 mins', 31.00)
ON DUPLICATE KEY UPDATE route_name=VALUES(route_name);

INSERT INTO bus_stops (id, stop_name, latitude, longitude, landmark) VALUES
(1, 'Majestic Central Terminus', 12.9767500, 77.5712550, 'Platform 18 Exit'),
(2, 'Corporation Circle', 12.9682500, 77.5891400, 'Near LIC Building'),
(3, 'Shantinagar Bus Station', 12.9538000, 77.5957000, 'Main Depot Gate'),
(4, 'Dairy Circle', 12.9351000, 77.6012000, 'Opposite Forum Mall Road'),
(5, 'Silk Board Junction', 12.9174000, 77.6238000, 'Flyover Ramp Entry'),
(6, 'HSR Layout BDA Complex', 12.9116000, 77.6389000, 'Near 14th Main Road'),
(7, 'Main Campus Gate 1', 12.8615000, 77.6645000, 'TRANSITX Campus Bus Bay'),
(8, 'Indiranagar Metro Station', 12.9783000, 77.6408000, 'Metro Pillar 82'),
(9, 'Domlur Flyover Stop', 12.9609000, 77.6416000, 'Beside EGL Tech Park'),
(10, 'Koramangala Water Tank', 12.9345000, 77.6254000, 'Sony World Junction'),
(11, 'Engineering Block 3', 12.8630000, 77.6655000, 'Academic Plaza Bay')
ON DUPLICATE KEY UPDATE stop_name=VALUES(stop_name);

INSERT INTO route_stops (route_id, stop_id, stop_sequence, pickup_time) VALUES
(1, 1, 1, '07:30 AM'),
(1, 2, 2, '07:42 AM'),
(1, 3, 3, '07:54 AM'),
(1, 4, 4, '08:06 AM'),
(1, 5, 5, '08:18 AM'),
(1, 6, 6, '08:26 AM'),
(1, 7, 7, '08:35 AM'),
(2, 8, 1, '07:45 AM'),
(2, 9, 2, '07:58 AM'),
(2, 10, 3, '08:12 AM'),
(2, 5, 4, '08:25 AM'),
(2, 11, 5, '08:40 AM')
ON DUPLICATE KEY UPDATE pickup_time=VALUES(pickup_time);

INSERT INTO students (id, user_id, reg_no, department, year_of_study, full_name, email, phone, assigned_bus_id, assigned_route_id) VALUES
(1, 4, 'REG2024001', 'Computer Science & Engineering', '3rd Year', 'Aaron Sharma', 'aaron.sharma@transitx.edu', '9123456780', 1, 1),
(2, 5, 'REG2024002', 'Information Technology', '2nd Year', 'Priya Nair', 'priya.nair@transitx.edu', '9123456781', 2, 2)
ON DUPLICATE KEY UPDATE full_name=VALUES(full_name);

INSERT INTO bus_assignments (id, bus_id, driver_id, route_id, assigned_date, status) VALUES
(1, 1, 1, 1, CURDATE(), 'ACTIVE'),
(2, 2, 2, 2, CURDATE(), 'ACTIVE')
ON DUPLICATE KEY UPDATE status=VALUES(status);

INSERT INTO bus_locations (id, bus_id, latitude, longitude, speed, status, last_updated, is_simulated) VALUES
(1, 1, 12.9351000, 77.6012000, 38.50, 'ON_ROUTE', NOW(), TRUE),
(2, 2, 12.9609000, 77.6416000, 42.00, 'ON_ROUTE', NOW(), TRUE),
(3, 3, 12.9767500, 77.5712550, 0.00, 'NOT_STARTED', NOW(), TRUE)
ON DUPLICATE KEY UPDATE latitude=VALUES(latitude), longitude=VALUES(longitude);

INSERT INTO trips (id, bus_id, driver_id, route_id, start_time, trip_status, current_stop_id) VALUES
(1, 1, 1, 1, DATE_SUB(NOW(), INTERVAL 35 MINUTE), 'ON_ROUTE', 4),
(2, 2, 2, 2, DATE_SUB(NOW(), INTERVAL 20 MINUTE), 'ON_ROUTE', 9)
ON DUPLICATE KEY UPDATE trip_status=VALUES(trip_status);

INSERT INTO student_bus_pass (id, pass_code, student_id, route_id, bus_id, boarding_stop_id, status, validity_start, validity_end, id_proof_path, qr_code_data) VALUES
(1, 'TXPASS-2024-REG2024001', 1, 1, 1, 4, 'APPROVED', CURDATE(), DATE_ADD(CURDATE(), INTERVAL 6 MONTH), '/uploads/id_proof_reg2024001.jpg', 'TRANSITX|PASS:TXPASS-2024-REG2024001|STUDENT:REG2024001|ROUTE:RT-101|BUS:BUS-01|BOARDING:Dairy Circle|VALID_TILL:2025-05-01')
ON DUPLICATE KEY UPDATE pass_code=VALUES(pass_code);
