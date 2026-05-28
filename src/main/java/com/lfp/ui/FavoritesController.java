package com.lfp.ui;

import com.lfp.model.Place;
import com.lfp.rmi.PlaceService;
import com.lfp.util.SessionManager;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
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
 * Controller for Favorites screen
 */
public class FavoritesController {
    
    @FXML private Label countLabel;
    @FXML private ProgressIndicator loadingIndicator;
    @FXML private Label errorLabel;
    @FXML private VBox emptyState;
    @FXML private VBox favoritesContainer;
    
    private PlaceService placeService;
    
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
                return null;
            }
        };
        
        task.setOnSucceeded(e -> loadFavorites());
        task.setOnFailed(e -> {
            Platform.runLater(() -> {
                showError("Failed to connect to server");
                showLoading(false);
            });
        });
        
        new Thread(task).start();
    }
    
    private void loadFavorites() {
        showLoading(true);
        hideError();
        
        Task<List<Place>> task = new Task<>() {
            @Override
            protected List<Place> call() throws Exception {
                int userId = SessionManager.getInstance().getCurrentUser().getId();
                return placeService.getUserFavorites(userId);
            }
        };
        
        task.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                List<Place> favorites = task.getValue();
                displayFavorites(favorites);
                showLoading(false);
            });
        });
        
        task.setOnFailed(e -> {
            Platform.runLater(() -> {
                showError("Failed to load favorites: " + task.getException().getMessage());
                showLoading(false);
            });
        });
        
        new Thread(task).start();
    }
    
    private void displayFavorites(List<Place> favorites) {
        favoritesContainer.getChildren().clear();
        
        if (favorites == null || favorites.isEmpty()) {
            countLabel.setText("(0 places)");
            emptyState.setVisible(true);
            emptyState.setManaged(true);
            return;
        }
        
        countLabel.setText("(" + favorites.size() + " places)");
        emptyState.setVisible(false);
        emptyState.setManaged(false);
        
        for (Place place : favorites) {
            favoritesContainer.getChildren().add(createFavoriteCard(place));
        }
    }
    
    private HBox createFavoriteCard(Place place) {
        HBox card = new HBox(20);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 8; " +
                     "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        
        // Left side - Place info
        VBox infoBox = new VBox(8);
        HBox.setHgrow(infoBox, Priority.ALWAYS);
        
        Label nameLabel = new Label(place.getName());
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        HBox categoryLocationBox = new HBox(15);
        Label categoryLabel = new Label(place.getCategory());
        categoryLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 14px;");
        Label locationLabel = new Label(place.getAddress());
        locationLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 14px;");
        categoryLocationBox.getChildren().addAll(categoryLabel, new Label("•"), locationLabel);
        
        HBox ratingsBox = new HBox(20);
        Label wifiLabel = new Label(String.format("WiFi: %.1f ⭐", place.getRatingWifi()));
        wifiLabel.setStyle("-fx-font-size: 14px;");
        Label serviceLabel = new Label(String.format("Service: %.1f ⭐", place.getRatingService()));
        serviceLabel.setStyle("-fx-font-size: 14px;");
        Label overallLabel = new Label(String.format("Overall: %.1f ⭐", place.getRatingOverall()));
        overallLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        ratingsBox.getChildren().addAll(wifiLabel, serviceLabel, overallLabel);
        
        infoBox.getChildren().addAll(nameLabel, categoryLocationBox, ratingsBox);
        
        // Right side - Actions
        VBox actionsBox = new VBox(10);
        actionsBox.setAlignment(Pos.CENTER_RIGHT);
        
        Button viewButton = new Button("View Details");
        viewButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; " +
                           "-fx-padding: 8 20; -fx-background-radius: 5; -fx-cursor: hand;");
        viewButton.setOnAction(e -> viewPlaceDetails(place));
        
        Button removeButton = new Button("Remove");
        removeButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; " +
                             "-fx-padding: 8 20; -fx-background-radius: 5; -fx-cursor: hand;");
        removeButton.setOnAction(e -> removeFromFavorites(place));
        
        actionsBox.getChildren().addAll(viewButton, removeButton);
        
        card.getChildren().addAll(infoBox, actionsBox);
        
        return card;
    }
    
    private void viewPlaceDetails(Place place) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/place-detail.fxml"));
            Parent root = loader.load();
            
            PlaceDetailController controller = loader.getController();
            controller.setPlace(place);
            
            Stage stage = (Stage) favoritesContainer.getScene().getWindow();
            Scene scene = new Scene(root, 1200, 800);
            stage.setScene(scene);
            stage.setTitle("LFP - Place Details");
        } catch (Exception e) {
            showError("Failed to open place details");
        }
    }
    
    private void removeFromFavorites(Place place) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Remove Favorite");
        alert.setHeaderText("Remove from favorites?");
        alert.setContentText("Are you sure you want to remove " + place.getName() + " from your favorites?");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                performRemoveFavorite(place);
            }
        });
    }
    
    private void performRemoveFavorite(Place place) {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                int userId = SessionManager.getInstance().getCurrentUser().getId();
                return placeService.removeFromFavorites(userId, place.getId());
            }
        };
        
        task.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                if (task.getValue()) {
                    loadFavorites(); // Reload the list
                } else {
                    showError("Failed to remove from favorites");
                }
            });
        });
        
        task.setOnFailed(e -> {
            Platform.runLater(() -> showError("Error: " + task.getException().getMessage()));
        });
        
        new Thread(task).start();
    }
    
    @FXML
    private void handleHome() {
        navigateTo("/fxml/home.fxml", "LFP - Home");
    }
    
    @FXML
    private void handleContribute() {
        navigateTo("/fxml/contribute.fxml", "LFP - Contribute");
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
            
            Stage stage = (Stage) favoritesContainer.getScene().getWindow();
            Scene scene = new Scene(root, 1200, 800);
            stage.setScene(scene);
            stage.setTitle(title);
        } catch (Exception e) {
            showError("Navigation failed");
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
    
    private void hideError() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }
}
