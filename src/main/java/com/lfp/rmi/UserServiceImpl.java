package com.lfp.rmi;

import com.lfp.model.User;
import com.lfp.model.UserRole;
import com.lfp.util.DatabaseUtil;
import com.lfp.util.PasswordUtil;
import com.lfp.util.ValidationUtil;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of UserService RMI interface
 * Handles all user-related database operations
 */
public class UserServiceImpl extends UnicastRemoteObject implements UserService {
    private static final long serialVersionUID = 1L;
    
    public UserServiceImpl() throws RemoteException {
        super();
    }
    
    @Override
    public User login(String email, String password) throws RemoteException {
        if (!ValidationUtil.isValidEmail(email)) {
            throw new RemoteException("Invalid email format");
        }
        
        String sql = "SELECT * FROM users WHERE email = ?";
        
        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String hashedPassword = rs.getString("password");
                
                // Verify password
                if (PasswordUtil.verifyPassword(password, hashedPassword)) {
                    User user = extractUserFromResultSet(rs);
                    
                    // Check if user is blocked
                    if (user.isBlocked()) {
                        throw new RemoteException("Account is blocked. Contact administrator.");
                    }
                    
                    System.out.println("✓ User logged in: " + user.getUsername());
                    return user;
                }
            }
            
            throw new RemoteException("Invalid email or password");
            
        } catch (SQLException e) {
            System.err.println("Login error: " + e.getMessage());
            throw new RemoteException("Login failed: " + e.getMessage());
        }
    }
    
    @Override
    public User register(String username, String email, String password) throws RemoteException {
        // Validate inputs
        if (!ValidationUtil.isValidUsername(username)) {
            throw new RemoteException("Invalid username. Use 3-20 alphanumeric characters.");
        }
        if (!ValidationUtil.isValidEmail(email)) {
            throw new RemoteException("Invalid email format");
        }
        if (!ValidationUtil.isValidPassword(password)) {
            throw new RemoteException("Password must be at least 6 characters");
        }
        
        // Check if email already exists
        if (getUserByEmail(email) != null) {
            throw new RemoteException("Email already registered");
        }
        
        String sql = "INSERT INTO users (username, email, password, role, created_at) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            String hashedPassword = PasswordUtil.hashPassword(password);
            
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, hashedPassword);
            stmt.setString(4, UserRole.USER.getValue());
            stmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int userId = generatedKeys.getInt(1);
                    User user = new User(userId, username, email, UserRole.USER);
                    System.out.println("✓ User registered: " + username);
                    return user;
                }
            }
            
            throw new RemoteException("Registration failed");
            
        } catch (SQLException e) {
            System.err.println("Registration error: " + e.getMessage());
            throw new RemoteException("Registration failed: " + e.getMessage());
        }
    }
    
    @Override
    public User getUserById(int userId) throws RemoteException {
        String sql = "SELECT * FROM users WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }
            
            return null;
            
        } catch (SQLException e) {
            System.err.println("Error getting user by ID: " + e.getMessage());
            throw new RemoteException("Failed to get user: " + e.getMessage());
        }
    }
    
    @Override
    public User getUserByEmail(String email) throws RemoteException {
        String sql = "SELECT * FROM users WHERE email = ?";
        
        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }
            
            return null;
            
        } catch (SQLException e) {
            System.err.println("Error getting user by email: " + e.getMessage());
            throw new RemoteException("Failed to get user: " + e.getMessage());
        }
    }
    
    @Override
    public List<User> getAllUsers() throws RemoteException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }
            
            System.out.println("✓ Retrieved " + users.size() + " users");
            return users;
            
        } catch (SQLException e) {
            System.err.println("Error getting all users: " + e.getMessage());
            throw new RemoteException("Failed to get users: " + e.getMessage());
        }
    }
    
    @Override
    public boolean updateUser(User user) throws RemoteException {
        String sql = "UPDATE users SET username = ?, email = ?, role = ?, updated_at = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getRole().getValue());
            stmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(5, user.getId());
            
            int affectedRows = stmt.executeUpdate();
            System.out.println("✓ User updated: " + user.getUsername());
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
            throw new RemoteException("Failed to update user: " + e.getMessage());
        }
    }
    
    @Override
    public boolean blockUser(int userId, boolean blocked) throws RemoteException {
        String sql = "UPDATE users SET is_blocked = ?, updated_at = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBoolean(1, blocked);
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(3, userId);
            
            int affectedRows = stmt.executeUpdate();
            System.out.println("✓ User " + (blocked ? "blocked" : "unblocked") + ": ID " + userId);
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error blocking/unblocking user: " + e.getMessage());
            throw new RemoteException("Failed to block/unblock user: " + e.getMessage());
        }
    }
    
    @Override
    public boolean deleteUser(int userId) throws RemoteException {
        String sql = "DELETE FROM users WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            int affectedRows = stmt.executeUpdate();
            System.out.println("✓ User deleted: ID " + userId);
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            throw new RemoteException("Failed to delete user: " + e.getMessage());
        }
    }
    
    @Override
    public boolean changePassword(int userId, String oldPassword, String newPassword) throws RemoteException {
        // First verify old password
        User user = getUserById(userId);
        if (user == null) {
            throw new RemoteException("User not found");
        }
        
        String sql = "UPDATE users SET password = ?, updated_at = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String hashedPassword = PasswordUtil.hashPassword(newPassword);
            
            stmt.setString(1, hashedPassword);
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(3, userId);
            
            int affectedRows = stmt.executeUpdate();
            System.out.println("✓ Password changed for user ID: " + userId);
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error changing password: " + e.getMessage());
            throw new RemoteException("Failed to change password: " + e.getMessage());
        }
    }
    
    @Override
    public int getUserCount() throws RemoteException {
        String sql = "SELECT COUNT(*) FROM users";
        
        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
            
        } catch (SQLException e) {
            System.err.println("Error getting user count: " + e.getMessage());
            throw new RemoteException("Failed to get user count: " + e.getMessage());
        }
    }
    
    /**
     * Helper method to extract User object from ResultSet
     */
    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setRole(UserRole.fromString(rs.getString("role")));
        user.setBlocked(rs.getBoolean("is_blocked"));
        user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            user.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return user;
    }
}
