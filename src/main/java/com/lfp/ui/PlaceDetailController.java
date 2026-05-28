package com.lfp.ui;

import com.lfp.model.Place;
import com.lfp.model.Report;
import com.lfp.rmi.PlaceService;
import com.lfp.rmi.ReportService;
import com.lfp.util.SessionManager;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Controller for Place Detail screen
 */
public class PlaceDetailController {
    
    @FXML private Label nameLabel;
    @FXML private Label categoryLabel;
    @FXML private Label locationLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label wifiRatingLabel;
    @FXML private Label serviceRatingLabel;
    @FXML private Label overallRatingLabel;
    @FXML private Button favoriteButton;
    @FXML private Button reportButton;
    @FXML private Label statusLabel;
    
    private Place place;
    private PlaceService placeService;
    private ReportService reportService;
    private boolean isFavorite = false;
    
    public void setPlace(Place place) {
        this.place = place;
        connectToRMI();
    }
    
    private void connectToRMI() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Registry registry = LocateRegistry.getRegistry("localhost", 1099);
                placeService = (PlaceService) registry.lookup("PlaceService");
                reportService = (ReportService) registry.lookup("ReportService");
                return null;
            }
        };
        
        task.setOnSucceeded(e -> {
            displayPlaceDetails();
            checkIfFavorite();
        });
        
        new Thread(task).start();
    }
    
    private void displayPlaceDetails() {
        Platform.runLater(() -> {
            nameLabel.setText(place.getName());
            categoryLabel.setText(place.getCategory());
            locationLabel.setText(place.getAddress());
            descriptionLabel.setText(place.getDescription() != null ? place.getDescription() : "No description available");
            
            wifiRatingLabel.setText(String.format("%.1f / 5.0", place.getRatingWifi()));
            serviceRatingLabel.setText(String.format("%.1f / 5.0", place.getRatingService()));
            overallRatingLabel.setText(String.format("%.1f", place.getRatingOverall()));
        });
    }
    
    private void checkIfFavorite() {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                int userId = SessionManager.getInstance().getCurrentUser().getId();
                return placeService.isFavorite(userId, place.getId());
            }
        };
        
        task.setOnSucceeded(e -> {
            isFavorite = task.getValue();
            updateFavoriteButton();
        });
        
        new Thread(task).start();
    }
    
    @FXML
    private void handleToggleFavorite() {
        int userId = SessionManager.getInstance().getCurrentUser().getId();
        
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                if (isFavorite) {
                    return placeService.removeFromFavorites(userId, place.getId());
                } else {
                    return placeService.addToFavorites(userId, place.getId());
                }
            }
        };
        
        task.setOnSucceeded(e -> {
            if (task.getValue()) {
                isFavorite = !isFavorite;
                updateFavoriteButton();
                showStatus(isFavorite ? "Added to favorites" : "Removed from favorites");
            }
        });
        
        task.setOnFailed(e -> showStatus("Failed to update favorites"));
        
        new Thread(task).start();
    }
    
    @FXML
    private void handleReport() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Report Place");
        dialog.setHeaderText("Report: " + place.getName());
        dialog.setContentText("Reason:");
        
        dialog.showAndWait().ifPresent(reason -> {
            if (!reason.trim().isEmpty()) {
                submitReport(reason);
            }
        });
    }
    
    private void submitReport(String reason) {
        Task<Integer> task = new Task<>() {
            @Override
            protected Integer call() throws Exception {
                Report report = new Report();
                report.setPlaceId(place.getId());
                report.setUserId(SessionManager.getInstance().getCurrentUser().getId());
                report.setReason(reason);
                
                return reportService.createReport(report);
            }
        };
        
        task.setOnSucceeded(e -> {
            showStatus("Report submitted successfully");
        });
        
        task.setOnFailed(e -> showStatus("Failed to submit report"));
        
        new Thread(task).start();
    }
    
    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/home.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) nameLabel.getScene().getWindow();
            Scene scene = new Scene(root, 1200, 800);
            stage.setScene(scene);
            stage.setTitle("LFP - Home");
        } catch (Exception e) {
            showStatus("Failed to go back");
        }
    }
    
    private void updateFavoriteButton() {
        Platform.runLater(() -> {
            if (isFavorite) {
                favoriteButton.setText("❤ Remove from Favorites");
                favoriteButton.getStyleClass().remove("primary-button");
                favoriteButton.getStyleClass().add("danger-button");
            } else {
                favoriteButton.setText("♡ Add to Favorites");
                favoriteButton.getStyleClass().remove("danger-button");
                favoriteButton.getStyleClass().add("primary-button");
            }
        });
    }
    
    private void showStatus(String message) {
        Platform.runLater(() -> {
            statusLabel.setText(message);
            statusLabel.setVisible(true);
            
            // Auto-hide after 3 seconds
            new Thread(() -> {
                try {
                    Thread.sleep(3000);
                    Platform.runLater(() -> statusLabel.setVisible(false));
                } catch (InterruptedException e) {
                    // Ignore
                }
            }).start();
        });
    }
}
