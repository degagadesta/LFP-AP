package com.lfp.ui;

import com.lfp.model.Place;
import com.lfp.model.PlaceStatus;
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
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

/**
 * Controller for Home screen - Browse places
 */
public class HomeController {
    
    @FXML private TextField searchField;
    @FXML private Button allButton;
    @FXML private Button newButton;
    @FXML private Button popularButton;
    @FXML private GridPane placesGrid;
    @FXML private ProgressIndicator loadingIndicator;
    @FXML private Label errorLabel;
    @FXML private Label usernameLabel;
    
    private PlaceService placeService;
    private String currentFilter = "all";
    
    @FXML
    public void initialize() {
        // Set username
        usernameLabel.setText("Welcome, " + SessionManager.getInstance().getCurrentUser().getUsername());
        
        // Connect to RMI and load places
        connectToRMI();
        
        // Set initial filter
        setActiveFilter(allButton);
    }
    
    private void connectToRMI() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Registry registry = LocateRegistry.getRegistry("localhost", 1099);
                placeService = (PlaceService) registry.lookup("PlaceService");
                return null;
            }
        };
        
        task.setOnSucceeded(e -> loadPlaces());
        task.setOnFailed(e -> showError("Failed to connect to server"));
        
        new Thread(task).start();
    }
    
    @FXML
    private void handleFilterAll() {
        currentFilter = "all";
        setActiveFilter(allButton);
        loadPlaces();
    }
    
    @FXML
    private void handleFilterNew() {
        currentFilter = "new";
        setActiveFilter(newButton);
        loadPlaces();
    }
    
    @FXML
    private void handleFilterPopular() {
        currentFilter = "popular";
        setActiveFilter(popularButton);
        loadPlaces();
    }
    
    @FXML
    private void handleSearch() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            loadPlaces();
            return;
        }
        
        showLoading(true);
        
        Task<List<Place>> task = new Task<>() {
            @Override
            protected List<Place> call() throws Exception {
                return placeService.searchPlaces(query);
            }
        };
        
        task.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                displayPlaces(task.getValue());
                showLoading(false);
            });
        });
        
        task.setOnFailed(e -> {
            Platform.runLater(() -> {
                showError("Search failed");
                showLoading(false);
            });
        });
        
        new Thread(task).start();
    }
    
    @FXML
    private void handleContribute() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/contribute.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) searchField.getScene().getWindow();
            Scene scene = new Scene(root, 900, 700);
            stage.setScene(scene);
            stage.setTitle("LFP - Contribute Place");
            stage.setResizable(true);
            stage.setMinWidth(800);
            stage.setMinHeight(600);
            
            // Add F11 fullscreen toggle
            scene.setOnKeyPressed(event -> {
                if (event.getCode().toString().equals("F11")) {
                    stage.setFullScreen(!stage.isFullScreen());
                }
            });
        } catch (Exception e) {
            showError("Failed to load contribute screen");
        }
    }
    
    @FXML
    private void handleFavorites() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/favorites.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) searchField.getScene().getWindow();
            Scene scene = new Scene(root, 1200, 800);
            stage.setScene(scene);
            stage.setTitle("LFP - My Favorites");
            stage.setResizable(true);
            stage.setMinWidth(1000);
            stage.setMinHeight(700);
            
            // Add F11 fullscreen toggle
            scene.setOnKeyPressed(event -> {
                if (event.getCode().toString().equals("F11")) {
                    stage.setFullScreen(!stage.isFullScreen());
                }
            });
        } catch (Exception e) {
            showError("Failed to load favorites screen");
        }
    }
    
    @FXML
    private void handleLogout() {
        SessionManager.getInstance().logout();
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) searchField.getScene().getWindow();
            Scene scene = new Scene(root, 900, 600);
            stage.setScene(scene);
            stage.setTitle("LFP - Login");
            stage.setResizable(true);
            stage.setMinWidth(800);
            stage.setMinHeight(600);
            
            // Add F11 fullscreen toggle
            scene.setOnKeyPressed(event -> {
                if (event.getCode().toString().equals("F11")) {
                    stage.setFullScreen(!stage.isFullScreen());
                }
            });
        } catch (Exception e) {
            showError("Failed to logout");
        }
    }
    
    private void loadPlaces() {
        showLoading(true);
        hideError();
        
        Task<List<Place>> task = new Task<>() {
            @Override
            protected List<Place> call() throws Exception {
                if ("all".equals(currentFilter)) {
                    return placeService.getPlacesByStatus(PlaceStatus.APPROVED);
                } else {
                    // For simplicity, just return approved places
                    // In a real app, you'd have separate methods for new/popular
                    return placeService.getPlacesByStatus(PlaceStatus.APPROVED);
                }
            }
        };
        
        task.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                displayPlaces(task.getValue());
                showLoading(false);
            });
        });
        
        task.setOnFailed(e -> {
            Platform.runLater(() -> {
                showError("Failed to load places");
                showLoading(false);
            });
        });
        
        new Thread(task).start();
    }
    
    private void displayPlaces(List<Place> places) {
        placesGrid.getChildren().clear();
        
        if (places == null || places.isEmpty()) {
            Label noPlaces = new Label("No places found");
            noPlaces.setStyle("-fx-font-size: 16px; -fx-text-fill: #7f8c8d;");
            placesGrid.add(noPlaces, 0, 0);
            return;
        }
        
        int col = 0;
        int row = 0;
        
        for (Place place : places) {
            VBox card = createPlaceCard(place);
            placesGrid.add(card, col, row);
            
            col++;
            if (col == 3) {
                col = 0;
                row++;
            }
        }
    }
    
    private VBox createPlaceCard(Place place) {
        VBox card = new VBox(10);
        card.getStyleClass().add("place-card");
        card.setPadding(new Insets(15));
        card.setPrefWidth(250);
        card.setAlignment(Pos.TOP_LEFT);
        card.setCursor(javafx.scene.Cursor.HAND);
        
        // Place name
        Label nameLabel = new Label(place.getName());
        nameLabel.getStyleClass().add("place-name");
        nameLabel.setWrapText(true);
        
        // Category
        Label categoryLabel = new Label(place.getCategory());
        categoryLabel.getStyleClass().add("place-category");
        
        // Location
        Label locationLabel = new Label(place.getAddress());
        locationLabel.getStyleClass().add("place-location");
        locationLabel.setWrapText(true);
        
        // Ratings
        HBox ratingsBox = new HBox(10);
        ratingsBox.setAlignment(Pos.CENTER_LEFT);
        
        Label wifiLabel = new Label("WiFi: " + String.format("%.1f", place.getRatingWifi()));
        wifiLabel.setStyle("-fx-font-size: 12px;");
        
        Label serviceLabel = new Label("Service: " + String.format("%.1f", place.getRatingService()));
        serviceLabel.setStyle("-fx-font-size: 12px;");
        
        ratingsBox.getChildren().addAll(wifiLabel, serviceLabel);
        
        // Overall rating badge
        Label ratingBadge = new Label(String.format("%.1f", place.getRatingOverall()));
        ratingBadge.getStyleClass().add("rating-badge");
        
        card.getChildren().addAll(nameLabel, categoryLabel, locationLabel, ratingsBox, ratingBadge);
        
        // Click handler
        card.setOnMouseClicked(e -> openPlaceDetail(place));
        
        return card;
    }
    
    private void openPlaceDetail(Place place) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/place-detail.fxml"));
            Parent root = loader.load();
            
            PlaceDetailController controller = loader.getController();
            controller.setPlace(place);
            
            Stage stage = (Stage) searchField.getScene().getWindow();
            Scene scene = new Scene(root, 800, 700);
            stage.setScene(scene);
            stage.setTitle("LFP - " + place.getName());
        } catch (Exception e) {
            showError("Failed to load place details");
        }
    }
    
    private void setActiveFilter(Button activeButton) {
        allButton.getStyleClass().remove("filter-tab-active");
        newButton.getStyleClass().remove("filter-tab-active");
        popularButton.getStyleClass().remove("filter-tab-active");
        
        activeButton.getStyleClass().add("filter-tab-active");
    }
    
    private void showLoading(boolean show) {
        loadingIndicator.setVisible(show);
        loadingIndicator.setManaged(show);
        placesGrid.setVisible(!show);
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
