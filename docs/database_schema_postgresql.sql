-- ================================================
-- Laptop Friendly Places - Advanced Programming
-- PostgreSQL Database Schema
-- ================================================

-- Create database (run this separately if needed)
-- CREATE DATABASE lfp_db;

-- Connect to the database
-- \c lfp_db

-- ================================================
-- Users Table
-- ================================================
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'USER' CHECK (role IN ('USER', 'ADMIN')),
    is_blocked BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);

-- ================================================
-- Places Table
-- ================================================
CREATE TABLE IF NOT EXISTS places (
    id SERIAL PRIMARY KEY,
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
    status VARCHAR(20) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')),
    contributed_by INTEGER,
    image_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (contributed_by) REFERENCES users(id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_places_status ON places(status);
CREATE INDEX IF NOT EXISTS idx_places_category ON places(category);
CREATE INDEX IF NOT EXISTS idx_places_location ON places(location_lat, location_lng);

-- ================================================
-- Favorites Table
-- ================================================
CREATE TABLE IF NOT EXISTS favorites (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    place_id INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (place_id) REFERENCES places(id) ON DELETE CASCADE,
    UNIQUE (user_id, place_id)
);

CREATE INDEX IF NOT EXISTS idx_favorites_user_id ON favorites(user_id);
CREATE INDEX IF NOT EXISTS idx_favorites_place_id ON favorites(place_id);

-- ================================================
-- Reports Table
-- ================================================
CREATE TABLE IF NOT EXISTS reports (
    id SERIAL PRIMARY KEY,
    place_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    reason TEXT NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'RESOLVED', 'REJECTED')),
    admin_notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (place_id) REFERENCES places(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_reports_status ON reports(status);
CREATE INDEX IF NOT EXISTS idx_reports_place_id ON reports(place_id);

-- ================================================
-- Insert Test Users
-- ================================================

-- Admin User
-- Email: admin@lfp.com
-- Password: admin123
-- BCrypt hash for 'admin123'
INSERT INTO users (username, email, password, role, created_at) 
VALUES (
    'admin',
    'admin@lfp.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    'ADMIN',
    NOW()
) ON CONFLICT (email) DO NOTHING;

-- Regular User
-- Email: user@lfp.com
-- Password: user123
-- BCrypt hash for 'user123'
INSERT INTO users (username, email, password, role, created_at) 
VALUES (
    'user',
    'user@lfp.com',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi',
    'USER',
    NOW()
) ON CONFLICT (email) DO NOTHING;

-- ================================================
-- Insert Sample Places
-- ================================================
INSERT INTO places (name, description, category, address, location_lat, location_lng, 
                   rating_wifi, rating_power, rating_service, rating_overall, status, contributed_by) 
VALUES
('Starbucks Bole', 'Modern coffee shop with excellent WiFi and plenty of power outlets. Great for remote work.', 
 'Cafe', 'Bole Road, Addis Ababa', 9.0192, 38.7525, 4.5, 4.8, 4.3, 4.5, 'APPROVED', 1),

('Tomoca Coffee', 'Traditional Ethiopian coffee house with reliable internet and quiet atmosphere.', 
 'Cafe', 'Wawel Street, Addis Ababa', 9.0320, 38.7469, 4.0, 3.8, 4.5, 4.1, 'APPROVED', 1),

('Impact Hub Addis', 'Coworking space designed for entrepreneurs and freelancers with high-speed internet.', 
 'Coworking', 'Kazanchis, Addis Ababa', 9.0227, 38.7636, 5.0, 5.0, 4.8, 4.9, 'APPROVED', 1),

('National Library', 'Quiet study environment with free WiFi. Limited power outlets.', 
 'Library', 'Arat Kilo, Addis Ababa', 9.0365, 38.7612, 3.5, 3.0, 4.0, 3.5, 'APPROVED', 1),

('Kaldi''s Coffee', 'Popular coffee chain with good WiFi and comfortable seating.', 
 'Cafe', 'Bole Medhanialem, Addis Ababa', 9.0100, 38.7600, 4.2, 4.0, 4.3, 4.2, 'PENDING', 2),

('BluSpace Coworking', 'Modern coworking space with meeting rooms and high-speed internet.', 
 'Coworking', 'CMC Road, Addis Ababa', 9.0150, 38.7550, 4.8, 4.9, 4.7, 4.8, 'PENDING', 2)
ON CONFLICT DO NOTHING;

-- ================================================
-- Insert Sample Favorites
-- ================================================
INSERT INTO favorites (user_id, place_id) VALUES
(2, 1),
(2, 3)
ON CONFLICT DO NOTHING;

-- ================================================
-- Insert Sample Reports
-- ================================================
INSERT INTO reports (place_id, user_id, reason, status) VALUES
(1, 2, 'WiFi password has changed and staff is not sharing it', 'PENDING'),
(4, 2, 'Power outlets are not working properly', 'RESOLVED')
ON CONFLICT DO NOTHING;

-- ================================================
-- Verification Queries
-- ================================================

-- Show all tables
SELECT table_name FROM information_schema.tables 
WHERE table_schema = 'public' 
ORDER BY table_name;

-- Count records
SELECT 
    (SELECT COUNT(*) FROM users) AS total_users,
    (SELECT COUNT(*) FROM places) AS total_places,
    (SELECT COUNT(*) FROM reports) AS total_reports,
    (SELECT COUNT(*) FROM favorites) AS total_favorites;

-- Show all users
SELECT id, username, email, role, is_blocked FROM users;

-- Show all places
SELECT id, name, category, status, rating_overall FROM places;

-- ================================================
-- Useful Queries for Testing
-- ================================================

-- Get all approved places
-- SELECT * FROM places WHERE status = 'APPROVED' ORDER BY rating_overall DESC;

-- Get user's favorites
-- SELECT p.* FROM places p 
-- INNER JOIN favorites f ON p.id = f.place_id 
-- WHERE f.user_id = 2;

-- Get pending reports
-- SELECT r.*, p.name as place_name, u.username as reported_by 
-- FROM reports r
-- INNER JOIN places p ON r.place_id = p.id
-- INNER JOIN users u ON r.user_id = u.id
-- WHERE r.status = 'PENDING';

-- Search places by name or category
-- SELECT * FROM places 
-- WHERE status = 'APPROVED' 
-- AND (name ILIKE '%coffee%' OR category ILIKE '%cafe%')
-- ORDER BY rating_overall DESC;

-- ================================================
-- Database Setup Complete!
-- ================================================
SELECT 'PostgreSQL database setup completed successfully!' AS status;
