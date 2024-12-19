-- database/setup.sql
CREATE DATABASE IF NOT EXISTS beach_resort_db;
USE beach_resort_db;

-- Users Table
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'STAFF', 'CUSTOMER') DEFAULT 'CUSTOMER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Initial Admin User
INSERT INTO users (username, password, role) 
VALUES ('admin', 'admin123', 'ADMIN');

CREATE DATABASE IF NOT EXISTS beach_resort_db;

USE beach_resort_db;

CREATE TABLE bookings (
    booking_id VARCHAR(50) PRIMARY KEY,
    guest_name VARCHAR(100) NOT NULL,
    contact_number VARCHAR(20),
    email VARCHAR(100),
    room_type VARCHAR(50) NOT NULL,
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    total_guests INT NOT NULL,
    status VARCHAR(20) DEFAULT 'Confirmed',
    special_requests TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Add some sample data
INSERT INTO bookings (booking_id, guest_name, room_type, check_in_date, check_out_date, total_guests, status, contact_number, email) 
VALUES 
('B001', 'John Doe', 'Deluxe Ocean View', '2023-06-15', '2023-06-20', 2, 'Confirmed', '123-456-7890', 'john@example.com'),
('B002', 'Jane Smith', 'Standard Room', '2023-06-18', '2023-06-22', 1, 'Pending', '987-654-3210', 'jane@example.com');