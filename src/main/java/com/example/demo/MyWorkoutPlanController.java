package com.example.demo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class MyWorkoutPlanController {
    
    @FXML private Label planTitleLabel;
    @FXML private VBox selectedWorkoutsContainer;
    @FXML private VBox loggedExercisesContainer;
    @FXML private HBox connectionWarningBox;
    @FXML private Label connectionWarningLabel;
    
    private String currentUserEmail;
    
    @FXML
    public void initialize() {
        // Try SessionManager first, then fallback to LoginController
        currentUserEmail = SessionManager.getCurrentEmail();
        if (currentUserEmail == null) {
            currentUserEmail = LoginController.getCurrentUserEmail();
        }
        
        // Check Firebase connection
        if (!FirebaseConnectionManager.isConnected()) {
            if (connectionWarningBox != null) {
                connectionWarningBox.setVisible(true);
                if (connectionWarningLabel != null) {
                    connectionWarningLabel.setText("Firebase connection not available. Using local data storage.");
                }
            }
        } else {
            if (connectionWarningBox != null) {
                connectionWarningBox.setVisible(false);
            }
        }
        
        loadSelectedWorkouts();
        loadLoggedExercises();
    }
    
    private void loadSelectedWorkouts() {
        if (currentUserEmail == null) {
            showNoWorkoutsMessage();
            return;
        }
        
        List<String> workouts = DataManager.getSelectedWorkouts(currentUserEmail);
        
        if (workouts == null || workouts.isEmpty()) {
            showNoWorkoutsMessage();
            return;
        }
        
        displaySelectedWorkouts(workouts);
    }
    
    private void showNoWorkoutsMessage() {
        if (selectedWorkoutsContainer == null) return;
        selectedWorkoutsContainer.getChildren().clear();
        
        Label noWorkoutsLabel = new Label("No workouts selected yet. Go to 'Filter & Select Custom Workouts' to add workouts to your plan.");
        noWorkoutsLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 16px; -fx-padding: 20; -fx-wrap-text: true;");
        selectedWorkoutsContainer.getChildren().add(noWorkoutsLabel);
    }
    
    private void displaySelectedWorkouts(List<String> workouts) {
        if (selectedWorkoutsContainer == null) return;
        
        selectedWorkoutsContainer.getChildren().clear();
        
        for (String workout : workouts) {
            VBox workoutCard = createWorkoutCard(workout);
            selectedWorkoutsContainer.getChildren().add(workoutCard);
        }
    }
    
    private VBox createWorkoutCard(String workoutName) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: rgba(102,126,234,0.1); -fx-background-radius: 12; -fx-padding: 20; -fx-border-color: rgba(102,126,234,0.3); -fx-border-width: 2; -fx-border-radius: 12;");
        
        Label workoutLabel = new Label(workoutName);
        workoutLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        card.getChildren().add(workoutLabel);
        return card;
    }
    
    private void loadLoggedExercises() {
        if (currentUserEmail == null || loggedExercisesContainer == null) {
            showNoLoggedExercisesMessage();
            return;
        }
        
        List<WorkoutSession> sessions = DataManager.getWorkoutSessions(currentUserEmail);
        
        if (sessions == null || sessions.isEmpty()) {
            showNoLoggedExercisesMessage();
            return;
        }
        
        // Collect all exercises from all sessions
        List<ExerciseLogEntry> allExercises = new ArrayList<>();
        for (WorkoutSession session : sessions) {
            if (session.getExercises() != null) {
                allExercises.addAll(session.getExercises());
            }
        }
        
        if (allExercises.isEmpty()) {
            showNoLoggedExercisesMessage();
            return;
        }
        
        // Sort by date (newest first)
        allExercises.sort((a, b) -> b.getDate().compareTo(a.getDate()));
        
        displayLoggedExercises(allExercises);
    }
    
    private void showNoLoggedExercisesMessage() {
        if (loggedExercisesContainer == null) return;
        loggedExercisesContainer.getChildren().clear();
        
        Label noExercisesLabel = new Label("No exercises logged yet. Go to 'Log Workout' to start logging your exercises.");
        noExercisesLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 16px; -fx-padding: 20; -fx-wrap-text: true;");
        loggedExercisesContainer.getChildren().add(noExercisesLabel);
    }
    
    private void displayLoggedExercises(List<ExerciseLogEntry> exercises) {
        if (loggedExercisesContainer == null) return;
        
        loggedExercisesContainer.getChildren().clear();
        
        for (ExerciseLogEntry exercise : exercises) {
            VBox exerciseCard = createExerciseCard(exercise);
            loggedExercisesContainer.getChildren().add(exerciseCard);
        }
    }
    
    private VBox createExerciseCard(ExerciseLogEntry exercise) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: rgba(39,174,96,0.1); -fx-background-radius: 12; -fx-padding: 15; -fx-border-color: rgba(39,174,96,0.3); -fx-border-width: 2; -fx-border-radius: 12;");
        
        HBox header = new HBox(10);
        Label exerciseName = new Label(exercise.getExerciseName());
        exerciseName.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        Label dateLabel = new Label(exercise.getDate().toString());
        dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");
        
        header.getChildren().addAll(exerciseName, dateLabel);
        
        VBox details = new VBox(5);
        if (exercise.getSets() > 0) {
            Label setsLabel = new Label("Sets: " + exercise.getSets());
            setsLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2c3e50;");
            details.getChildren().add(setsLabel);
        }
        if (exercise.getReps() > 0) {
            Label repsLabel = new Label("Reps: " + exercise.getReps());
            repsLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2c3e50;");
            details.getChildren().add(repsLabel);
        }
        if (exercise.getWeight() > 0) {
            Label weightLabel = new Label("Weight: " + String.format("%.1f lbs", exercise.getWeight()));
            weightLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2c3e50;");
            details.getChildren().add(weightLabel);
        }
        if (exercise.getRpe() > 0) {
            Label rpeLabel = new Label("RPE: " + exercise.getRpe() + "/10");
            rpeLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2c3e50;");
            details.getChildren().add(rpeLabel);
        }
        
        card.getChildren().addAll(header, details);
        return card;
    }
    
    @FXML
    private void handleLogWorkout(ActionEvent event) {
        if (currentUserEmail == null || currentUserEmail.isEmpty()) {
            showAlert("Please log in first.");
            return;
        }
        
        try {
            SceneSwitcher.switchScene(event, "/com/example/demo/personal_customization.fxml", "Personal Customization - ShapeShift");
        } catch (Exception e) {
            showAlert("Error navigating to workout log: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleViewProgress(ActionEvent event) {
        if (currentUserEmail == null || currentUserEmail.isEmpty()) {
            showAlert("Please log in first.");
            return;
        }
        
        try {
            SceneSwitcher.switchScene(event, "/com/example/demo/progress_dashboard.fxml", "Progress Dashboard - ShapeShift");
        } catch (Exception e) {
            showAlert("Error navigating to progress dashboard: " + e.getMessage());
            e.printStackTrace();
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

