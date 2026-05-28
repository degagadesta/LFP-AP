package com.lfp.ui;

import com.lfp.model.User;
import com.lfp.rmi.UserService;
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
 * Controller for Login and Register screen
 */
public class LoginController {
    
    @FXML private TextField loginUsername;
    @FXML private PasswordField loginPassword;
    @FXML private TextField registerUsername;
    @FXML private TextField registerEmail;
    @FXML private PasswordField registerPassword;
    @FXML private PasswordField registerConfirmPassword;
    @FXML private Label loginError;
    @FXML private Label registerError;
    @FXML private Label registerSuccess;
    @FXML private ProgressIndicator loginLoading;
    @FXML private ProgressIndicator registerLoading;
    @FXML private Button loginButton;
    @FXML private Button registerButton;
    
    private UserService userService;
    
    @FXML
    public void initialize() {
        connectToRMI();
    }
    
    private void connectToRMI() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Registry registry = LocateRegistry.getRegistry("localhost", 1099);
                userService = (UserService) registry.lookup("UserService");
                return null;
            }
        };
        
        task.setOnFailed(e -> {
            Platform.runLater(() -> {
                showLoginError("Failed to connect to server. Please ensure the server is running.");
            });
        });
        
        new Thread(task).start();
    }
    
    @FXML
    private void handleLogin() {
        String username = loginUsername.getText().trim();
        String password = loginPassword.getText();
        
        if (username.isEmpty() || password.isEmpty()) {
            showLoginError("Please enter username and password");
            return;
        }
        
        hideLoginError();
        setLoginFormEnabled(false);
        showLoginLoading(true);
        
        Task<User> task = new Task<>() {
            @Override
            protected User call() throws Exception {
                return userService.login(username, password);
            }
        };
        
        task.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                User user = task.getValue();
                if (user != null) {
                    SessionManager.getInstance().setCurrentUser(user);
                    
                    // Route based on user role
                    if (user.isAdmin()) {
                        navigateToAdminDashboard();
                    } else {
                        navigateToHome();
                    }
                } else {
                    showLoginError("Invalid username or password");
                    setLoginFormEnabled(true);
                    showLoginLoading(false);
                }
            });
        });
        
        task.setOnFailed(e -> {
            Platform.runLater(() -> {
                showLoginError("Login failed: " + task.getException().getMessage());
                setLoginFormEnabled(true);
                showLoginLoading(false);
            });
        });
        
        new Thread(task).start();
    }
    
    @FXML
    private void handleRegister() {
        String username = registerUsername.getText().trim();
        String email = registerEmail.getText().trim();
        String password = registerPassword.getText();
        String confirmPassword = registerConfirmPassword.getText();
        
        // Validation
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showRegisterError("All fields are required");
            return;
        }
        
        if (username.length() < 3) {
            showRegisterError("Username must be at least 3 characters");
            return;
        }
        
        if (!email.contains("@")) {
            showRegisterError("Please enter a valid email address");
            return;
        }
        
        if (password.length() < 6) {
            showRegisterError("Password must be at least 6 characters");
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            showRegisterError("Passwords do not match");
            return;
        }
        
        hideRegisterMessages();
        setRegisterFormEnabled(false);
        showRegisterLoading(true);
        
        Task<Integer> task = new Task<>() {
            @Override
            protected Integer call() throws Exception {
                User newUser = new User();
                newUser.setUsername(username);
                newUser.setEmail(email);
                newUser.setPassword(password);
                newUser.setAdmin(false);
                
                return userService.register(newUser);
            }
        };
        
        task.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                int userId = task.getValue();
                if (userId > 0) {
                    showRegisterSuccess("Registration successful! You can now login.");
                    clearRegisterForm();
                } else {
                    showRegisterError("Registration failed. Username or email may already exist.");
                }
                setRegisterFormEnabled(true);
                showRegisterLoading(false);
            });
        });
        
        task.setOnFailed(e -> {
            Platform.runLater(() -> {
                showRegisterError("Registration failed: " + task.getException().getMessage());
                setRegisterFormEnabled(true);
                showRegisterLoading(false);
            });
        });
        
        new Thread(task).start();
    }
    
    private void navigateToHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/home.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) loginUsername.getScene().getWindow();
            Scene scene = new Scene(root, 1200, 800);
            stage.setScene(scene);
            stage.setTitle("LFP - Home");
        } catch (Exception e) {
            showLoginError("Failed to load home screen");
        }
    }
    
    private void navigateToAdminDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin-dashboard.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) loginUsername.getScene().getWindow();
            Scene scene = new Scene(root, 1200, 800);
            stage.setScene(scene);
            stage.setTitle("LFP - Admin Dashboard");
        } catch (Exception e) {
            showLoginError("Failed to load admin dashboard");
        }
    }
    
    private void clearRegisterForm() {
        registerUsername.clear();
        registerEmail.clear();
        registerPassword.clear();
        registerConfirmPassword.clear();
    }
    
    private void setLoginFormEnabled(boolean enabled) {
        loginUsername.setDisable(!enabled);
        loginPassword.setDisable(!enabled);
        loginButton.setDisable(!enabled);
    }
    
    private void setRegisterFormEnabled(boolean enabled) {
        registerUsername.setDisable(!enabled);
        registerEmail.setDisable(!enabled);
        registerPassword.setDisable(!enabled);
        registerConfirmPassword.setDisable(!enabled);
        registerButton.setDisable(!enabled);
    }
    
    private void showLoginLoading(boolean show) {
        loginLoading.setVisible(show);
        loginLoading.setManaged(show);
    }
    
    private void showRegisterLoading(boolean show) {
        registerLoading.setVisible(show);
        registerLoading.setManaged(show);
    }
    
    private void showLoginError(String message) {
        loginError.setText(message);
        loginError.setVisible(true);
        loginError.setManaged(true);
    }
    
    private void hideLoginError() {
        loginError.setVisible(false);
        loginError.setManaged(false);
    }
    
    private void showRegisterError(String message) {
        registerError.setText(message);
        registerError.setVisible(true);
        registerError.setManaged(true);
    }
    
    private void showRegisterSuccess(String message) {
        registerSuccess.setText(message);
        registerSuccess.setVisible(true);
        registerSuccess.setManaged(true);
    }
    
    private void hideRegisterMessages() {
        registerError.setVisible(false);
        registerError.setManaged(false);
        registerSuccess.setVisible(false);
        registerSuccess.setManaged(false);
    }
}
