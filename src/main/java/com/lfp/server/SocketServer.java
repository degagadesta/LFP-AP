package com.lfp.server;

import com.lfp.socket.Message;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Socket server for real-time communication with clients
 * Handles multiple concurrent client connections using thread pool
 */
public class SocketServer implements Runnable {
    private final int port;
    private ServerSocket serverSocket;
    private boolean running;
    private final ExecutorService threadPool;
    private final List<ClientHandler> clients;
    
    public SocketServer(int port) {
        this.port = port;
        this.running = false;
        this.threadPool = Executors.newFixedThreadPool(10); // Max 10 concurrent clients
        this.clients = new ArrayList<>();
    }
    
    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            running = true;
            System.out.println("Socket Server listening on port " + port);
            
            while (running) {
                try {
                    // Accept client connection
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("✓ New client connected: " + clientSocket.getInetAddress());
                    
                    // Create client handler
                    ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                    synchronized (clients) {
                        clients.add(clientHandler);
                    }
                    
                    // Handle client in thread pool
                    threadPool.execute(clientHandler);
                    
                } catch (IOException e) {
                    if (running) {
                        System.err.println("Error accepting client connection: " + e.getMessage());
                    }
                }
            }
            
        } catch (IOException e) {
            System.err.println("Socket Server error: " + e.getMessage());
        } finally {
            stop();
        }
    }
    
    /**
     * Broadcast message to all connected clients
     */
    public void broadcast(Message message) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                try {
                    client.sendMessage(message);
                } catch (IOException e) {
                    System.err.println("Error broadcasting to client: " + e.getMessage());
                }
            }
        }
        System.out.println("✓ Broadcast message to " + clients.size() + " clients: " + message.getType());
    }
    
    /**
     * Send message to specific client
     */
    public void sendToClient(ClientHandler client, Message message) {
        try {
            client.sendMessage(message);
        } catch (IOException e) {
            System.err.println("Error sending message to client: " + e.getMessage());
        }
    }
    
    /**
     * Remove disconnected client
     */
    public void removeClient(ClientHandler client) {
        synchronized (clients) {
            clients.remove(client);
        }
        System.out.println("✓ Client disconnected. Active clients: " + clients.size());
    }
    
    /**
     * Get number of connected clients
     */
    public int getClientCount() {
        synchronized (clients) {
            return clients.size();
        }
    }
    
    /**
     * Stop the socket server
     */
    public void stop() {
        running = false;
        
        // Close all client connections
        synchronized (clients) {
            for (ClientHandler client : clients) {
                client.disconnect();
            }
            clients.clear();
        }
        
        // Shutdown thread pool
        threadPool.shutdown();
        
        // Close server socket
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                System.err.println("Error closing server socket: " + e.getMessage());
            }
        }
    }
    
    /**
     * Check if server is running
     */
    public boolean isRunning() {
        return running;
    }
}
