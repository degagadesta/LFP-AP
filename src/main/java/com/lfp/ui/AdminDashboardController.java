package com.lfp.ui;

import com.lfp.model.Place;
import com.lfp.model.PlaceStatus;
import com.lfp.rmi.PlaceService;
import com.lfp.rmi.ReportService;
import com.lfp.util.SessionManager;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

/**
 * Controller for Admin Dashboard
 */
public class AdminDashboardController {
    
    @FXML private ProgressIndicator loadingIndicator;
    @FXML private Label errorLabel;
    @FXML private Label pendingPlacesLabel;
    @FXML private Label pendingReportsLabel;
    @FXML private Label totalPlacesLabel;
    @FXML private VBox recentPlacesContainer;
    
    private PlaceService placeService;
    private ReportService reportService;
    
    @FXML
    public void initialize() {
        connectToRMI();
    }
    
    private void connectToRMI() {
        showLoading(true);
        
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Registry registry = LocateRegistry.getRegistry("localhost", 1099);
                placeService = (PlaceService) registry.lookup("PlaceService");
                reportService = (ReportService) registry.lookup("ReportService");
                return null;
            }
        };
        
        task.setOnSucceeded(e -> loadDashboardData());
        task.setOnFailed(e -> {
            Platform.runLater(() -> {
                showError("Failed to connect to server");
                showLoading(false);
            });
        });
        
        new Thread(task).start();
    }
    
    private void loadDashboardData() {
        Task<Void> task = new Task<>() {
            private int pendingPlaces;
            private int pendingReports;
            private int totalPlaces;
            private List<Place> recentPending;
            
            @Override
            protected Void call() throws Exception {
                pendingPlaces = placeService.getPendingPlaceCount();
                pendingReports = reportService.getPendingReportCount();
                totalPlaces = placeService.getPlaceCount();
                recentPending = placeService.getPlacesByStatus(PlaceStatus.PENDING);
                return null;
            }
            
            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    pendingPlacesLabel.setText(String.valueOf(pendingPlaces));
                    pendingReportsLabel.setText(String.valueOf(pendingReports));
                    totalPlacesLabel.setText(String.valueOf(totalPlaces));
                    displayRecentPlaces(recentPending);
                    showLoading(false);
                });
            }
            
            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    showError("Failed to load dashboard data");
                    showLoading(false);
                });
            }
        };
        
        new Thread(task).start();
    }
    
    private void displayRecentPlaces(List<Place> places) {
        recentPlacesContainer.getChildren().clear();
        
        if (places == null || places.isEmpty()) {
            Label emptyLabel = new Label("No pending places");
            emptyLabel.setStyle("-fx-text-fill: #999; -fx-font-size: 14px;");
            recentPlacesContainer.getChildren().add(emptyLabel);
            return;
        }
        
        // Show only first 5
        int count = Math.min(5, places.size());
        for (int i = 0; i < count; i++) {
            recentPlacesContainer.getChildren().add(createPlacePreview(places.get(i)));
        }
    }
    
    private HBox createPlacePreview(Place place) {
        HBox card = new HBox(15);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 5; " +
                     "-fx-border-color: #ddd; -fx-border-radius: 5;");
        
        VBox infoBox = new VBox(5);
        HBox.setHgrow(infoBox, Priority.ALWAYS);
        
        Label nameLabel = new Label(place.getName());
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        Label categoryLabel = new Label(place.getCategory() + " • " + place.getAddress());
        categoryLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 13px;");
        
        infoBox.getChildren().addAll(nameLabel, categoryLabel);
        
        Button reviewButton = new Button("Review");
        reviewButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; " +
                             "-fx-padding: 5 15; -fx-background-radius: 3;");
        reviewButton.setOnAction(e -> handleManagePlaces());
        
        card.getChildren().addAll(infoBox, reviewButton);
        
        return card;
    }
    
    @FXML
    private void handleManagePlaces() {
        navigateTo("/fxml/admin-places.fxml", "LFP - Manage Places");
    }
    
    @FXML
    private void handleManageReports() {
        navigateTo("/fxml/admin-reports.fxml", "LFP - Manage Reports");
    }
    
    @FXML
    private void handleLogout() {
        SessionManager.getInstance().clearSession();
        navigateTo("/fxml/login.fxml", "LFP - Login");
    }
    
    private void navigateTo(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            
            Stage stage = (Stage) pendingPlacesLabel.getScene().getWindow();
            Scene scene = new Scene(root, 1200, 800);
            stage.setScene(scene);
            stage.setTitle(title);
        } catch (Exception e) {
            showError("Navigation failed: " + e.getMessage());
        }
    }
    
    private void showLoading(boolean show) {
        loadingIndicator.setVisible(show);
        loadingIndicator.setManaged(show);
    }
    
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }
}
