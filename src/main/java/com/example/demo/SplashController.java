package com.example.demo;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
public class SplashController {
    @FXML
    public void handleGetStarted(ActionEvent event) {
        SceneSwitcher.switchScene(event, "/com/example/demo/login.fxml", "Login - ShapeShift"); // switched to login.fxml
    }
}
