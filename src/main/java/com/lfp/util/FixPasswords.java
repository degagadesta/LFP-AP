package com.lfp.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Utility to fix/update passwords for existing users
 */
public class FixPasswords {
    
    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║         LFP Password Fix - Updating Test Users          ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println();
        
        try {
            Connection conn = DatabaseUtil.getInstance().getConnection();
            
            // Show current users
            System.out.println("Current users in database:");
            System.out.println("─────────────────────────────────────────────────────────");
            listAllUsers(conn);
            System.out.println();
            
            // Update passwords
            System.out.println("Updating passwords...");
            System.out.println("─────────────────────────────────────────────────────────");
            updatePassword(conn, "admin@lfp.com", "admin123", "ADMIN");
            updatePassword(conn, "user@lfp.com", "user123", "USER");
            
            System.out.println();
            System.out.println("Verifying updated users:");
            System.out.println("─────────────────────────────────────────────────────────");
            listAllUsers(conn);
            
            conn.close();
            
            System.out.println();
            System.out.println("✅ Password update completed successfully!");
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
            System.out.println();
            System.out.println("You can now login with these credentials!");
            
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void updatePassword(Connection conn, String email, String password, String role) 
            throws SQLException {
        // Check if user exists
        String checkSql = "SELECT id, username FROM users WHERE email = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setString(1, email);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                int userId = rs.getInt("id");
                String username = rs.getString("username");
                
                // Update password and role
                String updateSql = "UPDATE users SET password = ?, role = ?, updated_at = ? WHERE id = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    String hashedPassword = PasswordUtil.hashPassword(password);
                    updateStmt.setString(1, hashedPassword);
                    updateStmt.setString(2, role);
                    updateStmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
                    updateStmt.setInt(4, userId);
                    updateStmt.executeUpdate();
                    
                    System.out.println("✓ Updated: " + email + " (username: " + username + ", role: " + role + ")");
                }
            } else {
                // Create new user
                String insertSql = "INSERT INTO users (username, email, password, role, is_blocked, created_at) " +
                                  "VALUES (?, ?, ?, ?, ?, ?)";
                
                String username = email.split("@")[0]; // Use email prefix as username
                
                try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                    String hashedPassword = PasswordUtil.hashPassword(password);
                    
                    stmt.setString(1, username);
                    stmt.setString(2, email);
                    stmt.setString(3, hashedPassword);
                    stmt.setString(4, role);
                    stmt.setBoolean(5, false);
                    stmt.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
                    
                    stmt.executeUpdate();
                    System.out.println("✓ Created: " + email + " (username: " + username + ", role: " + role + ")");
                }
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
            
            int count = 0;
            while (rs.next()) {
                System.out.printf("%-5d %-15s %-25s %-10s %-10s%n",
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getString("role"),
                    rs.getBoolean("is_blocked") ? "Yes" : "No"
                );
                count++;
            }
            
            if (count == 0) {
                System.out.println("No users found in database.");
            }
        }
    }
}
