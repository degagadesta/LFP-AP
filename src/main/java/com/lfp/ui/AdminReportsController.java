package com.lfp.ui;

import com.lfp.model.Place;
import com.lfp.model.Report;
import com.lfp.model.ReportStatus;
import com.lfp.rmi.PlaceService;
import com.lfp.rmi.ReportService;
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
 * Controller for Admin Manage Reports screen
 */
public class AdminReportsController {
    
    @FXML private ComboBox<String> statusFilter;
    @FXML private ProgressIndicator loadingIndicator;
    @FXML private Label errorLabel;
    @FXML private Label successLabel;
    @FXML private TableView<Report> reportsTable;
    @FXML private TableColumn<Report, String> idColumn;
    @FXML private TableColumn<Report, String> placeColumn;
    @FXML private TableColumn<Report, String> reasonColumn;
    @FXML private TableColumn<Report, String> reporterColumn;
    @FXML private TableColumn<Report, String> statusColumn;
    @FXML private TableColumn<Report, Void> actionsColumn;
    
    private ReportService reportService;
    private PlaceService placeService;
    private ObservableList<Report> reportsList = FXCollections.observableArrayList();
    
    @FXML
    public void initialize() {
        setupTable();
        setupStatusFilter();
        connectToRMI();
    }
    
    private void setupTable() {
        idColumn.setCellValueFactory(data -> 
            new SimpleStringProperty(String.valueOf(data.getValue().getId())));
        
        placeColumn.setCellValueFactory(data -> {
            Report report = data.getValue();
            return new SimpleStringProperty("Place ID: " + report.getPlaceId());
        });
        
        reasonColumn.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getReason()));
        
        reporterColumn.setCellValueFactory(data -> 
            new SimpleStringProperty("User " + data.getValue().getReportedBy()));
        
        statusColumn.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getStatus().toString()));
        
        // Actions column with buttons
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button approveBtn = new Button("Approve & Delete Place");
            private final Button rejectBtn = new Button("Reject Report");
            private final HBox container = new HBox(5);
            
            {
                approveBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; " +
                                   "-fx-padding: 5 10; -fx-font-size: 11px; -fx-cursor: hand;");
                rejectBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; " +
                                  "-fx-padding: 5 10; -fx-font-size: 11px; -fx-cursor: hand;");
                
                container.setAlignment(Pos.CENTER);
                container.getChildren().addAll(approveBtn, rejectBtn);
                
                approveBtn.setOnAction(e -> {
                    Report report = getTableRow().getItem();
                    if (report != null) {
                        handleApproveReport(report);
                    }
                });
                
                rejectBtn.setOnAction(e -> {
                    Report report = getTableRow().getItem();
                    if (report != null) {
                        handleRejectReport(report);
                    }
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    Report report = getTableRow().getItem();
                    
                    // Show buttons only for pending reports
                    boolean isPending = report.getStatus() == ReportStatus.PENDING;
                    
                    if (isPending) {
                        setGraphic(container);
                    } else {
                        Label statusLabel = new Label(report.getStatus().toString());
                        statusLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-style: italic;");
                        setGraphic(statusLabel);
                    }
                }
            }
        });
        
        reportsTable.setItems(reportsList);
    }
    
    private void setupStatusFilter() {
        statusFilter.getItems().addAll("All", "Pending", "Resolved", "Rejected");
        statusFilter.setValue("All");
    }
    
    private void connectToRMI() {
        showLoading(true);
        
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Registry registry = LocateRegistry.getRegistry("localhost", 1099);
                reportService = (ReportService) registry.lookup("ReportService");
                placeService = (PlaceService) registry.lookup("PlaceService");
                return null;
            }
        };
        
        task.setOnSucceeded(e -> loadReports());
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
        loadReports();
    }
    
    private void loadReports() {
        showLoading(true);
        hideMessages();
        
        Task<List<Report>> task = new Task<>() {
            @Override
            protected List<Report> call() throws Exception {
                String filter = statusFilter.getValue();
                
                if ("All".equals(filter)) {
                    return reportService.getAllReports();
                } else {
                    ReportStatus status = ReportStatus.valueOf(filter.toUpperCase());
                    return reportService.getReportsByStatus(status);
                }
            }
        };
        
        task.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                reportsList.clear();
                reportsList.addAll(task.getValue());
                showLoading(false);
            });
        });
        
        task.setOnFailed(e -> {
            Platform.runLater(() -> {
                showError("Failed to load reports: " + task.getException().getMessage());
                showLoading(false);
            });
        });
        
        new Thread(task).start();
    }
    
    private void handleApproveReport(Report report) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Approve Report");
        alert.setHeaderText("Approve this report and DELETE the place?");
        alert.setContentText("This will:\n1. Mark the report as RESOLVED\n2. DELETE the reported place permanently\n\nThis action cannot be undone!");
        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                performApproveReport(report);
            }
        });
    }
    
    private void performApproveReport(Report report) {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                // First delete the place
                boolean placeDeleted = placeService.deletePlace(report.getPlaceId());
                if (!placeDeleted) {
                    throw new Exception("Failed to delete place");
                }
                
                // Then update report status
                return reportService.updateReportStatus(
                    report.getId(), 
                    ReportStatus.RESOLVED, 
                    "Place deleted by admin"
                );
            }
        };
        
        task.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                if (task.getValue()) {
                    showSuccess("Report approved and place deleted successfully");
                    loadReports();
                } else {
                    showError("Failed to approve report");
                }
            });
        });
        
        task.setOnFailed(e -> {
            Platform.runLater(() -> showError("Error: " + task.getException().getMessage()));
        });
        
        new Thread(task).start();
    }
    
    private void handleRejectReport(Report report) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Reject Report");
        alert.setHeaderText("Reject this report?");
        alert.setContentText("This will dismiss the report and keep the place.");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                performRejectReport(report);
            }
        });
    }
    
    private void performRejectReport(Report report) {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                return reportService.updateReportStatus(
                    report.getId(), 
                    ReportStatus.REJECTED, 
                    "Report dismissed by admin"
                );
            }
        };
        
        task.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                if (task.getValue()) {
                    showSuccess("Report rejected successfully");
                    loadReports();
                } else {
                    showError("Failed to reject report");
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
    private void handleManagePlaces() {
        navigateTo("/fxml/admin-places.fxml", "LFP - Manage Places");
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
            
            Stage stage = (Stage) reportsTable.getScene().getWindow();
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
