package com.lfp.ui;

import com.lfp.model.Place;
import com.lfp.model.PlaceStatus;
import com.lfp.rmi.PlaceService;
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
 * Controller for Contribute Place screen
 */
public class ContributeController {
    
    @FXML private TextField nameField;
    @FXML private ComboBox<String> categoryCombo;
    @FXML private TextField locationField;
    @FXML private Slider wifiSlider;
    @FXML private Label wifiValueLabel;
    @FXML private Slider serviceSlider;
    @FXML private Label serviceValueLabel;
    @FXML private TextArea descriptionArea;
    @FXML private Label nameError;
    @FXML private Label categoryError;
    @FXML private Label locationError;
    @FXML private Label errorLabel;
    @FXML private Label successLabel;
    @FXML private Button submitButton;
    @FXML private ProgressIndicator loadingIndicator;
    
    private PlaceService placeService;
    
    @FXML
    public void initialize() {
        // Populate category dropdown
        categoryCombo.getItems().addAll("Cafe", "Library", "Coworking", "Restaurant", "Other");
        
        // Bind slider values to labels
        wifiSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            wifiValueLabel.setText(String.format("%.1f", newVal.doubleValue()));
        });
        
        serviceSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            serviceValueLabel.setText(String.format("%.1f", newVal.doubleValue()));
        });
        
        // Connect to RMI
        connectToRMI();
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
        
        task.setOnFailed(e -> showError("Failed to connect to server"));
        
        new Thread(task).start();
    }
    
    @FXML
    private void handleSubmit() {
        // Clear previous errors
        clearErrors();
        
        // Validate inputs
        String name = nameField.getText().trim();
        String category = categoryCombo.getValue();
        String location = locationField.getText().trim();
        String description = descriptionArea.getText().trim();
        double wifiRating = wifiSlider.getValue();
        double serviceRating = serviceSlider.getValue();
        
        boolean isValid = true;
        
        if (name.isEmpty()) {
            showFieldError(nameError, "Place name is required");
            isValid = false;
        }
        
        if (category == null) {
            showFieldError(categoryError, "Please select a category");
            isValid = false;
        }
        
        if (location.isEmpty()) {
            showFieldError(locationError, "Location is required");
            isValid = false;
        }
        
        if (!isValid) return;
        
        // Create place object
        Place place = new Place();
        place.setName(name);
        place.setCategory(category);
        place.setAddress(location);
        place.setDescription(description);
        place.setRatingWifi(wifiRating);
        place.setRatingService(serviceRating);
        place.setStatus(PlaceStatus.PENDING);
        place.setContributedBy(SessionManager.getInstance().getCurrentUser().getId());
        
        // Calculate overall rating
        double overall = (wifiRating + serviceRating) / 2.0;
        place.setRatingOverall(overall);
        
        // Submit place
        submitPlace(place);
    }
    
    private void submitPlace(Place place) {
        setFormEnabled(false);
        showLoading(true);
        
        Task<Integer> task = new Task<>() {
            @Override
            protected Integer call() throws Exception {
                return placeService.addPlace(place);
            }
        };
        
        task.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                showSuccess("Place submitted successfully! It will be reviewed by an admin.");
                clearForm();
                setFormEnabled(true);
                showLoading(false);
                
                // Auto-navigate back after 2 seconds
                new Thread(() -> {
                    try {
                        Thread.sleep(2000);
                        Platform.runLater(this::handleBack);
                    } catch (InterruptedException ex) {
                        // Ignore
                    }
                }).start();
            });
        });
        
        task.setOnFailed(e -> {
            Platform.runLater(() -> {
                showError("Failed to submit place: " + task.getException().getMessage());
                setFormEnabled(true);
                showLoading(false);
            });
        });
        
        new Thread(task).start();
    }
    
    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/home.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) nameField.getScene().getWindow();
            Scene scene = new Scene(root, 1200, 800);
            stage.setScene(scene);
            stage.setTitle("LFP - Home");
        } catch (Exception e) {
            showError("Failed to go back");
        }
    }
    
    private void clearForm() {
        nameField.clear();
        categoryCombo.setValue(null);
        locationField.clear();
        descriptionArea.clear();
        wifiSlider.setValue(3);
        serviceSlider.setValue(3);
    }
    
    private void clearErrors() {
        hideFieldError(nameError);
        hideFieldError(categoryError);
        hideFieldError(locationError);
        hideError(errorLabel);
        hideError(successLabel);
    }
    
    private void showFieldError(Label label, String message) {
        label.setText(message);
        label.setVisible(true);
        label.setManaged(true);
    }
    
    private void hideFieldError(Label label) {
        label.setVisible(false);
        label.setManaged(false);
    }
    
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }
    
    private void hideError(Label label) {
        label.setVisible(false);
        label.setManaged(false);
    }
    
    private void showSuccess(String message) {
        successLabel.setText(message);
        successLabel.setVisible(true);
        successLabel.setManaged(true);
    }
    
    private void setFormEnabled(boolean enabled) {
        nameField.setDisable(!enabled);
        categoryCombo.setDisable(!enabled);
        locationField.setDisable(!enabled);
        wifiSlider.setDisable(!enabled);
        serviceSlider.setDisable(!enabled);
        descriptionArea.setDisable(!enabled);
        submitButton.setDisable(!enabled);
    }
    
    private void showLoading(boolean show) {
        loadingIndicator.setVisible(show);
        loadingIndicator.setManaged(show);
    }
}
