package com.example.demo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MyWorkoutPlanController {
    
    @FXML private Label planTitleLabel;
    @FXML private Label planNameLabel;
    @FXML private Label planDatesLabel;
    @FXML private VBox weeklyPlanContainer;
    @FXML private Label workoutsCompletedLabel;
    @FXML private Label exercisesLoggedLabel;
    @FXML private HBox connectionWarningBox;
    @FXML private Label connectionWarningLabel;
    
    private String currentUserEmail;
    private WorkoutPlan currentPlan;
    
    @FXML
    public void initialize() {
        currentUserEmail = SessionManager.getCurrentEmail();
        
        // Check Firebase connection
        if (!FirebaseConnectionManager.isConnected()) {
            connectionWarningBox.setVisible(true);
            connectionWarningLabel.setText("Firebase connection not available. Using local data storage.");
        } else {
            connectionWarningBox.setVisible(false);
        }
        
        loadWorkoutPlan();
        updateProgressSummary();
    }
    
    private void loadWorkoutPlan() {
        if (currentUserEmail == null) {
            showNoPlanMessage();
            return;
        }
        
        currentPlan = DataManager.getWorkoutPlan(currentUserEmail);
        
        if (currentPlan == null) {
            showNoPlanMessage();
            return;
        }
        
        // Display plan info
        planNameLabel.setText(currentPlan.getPlanName());
        String dateRange = currentPlan.getStartDate().format(DateTimeFormatter.ofPattern("MMM d, yyyy")) + 
                          " - " + currentPlan.getEndDate().format(DateTimeFormatter.ofPattern("MMM d, yyyy"));
        planDatesLabel.setText("Week " + currentPlan.getCurrentWeek() + " | " + dateRange);
        
        // Display weekly plan
        displayWeeklyPlan(currentPlan);
    }
    
    private void showNoPlanMessage() {
        planNameLabel.setText("No Workout Plan Generated");
        planDatesLabel.setText("Create a profile to generate your personalized workout plan");
        weeklyPlanContainer.getChildren().clear();
        
        Label noPlanLabel = new Label("No workout plan available. Click 'Create/Update Plan' to generate one.");
        noPlanLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 16px; -fx-padding: 20;");
        weeklyPlanContainer.getChildren().add(noPlanLabel);
    }
    
    private void displayWeeklyPlan(WorkoutPlan plan) {
        weeklyPlanContainer.getChildren().clear();
        
        List<DailyWorkout> weeklyPlan = plan.getWeeklyPlan();
        if (weeklyPlan == null || weeklyPlan.isEmpty()) {
            showNoPlanMessage();
            return;
        }
        
        for (DailyWorkout daily : weeklyPlan) {
            VBox dayCard = createDayCard(daily);
            weeklyPlanContainer.getChildren().add(dayCard);
        }
    }
    
    private VBox createDayCard(DailyWorkout daily) {
        VBox card = new VBox(15);
        card.setStyle("-fx-background-color: rgba(102,126,234,0.1); -fx-background-radius: 12; -fx-padding: 20; -fx-border-color: rgba(102,126,234,0.3); -fx-border-width: 2; -fx-border-radius: 12;");
        
        HBox header = new HBox(15);
        Label dayLabel = new Label(daily.getDay().toString());
        dayLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        Label focusLabel = new Label(daily.getFocusArea());
        focusLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #667eea; -fx-font-weight: bold;");
        header.getChildren().addAll(dayLabel, focusLabel);
        
        card.getChildren().add(header);
        
        if (daily.isRestDay()) {
            Label restLabel = new Label("Rest Day - Recovery is important for progress!");
            restLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #7f8c8d; -fx-font-style: italic;");
            card.getChildren().add(restLabel);
        } else {
            VBox exercisesBox = new VBox(10);
            for (WorkoutExercise exercise : daily.getExercises()) {
                HBox exerciseRow = new HBox(10);
                Label exerciseLabel = new Label(exercise.toString());
                exerciseLabel.setStyle("-fx-font-size: 15px; -fx-text-fill: #2c3e50;");
                
                Button logBtn = new Button("Log");
                logBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 12px; -fx-background-radius: 8; -fx-pref-width: 60; -fx-pref-height: 30; -fx-cursor: hand;");
                String exerciseName = exercise.getName();
                logBtn.setOnAction(e -> handleQuickLogExercise(exerciseName));
                
                exerciseRow.getChildren().addAll(exerciseLabel, logBtn);
                exercisesBox.getChildren().add(exerciseRow);
            }
            card.getChildren().add(exercisesBox);
        }
        
        return card;
    }
    
    private void updateProgressSummary() {
        if (currentUserEmail == null) return;
        
        // Get workout sessions for this week
        List<WorkoutSession> sessions = DataManager.getWorkoutSessions(currentUserEmail);
        LocalDate weekStart = LocalDate.now().minusDays(LocalDate.now().getDayOfWeek().getValue() - 1);
        
        int workoutsThisWeek = 0;
        int exercisesThisWeek = 0;
        
        for (WorkoutSession session : sessions) {
            if (session.getDate().isAfter(weekStart.minusDays(1))) {
                workoutsThisWeek++;
                if (session.getExercises() != null) {
                    exercisesThisWeek += session.getExercises().size();
                }
            }
        }
        
        workoutsCompletedLabel.setText("Workouts Completed: " + workoutsThisWeek);
        exercisesLoggedLabel.setText("Exercises Logged: " + exercisesThisWeek);
    }
    
    @FXML
    private void handleCreatePlan(ActionEvent event) {
        SceneSwitcher.switchScene(event, "/com/example/demo/profile_setup.fxml", "Profile Setup - ShapeShift");
    }
    
    @FXML
    private void handleStartWorkout(ActionEvent event) {
        if (currentPlan == null) {
            showAlert("Please create a workout plan first.");
            return;
        }
        SceneSwitcher.switchScene(event, "/com/example/demo/personal_customization.fxml", "Personal Customization - ShapeShift");
    }
    
    @FXML
    private void handleLogWorkout(ActionEvent event) {
        SceneSwitcher.switchScene(event, "/com/example/demo/personal_customization.fxml", "Personal Customization - ShapeShift");
    }
    
    @FXML
    private void handleViewProgress(ActionEvent event) {
        SceneSwitcher.switchScene(event, "/com/example/demo/progress_dashboard.fxml", "Progress Dashboard - ShapeShift");
    }
    
    @FXML
    private void handleBack(ActionEvent event) {
        SceneSwitcher.switchScene(event, "/com/example/demo/workout_home.fxml", "Workout Home");
    }
    
    private void handleQuickLogExercise(String exerciseName) {
        // Navigate to Personal Customization
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/example/demo/personal_customization.fxml"));
            javafx.scene.Parent root = loader.load();
            PersonalCustomizationController controller = loader.getController();
            // Pre-fill exercise name
            if (controller != null && exerciseName != null) {
                controller.preFillExercise(exerciseName);
            }
            
            javafx.stage.Stage stage = (javafx.stage.Stage) planNameLabel.getScene().getWindow();
            double width = stage.getWidth();
            double height = stage.getHeight();
            stage.setScene(new javafx.scene.Scene(root, width, height));
            stage.setTitle("Personal Customization - ShapeShift");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback navigation
            showAlert("Navigating to Personal Customization...");
        }
    }
    
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("ShapeShift");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

