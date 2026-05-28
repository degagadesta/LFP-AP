package com.lfp.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Utility class to set up initial database data
 * Run this once to create test users
 */
public class DatabaseSetup {
    
    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║         LFP Database Setup - Creating Test Users        ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println();
        
        try {
            createTestUsers();
            System.out.println();
            System.out.println("✅ Database setup completed successfully!");
            System.out.println();
            System.out.println("Test Credentials:");
            System.out.println("─────────────────────────────────────────────────────────");
            System.out.println("Admin:");
            System.out.println("  Email:    admin@lfp.com");
            System.out.println("  Password: admin123");
            System.out.println();
            System.out.println("User:");
            System.out.println("  Email:    user@lfp.com");
            System.out.println("  Password: user123");
            System.out.println("─────────────────────────────────────────────────────────");
            
        } catch (Exception e) {
            System.err.println("❌ Error setting up database: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void createTestUsers() throws SQLException {
        Connection conn = DatabaseUtil.getInstance().getConnection();
        
        // Create admin user
        createUser(conn, "admin", "admin@lfp.com", "admin123", "ADMIN");
        
        // Create regular user
        createUser(conn, "user", "user@lfp.com", "user123", "USER");
        
        // Verify users
        System.out.println();
        System.out.println("Verifying users in database:");
        System.out.println("─────────────────────────────────────────────────────────");
        listAllUsers(conn);
        
        conn.close();
    }
    
    private static void createUser(Connection conn, String username, String email, 
                                   String password, String role) throws SQLException {
        // Check if user already exists
        String checkSql = "SELECT id FROM users WHERE email = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setString(1, email);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                int userId = rs.getInt("id");
                System.out.println("⚠ User already exists: " + email + " (ID: " + userId + ")");
                
                // Update password and role
                String updateSql = "UPDATE users SET password = ?, role = ?, updated_at = ? WHERE id = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    String hashedPassword = PasswordUtil.hashPassword(password);
                    updateStmt.setString(1, hashedPassword);
                    updateStmt.setString(2, role);
                    updateStmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
                    updateStmt.setInt(4, userId);
                    updateStmt.executeUpdate();
                    System.out.println("✓ Updated user: " + email);
                }
                return;
            }
        }
        
        // Insert new user
        String insertSql = "INSERT INTO users (username, email, password, role, is_blocked, created_at) " +
                          "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
            String hashedPassword = PasswordUtil.hashPassword(password);
            
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, hashedPassword);
            stmt.setString(4, role);
            stmt.setBoolean(5, false);
            stmt.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("✓ Created user: " + email + " (role: " + role + ")");
                System.out.println("  Password hash: " + hashedPassword.substring(0, 20) + "...");
            }
        }
    }
    
    private static void listAllUsers(Connection conn) throws SQLException {
        String sql = "SELECT id, username, email, role, is_blocked FROM users ORDER BY id";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            System.out.printf("%-5s %-15s %-25s %-10s %-10s%n", 
                             "ID", "Username", "Email", "Role", "Blocked");
            System.out.println("─────────────────────────────────────────────────────────");
            
            while (rs.next()) {
                System.out.printf("%-5d %-15s %-25s %-10s %-10s%n",
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getString("role"),
                    rs.getBoolean("is_blocked") ? "Yes" : "No"
                );
            }
        }
    }
}
