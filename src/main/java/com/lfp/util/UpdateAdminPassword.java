package com.lfp.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Simple utility to update the admin password
 */
public class UpdateAdminPassword {
    
    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║            Updating Admin Password                      ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println();
        
        try {
            Connection conn = DatabaseUtil.getInstance().getConnection();
            
            // Find admin user
            String findSql = "SELECT id, username, email, role FROM users WHERE role = 'admin' OR role = 'ADMIN' LIMIT 1";
            PreparedStatement findStmt = conn.prepareStatement(findSql);
            ResultSet rs = findStmt.executeQuery();
            
            if (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");
                String email = rs.getString("email");
                String role = rs.getString("role");
                
                System.out.println("Found admin user:");
                System.out.println("  ID: " + id);
                System.out.println("  Username: " + username);
                System.out.println("  Email: " + email);
                System.out.println("  Role: " + role);
                System.out.println();
                
                // Update password
                String newPassword = "admin123";
                String hashedPassword = PasswordUtil.hashPassword(newPassword);
                
                String updateSql = "UPDATE users SET password = ?, updated_at = ? WHERE id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setString(1, hashedPassword);
                updateStmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                updateStmt.setInt(3, id);
                
                int rows = updateStmt.executeUpdate();
                
                if (rows > 0) {
                    System.out.println("✅ Password updated successfully!");
                    System.out.println();
                    System.out.println("Login Credentials:");
                    System.out.println("─────────────────────────────────────────────────────────");
                    System.out.println("Email:    " + email);
                    System.out.println("Password: " + newPassword);
                    System.out.println("─────────────────────────────────────────────────────────");
                    System.out.println();
                    System.out.println("Use these credentials to login!");
                } else {
                    System.out.println("❌ Failed to update password");
                }
                
                updateStmt.close();
            } else {
                System.out.println("❌ No admin user found in database");
                System.out.println();
                System.out.println("Creating new admin user...");
                
                // Create admin user
                String insertSql = "INSERT INTO users (username, email, password, role, is_blocked, created_at) " +
                                  "VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                
                String hashedPassword = PasswordUtil.hashPassword("admin123");
                insertStmt.setString(1, "admin");
                insertStmt.setString(2, "admin@lfp.com");
                insertStmt.setString(3, hashedPassword);
                insertStmt.setString(4, "ADMIN");
                insertStmt.setBoolean(5, false);
                insertStmt.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
                
                int rows = insertStmt.executeUpdate();
                
                if (rows > 0) {
                    System.out.println("✅ Admin user created successfully!");
                    System.out.println();
                    System.out.println("Login Credentials:");
                    System.out.println("─────────────────────────────────────────────────────────");
                    System.out.println("Email:    admin@lfp.com");
                    System.out.println("Password: admin123");
                    System.out.println("─────────────────────────────────────────────────────────");
                }
                
                insertStmt.close();
            }
            
            findStmt.close();
            rs.close();
            conn.close();
            
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
