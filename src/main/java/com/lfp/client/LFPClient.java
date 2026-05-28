package com.lfp.client;

import com.lfp.model.User;
import com.lfp.rmi.UserService;
import com.lfp.rmi.PlaceService;
import com.lfp.rmi.ReportService;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

/**
 * Simple console-based client for testing the LFP server
 * This is a temporary test client until the JavaFX GUI is implemented
 */
public class LFPClient {
    private static UserService userService;
    private static PlaceService placeService;
    private static ReportService reportService;
    private static User currentUser;
    private static Scanner scanner;
    
    public static void main(String[] args) {
        scanner = new Scanner(System.in);
        
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║     Laptop Friendly Places - Test Client                ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println();
        
        try {
            // Connect to RMI services
            System.out.println("[1/2] Connecting to RMI services...");
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            
            userService = (UserService) registry.lookup("UserService");
            System.out.println("✓ Connected to UserService");
            
            placeService = (PlaceService) registry.lookup("PlaceService");
            System.out.println("✓ Connected to PlaceService");
            
            reportService = (ReportService) registry.lookup("ReportService");
            System.out.println("✓ Connected to ReportService");
            System.out.println();
            
            // Show main menu
            showMainMenu();
            
        } catch (Exception e) {
            System.err.println("✗ Failed to connect to server: " + e.getMessage());
            System.err.println("\nMake sure the server is running!");
            System.err.println("Run: mvn exec:java \"-Dexec.mainClass=com.lfp.server.LFPServer\"");
        } finally {
            scanner.close();
        }
    }
    
    private static void showMainMenu() {
        while (true) {
            System.out.println("\n╔══════════════════════════════════════════════════════════╗");
            System.out.println("║                    MAIN MENU                             ║");
            System.out.println("╚══════════════════════════════════════════════════════════╝");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. View All Places");
            System.out.println("4. View Statistics");
            System.out.println("5. Exit");
            System.out.print("\nChoose an option: ");
            
            String choice = scanner.nextLine();
            
            switch (choice) {
                case "1":
                    login();
                    break;
                case "2":
                    register();
                    break;
                case "3":
                    viewAllPlaces();
                    break;
                case "4":
                    viewStatistics();
                    break;
                case "5":
                    System.out.println("\n✓ Goodbye!");
                    return;
                default:
                    System.out.println("✗ Invalid option. Please try again.");
            }
        }
    }
    
    private static void login() {
        System.out.println("\n╔══════════════════════════════════════════════════════════╗");
        System.out.println("║                       LOGIN                              ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        
        System.out.print("Email: ");
        String email = scanner.nextLine();
        
        System.out.print("Password: ");
        String password = scanner.nextLine();
        
        try {
            currentUser = userService.login(email, password);
            System.out.println("\n✓ Login successful!");
            System.out.println("Welcome, " + currentUser.getUsername() + "!");
            System.out.println("Role: " + currentUser.getRole());
            
            if (currentUser.isAdmin()) {
                showAdminMenu();
            } else {
                showUserMenu();
            }
            
        } catch (Exception e) {
            System.err.println("✗ Login failed: " + e.getMessage());
        }
    }
    
    private static void register() {
        System.out.println("\n╔══════════════════════════════════════════════════════════╗");
        System.out.println("║                     REGISTER                             ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        
        System.out.print("Username: ");
        String username = scanner.nextLine();
        
        System.out.print("Email: ");
        String email = scanner.nextLine();
        
        System.out.print("Password: ");
        String password = scanner.nextLine();
        
        try {
            currentUser = userService.register(username, email, password);
            System.out.println("\n✓ Registration successful!");
            System.out.println("Welcome, " + currentUser.getUsername() + "!");
            
            showUserMenu();
            
        } catch (Exception e) {
            System.err.println("✗ Registration failed: " + e.getMessage());
        }
    }
    
    private static void showUserMenu() {
        while (currentUser != null) {
            System.out.println("\n╔══════════════════════════════════════════════════════════╗");
            System.out.println("║                    USER MENU                             ║");
            System.out.println("╚══════════════════════════════════════════════════════════╝");
            System.out.println("1. View All Places");
            System.out.println("2. Search Places");
            System.out.println("3. View My Favorites");
            System.out.println("4. View Statistics");
            System.out.println("5. Logout");
            System.out.print("\nChoose an option: ");
            
            String choice = scanner.nextLine();
            
            switch (choice) {
                case "1":
                    viewAllPlaces();
                    break;
                case "2":
                    searchPlaces();
                    break;
                case "3":
                    viewFavorites();
                    break;
                case "4":
                    viewStatistics();
                    break;
                case "5":
                    currentUser = null;
                    System.out.println("\n✓ Logged out successfully!");
                    return;
                default:
                    System.out.println("✗ Invalid option. Please try again.");
            }
        }
    }
    
    private static void showAdminMenu() {
        while (currentUser != null) {
            System.out.println("\n╔══════════════════════════════════════════════════════════╗");
            System.out.println("║                   ADMIN MENU                             ║");
            System.out.println("╚══════════════════════════════════════════════════════════╝");
            System.out.println("1. View All Users");
            System.out.println("2. View All Places");
            System.out.println("3. View Pending Places");
            System.out.println("4. View All Reports");
            System.out.println("5. View Statistics");
            System.out.println("6. Logout");
            System.out.print("\nChoose an option: ");
            
            String choice = scanner.nextLine();
            
            switch (choice) {
                case "1":
                    viewAllUsers();
                    break;
                case "2":
                    viewAllPlaces();
                    break;
                case "3":
                    viewPendingPlaces();
                    break;
                case "4":
                    viewAllReports();
                    break;
                case "5":
                    viewStatistics();
                    break;
                case "6":
                    currentUser = null;
                    System.out.println("\n✓ Logged out successfully!");
                    return;
                default:
                    System.out.println("✗ Invalid option. Please try again.");
            }
        }
    }
    
    private static void viewAllUsers() {
        try {
            var users = userService.getAllUsers();
            System.out.println("\n╔══════════════════════════════════════════════════════════╗");
            System.out.println("║                    ALL USERS                             ║");
            System.out.println("╚══════════════════════════════════════════════════════════╝");
            System.out.println(String.format("%-5s %-20s %-30s %-10s %-10s", 
                "ID", "Username", "Email", "Role", "Status"));
            System.out.println("─".repeat(80));
            
            for (var user : users) {
                System.out.println(String.format("%-5d %-20s %-30s %-10s %-10s",
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getRole(),
                    user.isBlocked() ? "Blocked" : "Active"));
            }
            System.out.println("\nTotal users: " + users.size());
            
        } catch (Exception e) {
            System.err.println("✗ Error: " + e.getMessage());
        }
    }
    
    private static void viewAllPlaces() {
        try {
            var places = placeService.getAllPlaces();
            System.out.println("\n╔══════════════════════════════════════════════════════════╗");
            System.out.println("║                   ALL PLACES                             ║");
            System.out.println("╚══════════════════════════════════════════════════════════╝");
            System.out.println(String.format("%-5s %-30s %-15s %-12s %-8s", 
                "ID", "Name", "Category", "Status", "Rating"));
            System.out.println("─".repeat(80));
            
            for (var place : places) {
                System.out.println(String.format("%-5d %-30s %-15s %-12s %-8.1f",
                    place.getId(),
                    place.getName().length() > 30 ? place.getName().substring(0, 27) + "..." : place.getName(),
                    place.getCategory(),
                    place.getStatus(),
                    place.getRatingOverall()));
            }
            System.out.println("\nTotal places: " + places.size());
            
        } catch (Exception e) {
            System.err.println("✗ Error: " + e.getMessage());
        }
    }
    
    private static void viewPendingPlaces() {
        try {
            var places = placeService.getPlacesByStatus(com.lfp.model.PlaceStatus.PENDING);
            System.out.println("\n╔══════════════════════════════════════════════════════════╗");
            System.out.println("║                 PENDING PLACES                           ║");
            System.out.println("╚══════════════════════════════════════════════════════════╝");
            
            if (places.isEmpty()) {
                System.out.println("No pending places.");
                return;
            }
            
            System.out.println(String.format("%-5s %-30s %-15s %-20s", 
                "ID", "Name", "Category", "Address"));
            System.out.println("─".repeat(80));
            
            for (var place : places) {
                System.out.println(String.format("%-5d %-30s %-15s %-20s",
                    place.getId(),
                    place.getName().length() > 30 ? place.getName().substring(0, 27) + "..." : place.getName(),
                    place.getCategory(),
                    place.getAddress().length() > 20 ? place.getAddress().substring(0, 17) + "..." : place.getAddress()));
            }
            System.out.println("\nTotal pending: " + places.size());
            
        } catch (Exception e) {
            System.err.println("✗ Error: " + e.getMessage());
        }
    }
    
    private static void searchPlaces() {
        System.out.print("\nEnter search query: ");
        String query = scanner.nextLine();
        
        try {
            var places = placeService.searchPlaces(query);
            System.out.println("\n╔══════════════════════════════════════════════════════════╗");
            System.out.println("║                 SEARCH RESULTS                           ║");
            System.out.println("╚══════════════════════════════════════════════════════════╝");
            
            if (places.isEmpty()) {
                System.out.println("No places found matching: " + query);
                return;
            }
            
            System.out.println(String.format("%-5s %-30s %-15s %-8s", 
                "ID", "Name", "Category", "Rating"));
            System.out.println("─".repeat(70));
            
            for (var place : places) {
                System.out.println(String.format("%-5d %-30s %-15s %-8.1f",
                    place.getId(),
                    place.getName().length() > 30 ? place.getName().substring(0, 27) + "..." : place.getName(),
                    place.getCategory(),
                    place.getRatingOverall()));
            }
            System.out.println("\nFound " + places.size() + " place(s)");
            
        } catch (Exception e) {
            System.err.println("✗ Error: " + e.getMessage());
        }
    }
    
    private static void viewFavorites() {
        if (currentUser == null) {
            System.out.println("✗ Please login first.");
            return;
        }
        
        try {
            var favorites = placeService.getUserFavorites(currentUser.getId());
            System.out.println("\n╔══════════════════════════════════════════════════════════╗");
            System.out.println("║                  MY FAVORITES                            ║");
            System.out.println("╚══════════════════════════════════════════════════════════╝");
            
            if (favorites.isEmpty()) {
                System.out.println("You have no favorites yet.");
                return;
            }
            
            System.out.println(String.format("%-5s %-30s %-15s %-8s", 
                "ID", "Name", "Category", "Rating"));
            System.out.println("─".repeat(70));
            
            for (var place : favorites) {
                System.out.println(String.format("%-5d %-30s %-15s %-8.1f",
                    place.getId(),
                    place.getName().length() > 30 ? place.getName().substring(0, 27) + "..." : place.getName(),
                    place.getCategory(),
                    place.getRatingOverall()));
            }
            System.out.println("\nTotal favorites: " + favorites.size());
            
        } catch (Exception e) {
            System.err.println("✗ Error: " + e.getMessage());
        }
    }
    
    private static void viewAllReports() {
        try {
            var reports = reportService.getAllReports();
            System.out.println("\n╔══════════════════════════════════════════════════════════╗");
            System.out.println("║                   ALL REPORTS                            ║");
            System.out.println("╚══════════════════════════════════════════════════════════╝");
            
            if (reports.isEmpty()) {
                System.out.println("No reports.");
                return;
            }
            
            System.out.println(String.format("%-5s %-20s %-15s %-12s", 
                "ID", "Place", "Reported By", "Status"));
            System.out.println("─".repeat(60));
            
            for (var report : reports) {
                String reportedBy = report.getReportedBy();
                System.out.println(String.format("%-5d %-20s %-15s %-12s",
                    report.getId(),
                    report.getPlaceName() != null ? 
                        (report.getPlaceName().length() > 20 ? report.getPlaceName().substring(0, 17) + "..." : report.getPlaceName()) : "N/A",
                    reportedBy != null ? reportedBy : "User " + report.getUserId(),
                    report.getStatus()));
            }
            System.out.println("\nTotal reports: " + reports.size());
            
        } catch (Exception e) {
            System.err.println("✗ Error: " + e.getMessage());
        }
    }
    
    private static void viewStatistics() {
        try {
            int userCount = userService.getUserCount();
            int placeCount = placeService.getPlaceCount();
            int pendingPlaces = placeService.getPendingPlaceCount();
            int reportCount = reportService.getReportCount();
            int pendingReports = reportService.getPendingReportCount();
            
            System.out.println("\n╔══════════════════════════════════════════════════════════╗");
            System.out.println("║                   STATISTICS                             ║");
            System.out.println("╚══════════════════════════════════════════════════════════╝");
            System.out.println("Total Users:          " + userCount);
            System.out.println("Total Places:         " + placeCount);
            System.out.println("Pending Places:       " + pendingPlaces);
            System.out.println("Total Reports:        " + reportCount);
            System.out.println("Pending Reports:      " + pendingReports);
            
        } catch (Exception e) {
            System.err.println("✗ Error: " + e.getMessage());
        }
    }
}
//mvn exec:java "-Dexec.mainClass=com.lfp.client.LFPClientApp"
//.\RUN_SERVER.bat

