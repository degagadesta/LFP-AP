package com.lfp.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Utility class for navigation between screens
 * Ensures consistent window settings across all screens
 */
public class NavigationUtil {
    
    /**
     * Navigate to a new screen with default settings
     * @param currentStage Current stage
     * @param fxmlPath Path to FXML file
     * @param title Window title
     * @param width Window width
     * @param height Window height
     */
    public static void navigateTo(Stage currentStage, String fxmlPath, String title, int width, int height) {
        try {
            FXMLLoader loader = new FXMLLoader(NavigationUtil.class.getResource(fxmlPath));
            Parent root = loader.load();
            
            Scene scene = new Scene(root, width, height);
            
            // Configure stage
            currentStage.setScene(scene);
            currentStage.setTitle(title);
            currentStage.setResizable(true);
            currentStage.setMinWidth(800);
            currentStage.setMinHeight(600);
            
            // Add F11 fullscreen toggle
            scene.setOnKeyPressed(event -> {
                if (event.getCode().toString().equals("F11")) {
                    currentStage.setFullScreen(!currentStage.isFullScreen());
                }
            });
            
        } catch (Exception e) {
            System.err.println("Navigation failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Navigate to a new screen with custom minimum size
     * @param currentStage Current stage
     * @param fxmlPath Path to FXML file
     * @param title Window title
     * @param width Window width
     * @param height Window height
     * @param minWidth Minimum width
     * @param minHeight Minimum height
     */
    public static void navigateTo(Stage currentStage, String fxmlPath, String title, 
                                  int width, int height, int minWidth, int minHeight) {
        try {
            FXMLLoader loader = new FXMLLoader(NavigationUtil.class.getResource(fxmlPath));
            Parent root = loader.load();
            
            Scene scene = new Scene(root, width, height);
            
            // Configure stage
            currentStage.setScene(scene);
            currentStage.setTitle(title);
            currentStage.setResizable(true);
            currentStage.setMinWidth(minWidth);
            currentStage.setMinHeight(minHeight);
            
            // Add F11 fullscreen toggle
            scene.setOnKeyPressed(event -> {
                if (event.getCode().toString().equals("F11")) {
                    currentStage.setFullScreen(!currentStage.isFullScreen());
                }
            });
            
        } catch (Exception e) {
            System.err.println("Navigation failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Navigate and maximize window
     * @param currentStage Current stage
     * @param fxmlPath Path to FXML file
     * @param title Window title
     */
    public static void navigateToMaximized(Stage currentStage, String fxmlPath, String title) {
        navigateTo(currentStage, fxmlPath, title, 1200, 800);
        currentStage.setMaximized(true);
    }
    
    /**
     * Navigate and enter fullscreen
     * @param currentStage Current stage
     * @param fxmlPath Path to FXML file
     * @param title Window title
     */
    public static void navigateToFullscreen(Stage currentStage, String fxmlPath, String title) {
        navigateTo(currentStage, fxmlPath, title, 1200, 800);
        currentStage.setFullScreen(true);
    }
}
