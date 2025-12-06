package com.example.demo;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.Node;

import java.time.LocalDate;

public class AddProgressController {
    
    @FXML private DatePicker datePicker;
    @FXML private TextField currentWeightField;
    @FXML private TextField targetWeightField;
    @FXML private TextField bodyFatField;
    @FXML private TextField caloriesConsumedField;
    @FXML private TextField caloriesTargetField;
    @FXML private TextField stepsField;
    
    @FXML
    public void initialize() {
        datePicker.setValue(LocalDate.now());
        
        // Load current profile values if available
        String email = LoginController.getCurrentUserEmail();
        if (email != null) {
            UserProfile profile = DataManager.getProfile(email);
            if (profile != null) {
                if (profile.getCurrentWeight() > 0) {
                    currentWeightField.setText(String.valueOf(profile.getCurrentWeight()));
                }
                if (profile.getTargetWeight() > 0) {
                    targetWeightField.setText(String.valueOf(profile.getTargetWeight()));
                }
                if (profile.getBodyFatPercentage() > 0) {
                    bodyFatField.setText(String.valueOf(profile.getBodyFatPercentage()));
                }
            }
        }
    }
    
    @FXML
    private void handleSave() {
        String email = LoginController.getCurrentUserEmail();
        if (email == null) {
            showAlert("Please log in first.");
            return;
        }
        
        LocalDate date = datePicker.getValue();
        if (date == null) {
            showAlert("Please select a date.");
            return;
        }
        
        try {
            // Create progress data
            ProgressData data = new ProgressData(date);
            
            // Get and validate current weight
            if (!currentWeightField.getText().trim().isEmpty()) {
                double currentWeight = Double.parseDouble(currentWeightField.getText().trim());
                if (currentWeight > 0) {
                    data.setWeight(currentWeight);
                }
            }
            
            // Get and validate body fat percentage
            if (!bodyFatField.getText().trim().isEmpty()) {
                double bodyFat = Double.parseDouble(bodyFatField.getText().trim());
                if (bodyFat > 0 && bodyFat <= 100) {
                    data.setBodyFatPercentage(bodyFat);
                }
            }
            
            // Get and validate calories consumed
            if (!caloriesConsumedField.getText().trim().isEmpty()) {
                int calories = Integer.parseInt(caloriesConsumedField.getText().trim());
                if (calories > 0) {
                    data.setCaloriesConsumed(calories);
                }
            }
            
            // Get and validate calorie target
            if (!caloriesTargetField.getText().trim().isEmpty()) {
                int target = Integer.parseInt(caloriesTargetField.getText().trim());
                if (target > 0) {
                    data.setCaloriesTarget(target);
                }
            }
            
            // Get and validate steps
            if (!stepsField.getText().trim().isEmpty()) {
                int steps = Integer.parseInt(stepsField.getText().trim());
                if (steps > 0) {
                    data.setSteps(steps);
                }
            }
            
            // Save progress data
            DataManager.saveProgress(email, data);
            
            // Update user profile with latest values
            UserProfile profile = DataManager.getProfile(email);
            if (profile == null) {
                profile = new UserProfile(email);
            }
            
            // Update profile with current weight if provided
            if (!currentWeightField.getText().trim().isEmpty()) {
                try {
                    double currentWeight = Double.parseDouble(currentWeightField.getText().trim());
                    if (currentWeight > 0) {
                        profile.setCurrentWeight(currentWeight);
                    }
                } catch (NumberFormatException e) {
                    // Ignore if invalid
                }
            }
            
            // Update profile with target weight if provided
            if (!targetWeightField.getText().trim().isEmpty()) {
                try {
                    double targetWeight = Double.parseDouble(targetWeightField.getText().trim());
                    if (targetWeight > 0) {
                        profile.setTargetWeight(targetWeight);
                    }
                } catch (NumberFormatException e) {
                    // Ignore if invalid
                }
            }
            
            // Update profile with body fat percentage if provided
            if (!bodyFatField.getText().trim().isEmpty()) {
                try {
                    double bodyFat = Double.parseDouble(bodyFatField.getText().trim());
                    if (bodyFat > 0 && bodyFat <= 100) {
                        profile.setBodyFatPercentage(bodyFat);
                    }
                } catch (NumberFormatException e) {
                    // Ignore if invalid
                }
            }
            
            // Save updated profile
            DataManager.saveProfile(profile);
            
            showAlert("Progress entry saved successfully!");
            
            // Navigate back to progress dashboard
            try {
                Node source = datePicker.getScene().getRoot();
                javafx.event.ActionEvent event = new javafx.event.ActionEvent(source, null);
                SceneSwitcher.switchScene(event, "/com/example/demo/progress_dashboard.fxml", "Progress Dashboard - ShapeShift");
            } catch (Exception e) {
                e.printStackTrace();
            }
            
        } catch (NumberFormatException e) {
            showAlert("Please enter valid numbers for all fields.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error saving progress entry: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleCancel(javafx.event.ActionEvent event) {
        SceneSwitcher.switchScene(event, "/com/example/demo/progress_dashboard.fxml", "Progress Dashboard - ShapeShift");
    }
    
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("ShapeShift");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
