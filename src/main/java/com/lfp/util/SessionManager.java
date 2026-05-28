package com.lfp.util;

import com.lfp.model.User;

/**
 * SessionManager - Singleton class to manage user session
 * Stores the currently logged-in user information
 */
public class SessionManager {
    
    private static SessionManager instance;
    private User currentUser;
    
    // Private constructor for singleton pattern
    private SessionManager() {
    }
    
    /**
     * Get the singleton instance of SessionManager
     * @return SessionManager instance
     */
    public static SessionManager getInstance() {
        if (instance == null) {
            synchronized (SessionManager.class) {
                if (instance == null) {
                    instance = new SessionManager();
                }
            }
        }
        return instance;
    }
    
    /**
     * Set the current logged-in user
     * @param user User object
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
    
    /**
     * Get the current logged-in user
     * @return Current user or null if not logged in
     */
    public User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Check if a user is currently logged in
     * @return true if user is logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    /**
     * Clear the current session (logout)
     */
    public void clearSession() {
        this.currentUser = null;
    }
    
    /**
     * Logout the current user (alias for clearSession)
     */
    public void logout() {
        clearSession();
    }
    
    /**
     * Check if the current user is an admin
     * @return true if current user is admin, false otherwise
     */
    public boolean isAdmin() {
        return currentUser != null && currentUser.isAdmin();
    }
    
    /**
     * Get the current user's ID
     * @return User ID or -1 if not logged in
     */
    public int getCurrentUserId() {
        return currentUser != null ? currentUser.getId() : -1;
    }
    
    /**
     * Get the current user's username
     * @return Username or null if not logged in
     */
    public String getCurrentUsername() {
        return currentUser != null ? currentUser.getUsername() : null;
    }
}
