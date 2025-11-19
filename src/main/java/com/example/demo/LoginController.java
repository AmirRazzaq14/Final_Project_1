package com.example.demo;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert;
import javafx.event.ActionEvent;

import static com.example.demo.SceneSwitcher.switchScene;

public class LoginController {
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    @FXML
    public void handleLogin(ActionEvent event) {

        String inputEmail = emailField.getText();
        String inputPassword = passwordField.getText();

        boolean found = false;
        for (UserCred user : RegisterController.userList) {
            if (user.getEmail() != null && user.getPassword() != null &&
                    user.getEmail().equals(inputEmail) &&
                    user.getPassword().equals(inputPassword)) {
                found = true;
                break;
            }
        }
        switchScene(event, "/com/example/demo/workout_home.fxml", "Workout Home");

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
