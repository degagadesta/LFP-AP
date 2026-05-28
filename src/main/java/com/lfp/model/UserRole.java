package com.lfp.model;

/**
 * Enum representing user roles in the system
 */
public enum UserRole {
    USER("user"),
    ADMIN("admin");
    
    private final String value;
    
    UserRole(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public static UserRole fromString(String value) {
        for (UserRole role : UserRole.values()) {
            if (role.value.equalsIgnoreCase(value)) {
                return role;
            }
        }
        return USER; // Default to USER if not found
    }
    
    @Override
    public String toString() {
        return value;
    }
}
