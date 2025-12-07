package com.example.demo;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert;
import javafx.event.ActionEvent;

import static com.example.demo.SceneSwitcher.switchScene;

public class LoginController {
    private static String currentUserEmail;
    
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    @FXML
    public void handleLogin(ActionEvent event) {
        String inputEmail = emailField.getText();
        String inputPassword = passwordField.getText();

        if (inputEmail.isEmpty() || inputPassword.isEmpty()) {
            showAlert("Please enter both email and password.");
            return;
        }

        // Use FirebaseService for authentication
        FirebaseService firebaseService = FirebaseService.getInstance();
        boolean found = firebaseService.loginUser(inputEmail, inputPassword);
        
        if (found) {
            currentUserEmail = inputEmail; // Store current user
            SessionManager.setCurrentEmail(inputEmail); // Also set in SessionManager
            // Increment login count
            DataManager.incrementLoginCount(inputEmail);
            switchScene(event, "/com/example/demo/workout_home.fxml", "Workout Home");
        } else {
            showAlert("Invalid email or password. Please try again.");
        }
    }
    
    public static String getCurrentUserEmail() {
        return currentUserEmail;
    }
    
    public static void setCurrentUserEmail(String email) {
        currentUserEmail = email;
    }

    @FXML
    public void handleShowRegister(ActionEvent event) {
        switchScene(event, "/com/example/demo/register.fxml", "Register - ShapeShift");
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.show();
    }
}
