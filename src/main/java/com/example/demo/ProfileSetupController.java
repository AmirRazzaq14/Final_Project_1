package com.example.demo;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.event.ActionEvent;

import java.util.ArrayList;
import java.util.List;

public class ProfileSetupController {
    
    @FXML private ToggleButton goalBulk;
    @FXML private ToggleButton goalCut;
    @FXML private ToggleButton goalMaintain;
    @FXML private ToggleGroup goalGroup;
    @FXML private Label userEmailLabel;
    @FXML private ToggleButton expBeginner;
    @FXML private ToggleButton expIntermediate;
    @FXML private ToggleButton expAdvanced;
    @FXML private ToggleGroup expGroup;
    
    @FXML private Slider daysSlider;
    @FXML private Label daysLabel;
    
    @FXML private TextField currentWeightField;
    @FXML private TextField targetWeightField;
    @FXML private TextField bodyFatField;
    
    @FXML private CheckBox injuryKnee;
    @FXML private CheckBox injuryShoulder;
    @FXML private CheckBox injuryBack;
    @FXML private CheckBox injuryWrist;
    @FXML private CheckBox injuryAnkle;
    @FXML private HBox connectionWarningBox;
    @FXML private Label connectionWarningLabel;
    
    private String currentUserEmail;
    
    @FXML
    public void initialize() {
        // Check Firebase connection
        if (!FirebaseConnectionManager.isConnected()) {
            if (connectionWarningBox != null) {
                connectionWarningBox.setVisible(true);
                if (connectionWarningLabel != null) {
                    connectionWarningLabel.setText("Firebase connection not available. Profile will be saved locally.");
                }
            }
        } else {
            if (connectionWarningBox != null) {
                connectionWarningBox.setVisible(false);
            }
        }
        
        // Update days label when slider changes
        daysSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int days = newVal.intValue();
            daysLabel.setText(days + " days");
        });
        
        // Load existing profile if available
        currentUserEmail = getCurrentUserEmail();
        if (userEmailLabel != null) {
            if (currentUserEmail != null && !currentUserEmail.isBlank()) {
                userEmailLabel.setText("Signed in as: " + currentUserEmail);
            } else {
                userEmailLabel.setText("Please log in to save your profile.");
            }
        }
        if (currentUserEmail != null) {
            UserProfile existing = DataManager.getProfile(currentUserEmail);
            if (existing != null) {
                loadProfile(existing);
            }
        }
    }
    
    private String getCurrentUserEmail() {
        // Try to get from a static variable or session
        // For now, we'll use a simple approach - store in LoginController
        String str=new String("hello");
     return str="test";   //add back (return LoginController.getCurrentUserEmail();) once finished testing
    }
    
    private void loadProfile(UserProfile profile) {
        // Load goal
        if ("bulk".equals(profile.getGoal())) goalBulk.setSelected(true);
        else if ("cut".equals(profile.getGoal())) goalCut.setSelected(true);
        else if ("maintain".equals(profile.getGoal())) goalMaintain.setSelected(true);
        
        // Load experience
        if ("beginner".equals(profile.getExperienceLevel())) expBeginner.setSelected(true);
        else if ("intermediate".equals(profile.getExperienceLevel())) expIntermediate.setSelected(true);
        else if ("advanced".equals(profile.getExperienceLevel())) expAdvanced.setSelected(true);
        
        // Load days
        daysSlider.setValue(profile.getDaysPerWeek());
        
        // Load weights
        if (profile.getCurrentWeight() > 0) {
            currentWeightField.setText(String.valueOf(profile.getCurrentWeight()));
        }
        if (profile.getTargetWeight() > 0) {
            targetWeightField.setText(String.valueOf(profile.getTargetWeight()));
        }
        if (profile.getBodyFatPercentage() > 0) {
            bodyFatField.setText(String.valueOf(profile.getBodyFatPercentage()));
        }
        
        // Load injuries
        List<String> injuries = profile.getInjuries();
        if (injuries != null) {
            injuryKnee.setSelected(injuries.contains("knee"));
            injuryShoulder.setSelected(injuries.contains("shoulder"));
            injuryBack.setSelected(injuries.contains("back"));
            injuryWrist.setSelected(injuries.contains("wrist"));
            injuryAnkle.setSelected(injuries.contains("ankle"));
        }
    }
    
    @FXML
    private void handleGeneratePlan(ActionEvent event) {
        // Validate inputs
        if (goalGroup.getSelectedToggle() == null || expGroup.getSelectedToggle() == null) {
            showAlert("Please select your goal and experience level.");
            return;
        }
        
        // Get current user email
        String email = getCurrentUserEmail();
        if (email == null || email.isEmpty()) {
            showAlert("Please log in first.");
            return;
        }
        
        // Create or update profile
        UserProfile profile = DataManager.getProfile(email);
        if (profile == null) {
            profile = new UserProfile(email);
        }
        
        // Set goal
        ToggleButton selectedGoal = (ToggleButton) goalGroup.getSelectedToggle();
        String goal = selectedGoal.getText().toLowerCase();
        profile.setGoal(goal);
        
        // Set experience
        ToggleButton selectedExp = (ToggleButton) expGroup.getSelectedToggle();
        String experience = selectedExp.getText().toLowerCase();
        profile.setExperienceLevel(experience);
        
        // Set days per week
        int days = (int) daysSlider.getValue();
        profile.setDaysPerWeek(days);
        
        // Set weights
        try {
            if (!currentWeightField.getText().isEmpty()) {
                profile.setCurrentWeight(Double.parseDouble(currentWeightField.getText()));
            }
            if (!targetWeightField.getText().isEmpty()) {
                profile.setTargetWeight(Double.parseDouble(targetWeightField.getText()));
            }
            if (!bodyFatField.getText().isEmpty()) {
                profile.setBodyFatPercentage(Double.parseDouble(bodyFatField.getText()));
            }
        } catch (NumberFormatException e) {
            showAlert("Please enter valid numbers for weight and body fat percentage.");
            return;
        }
        
        // Set injuries
        List<String> injuries = new ArrayList<>();
        if (injuryKnee.isSelected()) injuries.add("knee");
        if (injuryShoulder.isSelected()) injuries.add("shoulder");
        if (injuryBack.isSelected()) injuries.add("back");
        if (injuryWrist.isSelected()) injuries.add("wrist");
        if (injuryAnkle.isSelected()) injuries.add("ankle");
        profile.setInjuries(injuries);
        
        // Check Firebase connection
        boolean usingLocal = !FirebaseConnectionManager.isConnected();  //add back (boolean usingLocal = !FirebaseConnectionManager.isConnected(); ) once done
        
        // Save profile
        DataManager.saveProfile(profile);
        
        // Generate workout plan
        WorkoutPlan plan = WorkoutPlanGenerator.generatePlan(profile);
        DataManager.saveWorkoutPlan(email, plan);
        
        // Show success and navigate to plan view
        String message = usingLocal ? 
            "Your personalized workout plan has been generated! (Saved locally - Firebase not connected)" : 
            "Your personalized workout plan has been generated!";
        showAlert(message);
        
        // Navigate to My Workout Plan page to see the generated plan
        try {
            SceneSwitcher.switchScene(event, "/com/example/demo/my_workout_plan.fxml", "My Workout Plan - ShapeShift");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Plan generated successfully! You can view it from the My Workout Plan page.");
        }
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

