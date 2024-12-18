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