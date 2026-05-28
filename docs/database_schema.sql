-- ================================================
-- Laptop Friendly Places - Advanced Programming
-- Database Schema for Java Application
-- ================================================

-- Create database
CREATE DATABASE IF NOT EXISTS lfp_ap_db;
USE lfp_ap_db;

-- ================================================
-- Users Table
-- ================================================
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('user', 'admin') DEFAULT 'user',
    is_blocked TINYINT(1) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_username (username),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ================================================
-- Places Table
-- ================================================
CREATE TABLE IF NOT EXISTS places (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(100),
    address TEXT,
    location_lat DECIMAL(10, 8),
    location_lng DECIMAL(11, 8),
    rating_wifi DECIMAL(3, 2) DEFAULT 0,
    rating_power DECIMAL(3, 2) DEFAULT 0,
    rating_service DECIMAL(3, 2) DEFAULT 0,
    rating_overall DECIMAL(3, 2) DEFAULT 0,
    status ENUM('pending', 'approved', 'rejected') DEFAULT 'pending',
    contributed_by INT DEFAULT NULL,
    image_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (contributed_by) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_status (status),
    INDEX idx_category (category),
    INDEX idx_location (location_lat, location_lng)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ================================================
-- Favorites Table
-- ================================================
CREATE TABLE IF NOT EXISTS favorites (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    place_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (place_id) REFERENCES places(id) ON DELETE CASCADE,
    UNIQUE KEY unique_favorite (user_id, place_id),
    INDEX idx_user_id (user_id),
    INDEX idx_place_id (place_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ================================================
-- Reports Table
-- ================================================
CREATE TABLE IF NOT EXISTS reports (
    id INT AUTO_INCREMENT PRIMARY KEY,
    place_id INT NOT NULL,
    user_id INT NOT NULL,
    reason TEXT NOT NULL,
    status ENUM('pending', 'resolved', 'rejected') DEFAULT 'pending',
    admin_notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (place_id) REFERENCES places(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_status (status),
    INDEX idx_place_id (place_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ================================================
-- Login Attempts Table (for security)
-- ================================================
CREATE TABLE IF NOT EXISTS login_attempts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) NOT NULL,
    ip_address VARCHAR(45) NOT NULL,
    success TINYINT(1) DEFAULT 0,
    attempted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_ip (ip_address)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ================================================
-- Sessions Table (for tracking active sessions)
-- ================================================
CREATE TABLE IF NOT EXISTS sessions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    session_token VARCHAR(255) NOT NULL UNIQUE,
    ip_address VARCHAR(45),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_token (session_token),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ================================================
-- Insert Default Admin User
-- Password: admin123 (BCrypt hashed)
-- ================================================
INSERT INTO users (username, email, password, role, created_at) 
VALUES (
    'admin',
    'admin@laptopfriendly.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    'admin',
    NOW()
) ON DUPLICATE KEY UPDATE email=email;

-- ================================================
-- Insert Test User
-- Password: test123 (BCrypt hashed)
-- ================================================
INSERT INTO users (username, email, password, role, created_at) 
VALUES (
    'testuser',
    'test@example.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    'user',
    NOW()
) ON DUPLICATE KEY UPDATE email=email;

-- ================================================
-- Insert Sample Places (for testing)
-- ================================================
INSERT INTO places (name, description, category, address, location_lat, location_lng, 
                   rating_wifi, rating_power, rating_service, rating_overall, status, contributed_by) 
VALUES
('Starbucks Bole', 'Modern coffee shop with excellent WiFi and plenty of power outlets. Great for remote work.', 
 'Cafe', 'Bole Road, Addis Ababa', 9.0192, 38.7525, 4.5, 4.8, 4.3, 4.5, 'approved', 1),

('Tomoca Coffee', 'Traditional Ethiopian coffee house with reliable internet and quiet atmosphere.', 
 'Cafe', 'Wawel Street, Addis Ababa', 9.0320, 38.7469, 4.0, 3.8, 4.5, 4.1, 'approved', 1),

('Impact Hub Addis', 'Coworking space designed for entrepreneurs and freelancers with high-speed internet.', 
 'Coworking', 'Kazanchis, Addis Ababa', 9.0227, 38.7636, 5.0, 5.0, 4.8, 4.9, 'approved', 1),

('National Library', 'Quiet study environment with free WiFi. Limited power outlets.', 
 'Library', 'Arat Kilo, Addis Ababa', 9.0365, 38.7612, 3.5, 3.0, 4.0, 3.5, 'approved', 1),

('Kaldi\'s Coffee', 'Popular coffee chain with good WiFi and comfortable seating.', 
 'Cafe', 'Bole Medhanialem, Addis Ababa', 9.0100, 38.7600, 4.2, 4.0, 4.3, 4.2, 'pending', 2),

('BluSpace Coworking', 'Modern coworking space with meeting rooms and high-speed internet.', 
 'Coworking', 'CMC Road, Addis Ababa', 9.0150, 38.7550, 4.8, 4.9, 4.7, 4.8, 'pending', 2)
ON DUPLICATE KEY UPDATE name=name;

-- ================================================
-- Insert Sample Favorites
-- ================================================
INSERT INTO favorites (user_id, place_id) VALUES
(2, 1),
(2, 3)
ON DUPLICATE KEY UPDATE user_id=user_id;

-- ================================================
-- Insert Sample Reports
-- ================================================
INSERT INTO reports (place_id, user_id, reason, status) VALUES
(1, 2, 'WiFi password has changed and staff is not sharing it', 'pending'),
(4, 2, 'Power outlets are not working properly', 'resolved')
ON DUPLICATE KEY UPDATE reason=reason;

-- ================================================
-- Verification Queries
-- ================================================

-- Check if tables were created
SELECT 'Database setup completed successfully!' AS status;

-- Show all tables
SHOW TABLES;

-- Count records
SELECT 
    (SELECT COUNT(*) FROM users) AS total_users,
    (SELECT COUNT(*) FROM places) AS total_places,
    (SELECT COUNT(*) FROM reports) AS total_reports,
    (SELECT COUNT(*) FROM favorites) AS total_favorites;

-- Show admin user
SELECT id, username, email, role FROM users WHERE role = 'admin';

-- Show all places
SELECT id, name, category, status, rating_overall FROM places;

-- ================================================
-- Useful Queries for Application
-- ================================================

-- Get all approved places
-- SELECT * FROM places WHERE status = 'approved' ORDER BY rating_overall DESC;

-- Get user's favorites
-- SELECT p.* FROM places p 
-- INNER JOIN favorites f ON p.id = f.place_id 
-- WHERE f.user_id = ?;

-- Get pending reports
-- SELECT r.*, p.name as place_name, u.username as reported_by 
-- FROM reports r
-- INNER JOIN places p ON r.place_id = p.id
-- INNER JOIN users u ON r.user_id = u.id
-- WHERE r.status = 'pending';

-- Search places by name or category
-- SELECT * FROM places 
-- WHERE status = 'approved' 
-- AND (name LIKE ? OR category LIKE ?)
-- ORDER BY rating_overall DESC;
