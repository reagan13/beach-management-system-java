-- Create Database
CREATE DATABASE beach_resort_db;

-- Use the database
USE beach_resort_db;

-- Create Users Table
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE,
    password VARCHAR(255),
    role ENUM('CUSTOMER', 'STAFF', 'OWNER') DEFAULT 'CUSTOMER'
);

-- Insert initial admin user
INSERT INTO users (username, password, role) VALUES 
('admin', 'admin123', 'OWNER');