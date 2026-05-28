package com.lfp.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX Application Entry Point for LFP Client
 */
public class LFPClientApp extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the login screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            
            // Create scene
            Scene scene = new Scene(root, 900, 600);
            
            // Load CSS if available
            try {
                String css = getClass().getResource("/css/style.css").toExternalForm();
                scene.getStylesheets().add(css);
            } catch (Exception e) {
                System.out.println("CSS file not found, using default styles");
            }
            
            // Configure stage
            primaryStage.setTitle("Laptop Friendly Places - Login");
            primaryStage.setScene(scene);
            primaryStage.setResizable(true); // Make resizable
            primaryStage.setMaximized(false); // Start normal size
            
            // Set minimum size
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);
            
            // Add fullscreen toggle (F11 key)
            scene.setOnKeyPressed(event -> {
                if (event.getCode().toString().equals("F11")) {
                    primaryStage.setFullScreen(!primaryStage.isFullScreen());
                }
            });
            
            primaryStage.show();
            
            System.out.println("✓ Application started");
            System.out.println("✓ Press F11 for fullscreen");
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to load application: " + e.getMessage());
        }
    }
    
    @Override
    public void stop() {
        System.out.println("Application is closing...");
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
