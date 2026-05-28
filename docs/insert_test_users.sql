-- ================================================
-- Insert Test Users for LFP Application
-- Run this in your PostgreSQL database
-- ================================================

-- Admin User
-- Email: admin@lfp.com
-- Password: admin123
INSERT INTO users (username, email, password, role, created_at) 
VALUES (
    'admin',
    'admin@lfp.com',
    '$2a$10$N9qo$8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    'ADMIN',
    NOW()
) ON CONFLICT (email) DO UPDATE SET 
    password = EXCLUDED.password,
    role = EXCLUDED.role;

-- Regular User
-- Email: user@lfp.com
-- Password: user123
INSERT INTO users (username, email, password, role, created_at) 
VALUES (
    'user',
    'user@lfp.com',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi',
    'USER',
    NOW()
) ON CONFLICT (email) DO UPDATE SET 
    password = EXCLUDED.password,
    role = EXCLUDED.role;

-- Verify users were inserted
SELECT id, username, email, role, is_blocked, created_at 
FROM users 
ORDER BY id;
