package com.lfp.socket;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Message class for socket communication
 * Implements Serializable for network transmission
 */
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private MessageType type;
    private String content;
    private int senderId;
    private String senderName;
    private LocalDateTime timestamp;
    private Map<String, Object> data;
    
    // Constructors
    public Message() {
        this.timestamp = LocalDateTime.now();
        this.data = new HashMap<>();
    }
    
    public Message(MessageType type, String content) {
        this();
        this.type = type;
        this.content = content;
    }
    
    public Message(MessageType type, String content, int senderId) {
        this(type, content);
        this.senderId = senderId;
    }
    
    // Getters and Setters
    public MessageType getType() {
        return type;
    }
    
    public void setType(MessageType type) {
        this.type = type;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public int getSenderId() {
        return senderId;
    }
    
    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }
    
    public String getSenderName() {
        return senderName;
    }
    
    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public Map<String, Object> getData() {
        return data;
    }
    
    public void setData(Map<String, Object> data) {
        this.data = data;
    }
    
    // Utility methods
    public void addData(String key, Object value) {
        this.data.put(key, value);
    }
    
    public Object getData(String key) {
        return this.data.get(key);
    }
    
    public boolean hasData(String key) {
        return this.data.containsKey(key);
    }
    
    @Override
    public String toString() {
        return "Message{" +
                "type=" + type +
                ", content='" + content + '\'' +
                ", senderId=" + senderId +
                ", senderName='" + senderName + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
    
    // Factory methods for common message types
    public static Message notification(String content) {
        return new Message(MessageType.NOTIFICATION, content);
    }
    
    public static Message error(String content) {
        return new Message(MessageType.ERROR, content);
    }
    
    public static Message success(String content) {
        return new Message(MessageType.SUCCESS, content);
    }
    
    public static Message placeAdded(String placeName) {
        Message msg = new Message(MessageType.PLACE_ADDED, 
            "New place added: " + placeName);
        msg.addData("placeName", placeName);
        return msg;
    }
    
    public static Message placeApproved(String placeName) {
        Message msg = new Message(MessageType.PLACE_APPROVED, 
            "Place approved: " + placeName);
        msg.addData("placeName", placeName);
        return msg;
    }
    
    public static Message userOnline(String username) {
        Message msg = new Message(MessageType.USER_ONLINE, 
            username + " is now online");
        msg.addData("username", username);
        return msg;
    }
    
    public static Message userOffline(String username) {
        Message msg = new Message(MessageType.USER_OFFLINE, 
            username + " is now offline");
        msg.addData("username", username);
        return msg;
    }
}
