package com.lfp.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Database utility class for managing database connections
 * Implements Singleton pattern for connection management
 */
public class DatabaseUtil {
    private static DatabaseUtil instance;
    private String url;
    private String username;
    private String password;
    
    // Private constructor for Singleton pattern
    private DatabaseUtil() {
        loadDatabaseProperties();
    }
    
    /**
     * Get singleton instance of DatabaseUtil
     */
    public static synchronized DatabaseUtil getInstance() {
        if (instance == null) {
            instance = new DatabaseUtil();
        }
        return instance;
    }
    
    /**
     * Load database properties from configuration file
     */
    private void loadDatabaseProperties() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("database.properties")) {
            if (input == null) {
                System.err.println("Unable to find database.properties");
                // Use default values
                this.url = "jdbc:mysql://localhost:3306/lfp_ap_db?useSSL=false&serverTimezone=UTC";
                this.username = "root";
                this.password = "";
                return;
            }
            props.load(input);
            this.url = props.getProperty("db.url");
            this.username = props.getProperty("db.username");
            
            // Handle empty password (XAMPP default)
            String pwd = props.getProperty("db.password");
            this.password = (pwd == null || pwd.trim().isEmpty()) ? "" : pwd.trim();
            
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✓ Database configuration loaded successfully");
            
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading database properties: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Get a new database connection
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public Connection getConnection() throws SQLException {
        try {
            Connection conn = DriverManager.getConnection(url, username, password);
            System.out.println("✓ Database connection established");
            return conn;
        } catch (SQLException e) {
            System.err.println("✗ Failed to connect to database: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Test database connection
     * @return true if connection is successful
     */
    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Database connection test failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Close database connection safely
     * @param conn Connection to close
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("✓ Database connection closed");
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
    
    /**
     * Get database URL
     */
    public String getUrl() {
        return url;
    }
    
    /**
     * Get database username
     */
    public String getUsername() {
        return username;
    }
}
