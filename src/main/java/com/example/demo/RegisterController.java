package com.example.demo;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class RegisterController {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    @FXML
    public void handleRegister() {
        String first = firstNameField.getText();
        String last = lastNameField.getText();
        String email = emailField.getText();
        String pass = passwordField.getText();

        if (first.isEmpty() || last.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            showAlert("All fields are required!");
            return;
        }

        if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            showAlert("Invalid email format!");
            return;
        }

        showAlert("Account created successfully!");
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.show();
    }
}
