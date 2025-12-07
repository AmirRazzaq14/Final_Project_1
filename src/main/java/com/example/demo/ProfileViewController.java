package com.example.demo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class ProfileViewController {
    
    @FXML private Label emailLabel;
    @FXML private Label loginCountLabel;
    @FXML private Label currentWeightLabel;
    @FXML private Label targetWeightLabel;
    @FXML private Label bodyFatLabel;
    @FXML private VBox injuriesContainer;
    
    private String currentUserEmail;
    private UserProfile currentProfile;
    
    @FXML
    public void initialize() {
        currentUserEmail = LoginController.getCurrentUserEmail();
        
        loadProfile();
    }
    
    private void loadProfile() {
        if (currentUserEmail == null || currentUserEmail.isEmpty()) {
            showNoProfileMessage();
            return;
        }
        
        currentProfile = DataManager.getProfile(currentUserEmail);
        
        if (currentProfile == null) {
            showNoProfileMessage();
            return;
        }
        
        // Display profile information
        emailLabel.setText("Email: " + currentProfile.getEmail());
        
        // Display login count
        int loginCount = DataManager.getLoginCount(currentUserEmail);
        loginCountLabel.setText("Times Logged In: " + loginCount);
        
        if (currentProfile.getCurrentWeight() > 0) {
            currentWeightLabel.setText("Current Weight: " + String.format("%.1f lbs", currentProfile.getCurrentWeight()));
        } else {
            currentWeightLabel.setText("Current Weight: Not set");
        }
        
        if (currentProfile.getTargetWeight() > 0) {
            targetWeightLabel.setText("Target Weight: " + String.format("%.1f lbs", currentProfile.getTargetWeight()));
        } else {
            targetWeightLabel.setText("Target Weight: Not set");
        }
        
        if (currentProfile.getBodyFatPercentage() > 0) {
            bodyFatLabel.setText("Body Fat Percentage: " + String.format("%.1f%%", currentProfile.getBodyFatPercentage()));
        } else {
            bodyFatLabel.setText("Body Fat Percentage: Not set");
        }
        
        // Display injuries
        injuriesContainer.getChildren().clear();
        List<String> injuries = currentProfile.getInjuries();
        if (injuries != null && !injuries.isEmpty()) {
            Label injuriesTitle = new Label("Injuries/Areas to Avoid:");
            injuriesTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #2c3e50;");
            injuriesContainer.getChildren().add(injuriesTitle);
            
            for (String injury : injuries) {
                Label injuryLabel = new Label("• " + capitalizeFirst(injury));
                injuryLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d; -fx-padding: 5 0 0 20;");
                injuriesContainer.getChildren().add(injuryLabel);
            }
        } else {
            Label noInjuries = new Label("No injuries recorded");
            noInjuries.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d; -fx-font-style: italic;");
            injuriesContainer.getChildren().add(noInjuries);
        }
    }
    
    private void showNoProfileMessage() {
        emailLabel.setText("Email: " + (currentUserEmail != null ? currentUserEmail : "Not logged in"));
        int loginCount = currentUserEmail != null ? DataManager.getLoginCount(currentUserEmail) : 0;
        loginCountLabel.setText("Times Logged In: " + loginCount);
        currentWeightLabel.setText("Current Weight: No profile created yet");
        targetWeightLabel.setText("Target Weight: No profile created yet");
        bodyFatLabel.setText("Body Fat Percentage: No profile created yet");
        injuriesContainer.getChildren().clear();
        Label noProfile = new Label("Please create a profile to see your information here.");
        noProfile.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d; -fx-font-style: italic;");
        injuriesContainer.getChildren().add(noProfile);
    }
    
    private String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
    
    @FXML
    private void handleBack(ActionEvent event) {
        SceneSwitcher.switchScene(event, "/com/example/demo/workout_home.fxml", "Workout Home");
    }
    
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("ShapeShift");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
