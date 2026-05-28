package com.lfp.ui;

import com.lfp.model.Place;
import com.lfp.model.PlaceStatus;
import com.lfp.rmi.PlaceService;
import com.lfp.util.SessionManager;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

/**
 * Controller for Admin Manage Places screen
 */
public class AdminPlacesController {
    
    @FXML private ComboBox<String> statusFilter;
    @FXML private ProgressIndicator loadingIndicator;
    @FXML private Label errorLabel;
    @FXML private Label successLabel;
    @FXML private TableView<Place> placesTable;
    @FXML private TableColumn<Place, String> idColumn;
    @FXML private TableColumn<Place, String> nameColumn;
    @FXML private TableColumn<Place, String> categoryColumn;
    @FXML private TableColumn<Place, String> locationColumn;
    @FXML private TableColumn<Place, String> wifiColumn;
    @FXML private TableColumn<Place, String> serviceColumn;
    @FXML private TableColumn<Place, String> statusColumn;
    @FXML private TableColumn<Place, Void> actionsColumn;
    
    private PlaceService placeService;
    private ObservableList<Place> placesList = FXCollections.observableArrayList();
    
    @FXML
    public void initialize() {
        setupTable();
        setupStatusFilter();
        connectToRMI();
    }
    
    private void setupTable() {
        idColumn.setCellValueFactory(data -> 
            new SimpleStringProperty(String.valueOf(data.getValue().getId())));
        
        nameColumn.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getName()));
        
        categoryColumn.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getCategory()));
        
        locationColumn.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getAddress()));
        
        wifiColumn.setCellValueFactory(data -> 
            new SimpleStringProperty(String.format("%.1f", data.getValue().getRatingWifi())));
        
        serviceColumn.setCellValueFactory(data -> 
            new SimpleStringProperty(String.format("%.1f", data.getValue().getRatingService())));
        
        statusColumn.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getStatus().toString()));
        
        // Actions column with buttons
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button approveBtn = new Button("Approve");
            private final Button rejectBtn = new Button("Reject");
            private final Button deleteBtn = new Button("Delete");
            private final HBox container = new HBox(5);
            
            {
                approveBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; " +
                                   "-fx-padding: 5 10; -fx-font-size: 11px;");
                rejectBtn.setStyle("-fx-background-color: #ff9800; -fx-text-fill: white; " +
                                  "-fx-padding: 5 10; -fx-font-size: 11px;");
                deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; " +
                                  "-fx-padding: 5 10; -fx-font-size: 11px;");
                
                container.setAlignment(Pos.CENTER);
                container.getChildren().addAll(approveBtn, rejectBtn, deleteBtn);
                
                approveBtn.setOnAction(e -> {
                    Place place = getTableView().getItems().get(getIndex());
                    handleApprove(place);
                });
                
                rejectBtn.setOnAction(e -> {
                    Place place = getTableView().getItems().get(getIndex());
                    handleReject(place);
                });
                
                deleteBtn.setOnAction(e -> {
                    Place place = getTableView().getItems().get(getIndex());
                    handleDelete(place);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Place place = getTableView().getItems().get(getIndex());
                    
                    // Show/hide buttons based on status
                    approveBtn.setVisible(place.getStatus() == PlaceStatus.PENDING);
                    approveBtn.setManaged(place.getStatus() == PlaceStatus.PENDING);
                    rejectBtn.setVisible(place.getStatus() == PlaceStatus.PENDING);
                    rejectBtn.setManaged(place.getStatus() == PlaceStatus.PENDING);
                    
                    setGraphic(container);
                }
            }
        });
        
        placesTable.setItems(placesList);
    }
    
    private void setupStatusFilter() {
        statusFilter.getItems().addAll("All", "Pending", "Approved", "Rejected");
        statusFilter.setValue("All");
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
        
        task.setOnSucceeded(e -> loadPlaces());
        task.setOnFailed(e -> {
            Platform.runLater(() -> {
                showError("Failed to connect to server");
                showLoading(false);
            });
        });
        
        new Thread(task).start();
    }
    
    @FXML
    private void handleFilterChange() {
        loadPlaces();
    }
    
    private void loadPlaces() {
        showLoading(true);
        hideMessages();
        
        Task<List<Place>> task = new Task<>() {
            @Override
            protected List<Place> call() throws Exception {
                String filter = statusFilter.getValue();
                
                if ("All".equals(filter)) {
                    return placeService.getAllPlaces();
                } else {
                    PlaceStatus status = PlaceStatus.valueOf(filter.toUpperCase());
                    return placeService.getPlacesByStatus(status);
                }
            }
        };
        
        task.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                placesList.clear();
                placesList.addAll(task.getValue());
                showLoading(false);
            });
        });
        
        task.setOnFailed(e -> {
            Platform.runLater(() -> {
                showError("Failed to load places: " + task.getException().getMessage());
                showLoading(false);
            });
        });
        
        new Thread(task).start();
    }
    
    private void handleApprove(Place place) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Approve Place");
        alert.setHeaderText("Approve this place?");
        alert.setContentText("Place: " + place.getName());
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                performApprove(place);
            }
        });
    }
    
    private void performApprove(Place place) {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                return placeService.approvePlace(place.getId());
            }
        };
        
        task.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                if (task.getValue()) {
                    showSuccess("Place approved successfully");
                    loadPlaces();
                } else {
                    showError("Failed to approve place");
                }
            });
        });
        
        task.setOnFailed(e -> {
            Platform.runLater(() -> showError("Error: " + task.getException().getMessage()));
        });
        
        new Thread(task).start();
    }
    
    private void handleReject(Place place) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Reject Place");
        alert.setHeaderText("Reject this place?");
        alert.setContentText("Place: " + place.getName());
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                performReject(place);
            }
        });
    }
    
    private void performReject(Place place) {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                return placeService.rejectPlace(place.getId());
            }
        };
        
        task.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                if (task.getValue()) {
                    showSuccess("Place rejected successfully");
                    loadPlaces();
                } else {
                    showError("Failed to reject place");
                }
            });
        });
        
        task.setOnFailed(e -> {
            Platform.runLater(() -> showError("Error: " + task.getException().getMessage()));
        });
        
        new Thread(task).start();
    }
    
    private void handleDelete(Place place) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Place");
        alert.setHeaderText("Delete this place permanently?");
        alert.setContentText("Place: " + place.getName() + "\nThis action cannot be undone!");
        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                performDelete(place);
            }
        });
    }
    
    private void performDelete(Place place) {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                return placeService.deletePlace(place.getId());
            }
        };
        
        task.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                if (task.getValue()) {
                    showSuccess("Place deleted successfully");
                    loadPlaces();
                } else {
                    showError("Failed to delete place");
                }
            });
        });
        
        task.setOnFailed(e -> {
            Platform.runLater(() -> showError("Error: " + task.getException().getMessage()));
        });
        
        new Thread(task).start();
    }
    
    @FXML
    private void handleDashboard() {
        navigateTo("/fxml/admin-dashboard.fxml", "LFP - Admin Dashboard");
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
            
            Stage stage = (Stage) placesTable.getScene().getWindow();
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
    
    private void showSuccess(String message) {
        successLabel.setText(message);
        successLabel.setVisible(true);
        successLabel.setManaged(true);
    }
    
    private void hideMessages() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        successLabel.setVisible(false);
        successLabel.setManaged(false);
    }
}
