package com.lfp.util;

import java.util.regex.Pattern;

/**
 * Utility class for input validation
 */
public class ValidationUtil {
    
    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    // Username validation pattern (alphanumeric and underscore, 3-20 chars)
    private static final Pattern USERNAME_PATTERN = Pattern.compile(
        "^[A-Za-z0-9_]{3,20}$"
    );
    
    /**
     * Validate email address
     * @param email Email to validate
     * @return true if email is valid
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * Validate username
     * @param username Username to validate
     * @return true if username is valid
     */
    public static boolean isValidUsername(String username) {
        if (username == null || username.isEmpty()) {
            return false;
        }
        return USERNAME_PATTERN.matcher(username).matches();
    }
    
    /**
     * Validate password strength
     * @param password Password to validate
     * @return true if password meets requirements
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 6) {
            return false;
        }
        // Add more complex validation if needed
        return true;
    }
    
    /**
     * Validate place name
     * @param name Place name to validate
     * @return true if name is valid
     */
    public static boolean isValidPlaceName(String name) {
        return name != null && !name.trim().isEmpty() && name.length() >= 3;
    }
    
    /**
     * Validate rating value (0.0 to 5.0)
     * @param rating Rating to validate
     * @return true if rating is valid
     */
    public static boolean isValidRating(double rating) {
        return rating >= 0.0 && rating <= 5.0;
    }
    
    /**
     * Validate coordinates
     * @param latitude Latitude value
     * @param longitude Longitude value
     * @return true if coordinates are valid
     */
    public static boolean isValidCoordinates(double latitude, double longitude) {
        return latitude >= -90 && latitude <= 90 && 
               longitude >= -180 && longitude <= 180;
    }
    
    /**
     * Sanitize string input to prevent SQL injection
     * @param input Input string
     * @return Sanitized string
     */
    public static String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        // Remove potentially dangerous characters
        return input.replaceAll("[<>\"';]", "");
    }
    
    /**
     * Check if string is null or empty
     * @param str String to check
     * @return true if string is null or empty
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}
