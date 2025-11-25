package com.example.demo;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;

public class AddProgressController {
    
    @FXML private DatePicker datePicker;
    @FXML private TextField weightField;
    @FXML private TextField bodyFatField;
    @FXML private TextField caloriesField;
    @FXML private TextField calorieTargetField;
    @FXML private TextField stepsField;
    @FXML private TextField benchWeightField;
    @FXML private TextField benchRepsField;
    @FXML private TextField squatWeightField;
    @FXML private TextField squatRepsField;
    @FXML private TextField deadliftWeightField;
    @FXML private TextField deadliftRepsField;
    
    @FXML
    public void initialize() {
        datePicker.setValue(LocalDate.now());
        
        // Check Firebase connection
        if (!FirebaseConnectionManager.isConnected()) {
            showConnectionWarning();
        }
    }
    
    private void showConnectionWarning() {
        System.out.println("Warning: Firebase connection not available. Progress data will be saved locally.");
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
        
        ProgressData data = new ProgressData(date);
        
        try {
            if (!weightField.getText().isEmpty()) {
                data.setWeight(Double.parseDouble(weightField.getText()));
            }
            if (!bodyFatField.getText().isEmpty()) {
                data.setBodyFatPercentage(Double.parseDouble(bodyFatField.getText()));
            }
            if (!caloriesField.getText().isEmpty()) {
                data.setCaloriesConsumed(Integer.parseInt(caloriesField.getText()));
            }
            if (!calorieTargetField.getText().isEmpty()) {
                data.setCaloriesTarget(Integer.parseInt(calorieTargetField.getText()));
            }
            if (!stepsField.getText().isEmpty()) {
                data.setSteps(Integer.parseInt(stepsField.getText()));
            }
            
            // Handle PRs
            if (!benchWeightField.getText().isEmpty() && !benchRepsField.getText().isEmpty()) {
                double weight = Double.parseDouble(benchWeightField.getText());
                int reps = Integer.parseInt(benchRepsField.getText());
                PersonalRecord pr = new PersonalRecord("Bench Press", weight, reps, date);
                data.setBenchPR(pr);
            }
            
            if (!squatWeightField.getText().isEmpty() && !squatRepsField.getText().isEmpty()) {
                double weight = Double.parseDouble(squatWeightField.getText());
                int reps = Integer.parseInt(squatRepsField.getText());
                PersonalRecord pr = new PersonalRecord("Squat", weight, reps, date);
                data.setSquatPR(pr);
            }
            
            if (!deadliftWeightField.getText().isEmpty() && !deadliftRepsField.getText().isEmpty()) {
                double weight = Double.parseDouble(deadliftWeightField.getText());
                int reps = Integer.parseInt(deadliftRepsField.getText());
                PersonalRecord pr = new PersonalRecord("Deadlift", weight, reps, date);
                data.setDeadliftPR(pr);
            }
            
            boolean usingLocal = !FirebaseConnectionManager.isConnected();
            DataManager.saveProgress(email, data);
            
            String message = usingLocal ? 
                "Progress entry saved locally! (Firebase not connected)" : 
                "Progress entry saved successfully!";
            showAlert(message);
            handleCancel();
            
        } catch (NumberFormatException e) {
            showAlert("Please enter valid numbers for all fields.");
        }
    }
    
    @FXML
    private void handleCancel() {
        Stage stage = (Stage) datePicker.getScene().getWindow();
        stage.close();
    }
    
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("ShapeShift");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

