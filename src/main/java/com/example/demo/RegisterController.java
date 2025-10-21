package com.example.demo;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert;
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
        /* DEBUG: print out field values to verify injection!
        System.out.println("firstNameField: " + firstNameField);
        System.out.println("lastNameField: " + lastNameField);
        System.out.println("emailField: " + emailField);
        System.out.println("passwordField: " + passwordField);
        */

        String first = firstNameField.getText();
        String last = lastNameField.getText();
        String email = emailField.getText();
        String pass = passwordField.getText();

        /* DEBUG: print the actual text entered
        System.out.println("First: " + first);
        System.out.println("Last: " + last);
        System.out.println("Email: " + email);
        System.out.println("Pass: " + pass);
         */

        if (first.isEmpty() || last.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            showAlert("All fields are required!");
            return;
        }
        if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            showAlert("Invalid email format!");
            return;
        }

        // User list is in memory, not exported at all.
        UserCred user = new UserCred(first, last, email, pass);
        userList.add(user);

       /* Debugging
        System.out.println("After Registration:");
        for (UserCred u : userList) {
            System.out.println("User: " + u.getEmail() + " (" + u.getPassword() + ")");
        }
        */

        SceneSwitcher.switchScene(event, "login.fxml", "Login");
    }
    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.show();
    }
}
