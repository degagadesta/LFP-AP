package com.lfp.server;

import com.lfp.socket.Message;
import com.lfp.socket.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Handles individual client connections
 * Processes messages from clients and sends responses
 * Runs in separate thread for each client
 */
public class ClientHandler implements Runnable {
    private final Socket socket;
    private final SocketServer server;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean connected;
    private int userId;
    private String username;
    
    public ClientHandler(Socket socket, SocketServer server) {
        this.socket = socket;
        this.server = server;
        this.connected = true;
        this.userId = -1;
    }
    
    @Override
    public void run() {
        try {
            // Initialize streams
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            
            // Send welcome message
            Message welcome = Message.notification("Connected to LFP Server");
            sendMessage(welcome);
            
            // Listen for messages
            while (connected) {
                try {
                    Message message = (Message) in.readObject();
                    handleMessage(message);
                } catch (ClassNotFoundException e) {
                    System.err.println("Invalid message received: " + e.getMessage());
                }
            }
            
        } catch (IOException e) {
            if (connected) {
                System.err.println("Client connection error: " + e.getMessage());
            }
        } finally {
            disconnect();
        }
    }
    
    /**
     * Handle incoming message from client
     */
    private void handleMessage(Message message) {
        System.out.println("Received message: " + message.getType() + " from " + 
                          (username != null ? username : "unknown"));
        
        try {
            switch (message.getType()) {
                case LOGIN:
                    handleLogin(message);
                    break;
                    
                case LOGOUT:
                    handleLogout(message);
                    break;
                    
                case HEARTBEAT:
                    // Respond to heartbeat
                    sendMessage(new Message(MessageType.HEARTBEAT, "pong"));
                    break;
                    
                case CHAT_MESSAGE:
                    // Broadcast chat message to all clients
                    server.broadcast(message);
                    break;
                    
                case DISCONNECT:
                    disconnect();
                    break;
                    
                default:
                    System.out.println("Unhandled message type: " + message.getType());
            }
        } catch (IOException e) {
            System.err.println("Error handling message: " + e.getMessage());
        }
    }
    
    /**
     * Handle user login notification
     */
    private void handleLogin(Message message) throws IOException {
        this.userId = message.getSenderId();
        this.username = message.getSenderName();
        
        // Notify all clients that user is online
        Message notification = Message.userOnline(username);
        server.broadcast(notification);
        
        System.out.println("✓ User logged in: " + username + " (ID: " + userId + ")");
    }
    
    /**
     * Handle user logout notification
     */
    private void handleLogout(Message message) throws IOException {
        if (username != null) {
            // Notify all clients that user is offline
            Message notification = Message.userOffline(username);
            server.broadcast(notification);
            
            System.out.println("✓ User logged out: " + username);
        }
        disconnect();
    }
    
    /**
     * Send message to this client
     */
    public void sendMessage(Message message) throws IOException {
        if (out != null && connected) {
            synchronized (out) {
                out.writeObject(message);
                out.flush();
            }
        }
    }
    
    /**
     * Disconnect client
     */
    public void disconnect() {
        connected = false;
        
        // Close streams
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            System.err.println("Error closing client connection: " + e.getMessage());
        }
        
        // Remove from server
        server.removeClient(this);
    }
    
    /**
     * Check if client is connected
     */
    public boolean isConnected() {
        return connected && !socket.isClosed();
    }
    
    /**
     * Get user ID
     */
    public int getUserId() {
        return userId;
    }
    
    /**
     * Get username
     */
    public String getUsername() {
        return username;
    }
}
