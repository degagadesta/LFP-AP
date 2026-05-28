package com.lfp.socket;

/**
 * Enum representing different types of socket messages
 */
public enum MessageType {
    // Authentication messages
    LOGIN,
    LOGOUT,
    REGISTER,
    
    // Notification messages
    NOTIFICATION,
    ALERT,
    
    // Place-related messages
    PLACE_ADDED,
    PLACE_UPDATED,
    PLACE_DELETED,
    PLACE_APPROVED,
    
    // User-related messages
    USER_ONLINE,
    USER_OFFLINE,
    USER_BLOCKED,
    
    // Report messages
    REPORT_CREATED,
    REPORT_UPDATED,
    
    // Chat messages
    CHAT_MESSAGE,
    
    // System messages
    HEARTBEAT,
    DISCONNECT,
    ERROR,
    SUCCESS
}
