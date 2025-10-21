package com.example.demo;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class RegisterController {
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    public static List<UserCred> userList = new ArrayList<>();

    @FXML
    public void handleRegister(ActionEvent event) {
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

        // List is saved in memory, not output anywhere
        UserCred user = new UserCred(first, last, email, pass);
        userList.add(user);

        //prompts user to login after registering
        SceneSwitcher.switchScene(event, "login.fxml", "Login");
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.show();
    }
}
