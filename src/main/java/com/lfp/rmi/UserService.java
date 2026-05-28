package com.lfp.rmi;

import com.lfp.model.User;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * RMI Service interface for User operations
 * Remote interface for distributed user management
 */
public interface UserService extends Remote {
    
    /**
     * Authenticate user with email and password
     * @param email User email
     * @param password User password (plain text)
     * @return User object if authentication successful, null otherwise
     * @throws RemoteException if RMI communication fails
     */
    User login(String email, String password) throws RemoteException;
    
    /**
     * Register a new user
     * @param username Username
     * @param email Email address
     * @param password Password (plain text, will be hashed)
     * @return Newly created User object
     * @throws RemoteException if RMI communication fails or registration fails
     */
    User register(String username, String email, String password) throws RemoteException;
    
    /**
     * Get user by ID
     * @param userId User ID
     * @return User object or null if not found
     * @throws RemoteException if RMI communication fails
     */
    User getUserById(int userId) throws RemoteException;
    
    /**
     * Get user by email
     * @param email User email
     * @return User object or null if not found
     * @throws RemoteException if RMI communication fails
     */
    User getUserByEmail(String email) throws RemoteException;
    
    /**
     * Get all users (admin only)
     * @return List of all users
     * @throws RemoteException if RMI communication fails
     */
    List<User> getAllUsers() throws RemoteException;
    
    /**
     * Update user information
     * @param user User object with updated information
     * @return true if update successful
     * @throws RemoteException if RMI communication fails
     */
    boolean updateUser(User user) throws RemoteException;
    
    /**
     * Block or unblock a user (admin only)
     * @param userId User ID to block/unblock
     * @param blocked true to block, false to unblock
     * @return true if operation successful
     * @throws RemoteException if RMI communication fails
     */
    boolean blockUser(int userId, boolean blocked) throws RemoteException;
    
    /**
     * Delete user account
     * @param userId User ID to delete
     * @return true if deletion successful
     * @throws RemoteException if RMI communication fails
     */
    boolean deleteUser(int userId) throws RemoteException;
    
    /**
     * Change user password
     * @param userId User ID
     * @param oldPassword Current password
     * @param newPassword New password
     * @return true if password changed successfully
     * @throws RemoteException if RMI communication fails
     */
    boolean changePassword(int userId, String oldPassword, String newPassword) throws RemoteException;
    
    /**
     * Get total number of users
     * @return Total user count
     * @throws RemoteException if RMI communication fails
     */
    int getUserCount() throws RemoteException;
}
