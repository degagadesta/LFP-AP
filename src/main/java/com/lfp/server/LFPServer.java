package com.lfp.server;

import com.lfp.rmi.PlaceService;
import com.lfp.rmi.PlaceServiceImpl;
import com.lfp.rmi.ReportService;
import com.lfp.rmi.ReportServiceImpl;
import com.lfp.rmi.UserService;
import com.lfp.rmi.UserServiceImpl;
import com.lfp.util.DatabaseUtil;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Main server application for LFP-AP
 * Initializes RMI registry and registers all services
 * Starts socket server for real-time communication
 */
public class LFPServer {
    private static final int RMI_PORT = 1099;
    private static final int SOCKET_PORT = 8888;
    
    private static Registry registry;
    private static SocketServer socketServer;
    
    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║     Laptop Friendly Places - Server Starting...         ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println();
        
        try {
            // Step 1: Test database connection
            System.out.println("[1/4] Testing database connection...");
            if (!DatabaseUtil.getInstance().testConnection()) {
                System.err.println("✗ Database connection failed!");
                System.err.println("Please check:");
                System.err.println("  - MySQL is running");
                System.err.println("  - Database 'lfp_ap_db' exists");
                System.err.println("  - Credentials in database.properties are correct");
                return;
            }
            System.out.println("✓ Database connection successful");
            System.out.println();
            
            // Step 2: Start RMI Registry
            System.out.println("[2/4] Starting RMI Registry on port " + RMI_PORT + "...");
            try {
                registry = LocateRegistry.createRegistry(RMI_PORT);
                System.out.println("✓ RMI Registry started on port " + RMI_PORT);
            } catch (RemoteException e) {
                // Registry might already exist
                System.out.println("⚠ RMI Registry already running, using existing registry");
                registry = LocateRegistry.getRegistry(RMI_PORT);
            }
            System.out.println();
            
            // Step 3: Register RMI Services
            System.out.println("[3/4] Registering RMI services...");
            
            // Register UserService
            UserService userService = new UserServiceImpl();
            registry.rebind("UserService", userService);
            System.out.println("✓ UserService registered");
            
            // Register PlaceService
            PlaceService placeService = new PlaceServiceImpl();
            registry.rebind("PlaceService", placeService);
            System.out.println("✓ PlaceService registered");
            
            // Register ReportService
            ReportService reportService = new ReportServiceImpl();
            registry.rebind("ReportService", reportService);
            System.out.println("✓ ReportService registered");
            System.out.println();
            
            // Step 4: Start Socket Server
            System.out.println("[4/4] Starting Socket Server on port " + SOCKET_PORT + "...");
            socketServer = new SocketServer(SOCKET_PORT);
            Thread socketThread = new Thread(socketServer);
            socketThread.start();
            System.out.println("✓ Socket Server started on port " + SOCKET_PORT);
            System.out.println();
            
            // Server is ready
            System.out.println("╔══════════════════════════════════════════════════════════╗");
            System.out.println("║          ✓ LFP Server is running successfully!          ║");
            System.out.println("╚══════════════════════════════════════════════════════════╝");
            System.out.println();
            System.out.println("Server Information:");
            System.out.println("  RMI Registry: localhost:" + RMI_PORT);
            System.out.println("  Socket Server: localhost:" + SOCKET_PORT);
            System.out.println("  Services: UserService, PlaceService, ReportService");
            System.out.println();
            System.out.println("Press Ctrl+C to stop the server");
            System.out.println();
            
            // Add shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("\n\nShutting down server...");
                shutdown();
                System.out.println("✓ Server stopped successfully");
            }));
            
            // Keep server running
            Thread.currentThread().join();
            
        } catch (Exception e) {
            System.err.println("✗ Server startup failed: " + e.getMessage());
            e.printStackTrace();
            shutdown();
        }
    }
    
    /**
     * Shutdown server gracefully
     */
    private static void shutdown() {
        try {
            // Stop socket server
            if (socketServer != null) {
                socketServer.stop();
                System.out.println("✓ Socket server stopped");
            }
            
            // Unbind RMI services
            if (registry != null) {
                try {
                    registry.unbind("UserService");
                    registry.unbind("PlaceService");
                    registry.unbind("ReportService");
                    System.out.println("✓ RMI services unbound");
                } catch (Exception e) {
                    // Ignore errors during shutdown
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error during shutdown: " + e.getMessage());
        }
    }
}
