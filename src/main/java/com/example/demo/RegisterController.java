package com.example.demo;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert;

import java.io.IOException;
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





        SceneSwitcher.switchScene(event, "login.fxml", "Login");
    }
    @FXML
    private void handleBackButton(ActionEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml")); // replace with your previous page
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.show();
    }



    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.show();
    }
}
