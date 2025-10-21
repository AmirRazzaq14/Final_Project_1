package com.example.demo;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert;
import javafx.event.ActionEvent;

public class LoginController {
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    @FXML
    public void handleLogin() {
        String inputEmail = emailField.getText();
        String inputPassword = passwordField.getText();
        boolean found = false;
        for (UserCred user : RegisterController.userList) {
            if (user.getEmail().equals(inputEmail) && user.getPassword().equals(inputPassword)) {
                found = true;
                break;
            }
        }
        if (found) {
            showAlert("Login successful!");
            // Optionally: Switch to "home" or dashboard scene here
        } else {
            showAlert("Invalid email or password.");
        }
    }

    @FXML
    public void handleShowRegister(ActionEvent event) {
        SceneSwitcher.switchScene(event, "/com/example/demo/register.fxml", "Register - ShapeShift");
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.show();
    }
}
