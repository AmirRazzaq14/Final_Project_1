package com.example.demo;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class PersonalCustomizationController {
    
    // Exercise Logging
    @FXML private TextField exerciseNameField;
    @FXML private TextField setsField;
    @FXML private TextField repsField;
    @FXML private TextField weightField;
    @FXML private Slider rpeSlider;
    @FXML private Label rpeLabel;
    @FXML private ListView<String> exerciseLogList;
    
    // Stopwatch
    @FXML private Label stopwatchLabel;
    @FXML private Button stopwatchStartBtn;
    @FXML private Button stopwatchPauseBtn;
    private Timeline stopwatchTimeline;
    private long stopwatchSeconds = 0;
    private boolean stopwatchRunning = false;
    
    // Rest Timer
    @FXML private Label restTimerLabel;
    @FXML private TextField restDurationField;
    @FXML private Button restTimerStartBtn;
    @FXML private ProgressBar restProgressBar;
    private Timeline restTimerTimeline;
    private int restTimerSeconds = 0;
    private int restTimerTotal = 60;
    private boolean restTimerRunning = false;
    
    // HIIT Timer
    @FXML private Label hiitTimerLabel;
    @FXML private Label hiitStatusLabel;
    @FXML private TextField workDurationField;
    @FXML private TextField restDurationHIITField;
    @FXML private TextField roundsField;
    @FXML private Button hiitStartBtn;
    @FXML private ProgressBar hiitProgressBar;
    private Timeline hiitTimeline;
    private int hiitCurrentTime = 0;
    private int hiitWorkDuration = 30;
    private int hiitRestDuration = 15;
    private int hiitRounds = 10;
    private int hiitCurrentRound = 0;
    private boolean hiitIsWorkPhase = true;
    private boolean hiitRunning = false;
    
    private ObservableList<String> exerciseLog = FXCollections.observableArrayList();
    private List<com.example.demo.ExerciseLogEntry> exerciseEntries = new ArrayList<>();
    
    @FXML private HBox connectionWarningBox;
    @FXML private Label connectionWarningLabel;
    
    @FXML
    public void initialize() {
        // Check Firebase connection
        if (!FirebaseConnectionManager.isConnected()) {
            if (connectionWarningBox != null) {
                connectionWarningBox.setVisible(true);
                if (connectionWarningLabel != null) {
                    connectionWarningLabel.setText("Firebase connection not available. Workout data will be saved locally.");
                }
            }
        } else {
            if (connectionWarningBox != null) {
                connectionWarningBox.setVisible(false);
            }
        }
        
        // Setup RPE slider listener
        rpeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int rpe = newVal.intValue();
            rpeLabel.setText(String.valueOf(rpe));
        });
        
        // Setup exercise log list
        exerciseLogList.setItems(exerciseLog);
        
        // Initialize timers
        initializeStopwatch();
        initializeRestTimer();
        initializeHIITTimer();
    }
    
    public void preFillExercise(String exerciseName) {
        if (exerciseNameField != null && exerciseName != null) {
            exerciseNameField.setText(exerciseName);
        }
    }
    
    // Exercise Logging Methods
    @FXML
    private void handleLogExercise() {
        String exerciseName = exerciseNameField.getText().trim();
        if (exerciseName.isEmpty()) {
            showAlert("Please enter an exercise name.");
            return;
        }
        
        try {
            int sets = setsField.getText().isEmpty() ? 0 : Integer.parseInt(setsField.getText());
            int reps = repsField.getText().isEmpty() ? 0 : Integer.parseInt(repsField.getText());
            double weight = weightField.getText().isEmpty() ? 0 : Double.parseDouble(weightField.getText());
            int rpe = (int) rpeSlider.getValue();
            
            String logEntry = String.format("%s | Sets: %d | Reps: %d | Weight: %.1f lbs | RPE: %d/10",
                    exerciseName, sets, reps, weight, rpe);
            exerciseLog.add(logEntry);
            
            exerciseEntries.add(new com.example.demo.ExerciseLogEntry(exerciseName, sets, reps, weight, rpe, LocalDate.now()));
            
            // Clear fields
            exerciseNameField.clear();
            setsField.clear();
            repsField.clear();
            weightField.clear();
            rpeSlider.setValue(5);
            
            // Auto-start rest timer if enabled
            if (restDurationField.getText() != null && !restDurationField.getText().isEmpty()) {
                try {
                    int restDuration = Integer.parseInt(restDurationField.getText());
                    if (restDuration > 0 && !restTimerRunning) {
                        startRestTimer(restDuration);
                    }
                } catch (NumberFormatException e) {
                    // Ignore
                }
            }
            
        } catch (NumberFormatException e) {
            showAlert("Please enter valid numbers for sets, reps, and weight.");
        }
    }
    
    @FXML
    private void handleClearLog() {
        exerciseLog.clear();
        exerciseEntries.clear();
    }
    
    @FXML
    private void handleSaveWorkout() {
        if (exerciseEntries.isEmpty()) {
            showAlert("No exercises to save.");
            return;
        }
        
        String email = SessionManager.getCurrentEmail();
        if (email == null) {
            showAlert("Please log in first.");
            return;
        }
        
        // Check Firebase connection
        boolean usingLocal = !FirebaseConnectionManager.isConnected();
        
        // Create a copy of exercise entries to save (to avoid reference issues)
        List<ExerciseLogEntry> exercisesToSave = new ArrayList<>(exerciseEntries);
        
        // Save workout session
        WorkoutSession session = new WorkoutSession(LocalDate.now(), LocalTime.now(), exercisesToSave, stopwatchSeconds);
        DataManager.saveWorkoutSession(email, session);
        
        // Also update progress data with PRs if any
        updateProgressWithPRs(email, exerciseEntries);
        
        String message = usingLocal ? 
            "Workout saved locally! (Firebase not connected)" : 
            "Workout saved successfully!";
        showAlert(message);
        handleClearLog();
        handleStopwatchReset();
    }
    
    private void updateProgressWithPRs(String email, List<com.example.demo.ExerciseLogEntry> entries) {
        // Check for PRs in logged exercises and update ProgressData
        ProgressData todayData = null;
        List<ProgressData> allProgress = DataManager.getProgress(email);
        
        for (ProgressData pd : allProgress) {
            if (pd.getDate().equals(LocalDate.now())) {
                todayData = pd;
                break;
            }
        }
        
        if (todayData == null) {
            todayData = new ProgressData(LocalDate.now());
        }
        
        // Check for bench, squat, deadlift PRs
        for (com.example.demo.ExerciseLogEntry entry : entries) {
            String exerciseName = entry.getExerciseName().toLowerCase();
            if (exerciseName.contains("bench") && entry.getWeight() > 0) {
                if (todayData.getBenchPR() == null || entry.getWeight() > todayData.getBenchPR().getWeight()) {
                    PersonalRecord pr = new PersonalRecord("Bench Press", entry.getWeight(), entry.getReps(), LocalDate.now());
                    todayData.setBenchPR(pr);
                }
            } else if (exerciseName.contains("squat") && entry.getWeight() > 0) {
                if (todayData.getSquatPR() == null || entry.getWeight() > todayData.getSquatPR().getWeight()) {
                    PersonalRecord pr = new PersonalRecord("Squat", entry.getWeight(), entry.getReps(), LocalDate.now());
                    todayData.setSquatPR(pr);
                }
            } else if (exerciseName.contains("deadlift") && entry.getWeight() > 0) {
                if (todayData.getDeadliftPR() == null || entry.getWeight() > todayData.getDeadliftPR().getWeight()) {
                    PersonalRecord pr = new PersonalRecord("Deadlift", entry.getWeight(), entry.getReps(), LocalDate.now());
                    todayData.setDeadliftPR(pr);
                }
            }
        }
        
        DataManager.saveProgress(email, todayData);
    }
    
    // Stopwatch Methods
    private void initializeStopwatch() {
        stopwatchTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> updateStopwatch()));
        stopwatchTimeline.setCycleCount(Animation.INDEFINITE);
    }
    
    @FXML
    private void handleStopwatchStart() {
        if (!stopwatchRunning) {
            stopwatchRunning = true;
            stopwatchTimeline.play();
            stopwatchStartBtn.setText("Resume");
            stopwatchPauseBtn.setDisable(false);
        }
    }
    
    @FXML
    private void handleStopwatchPause() {
        if (stopwatchRunning) {
            stopwatchRunning = false;
            stopwatchTimeline.pause();
            stopwatchPauseBtn.setDisable(true);
        }
    }
    
    @FXML
    private void handleStopwatchReset() {
        stopwatchRunning = false;
        stopwatchTimeline.stop();
        stopwatchSeconds = 0;
        stopwatchLabel.setText("00:00:00");
        stopwatchStartBtn.setText("Start");
        stopwatchPauseBtn.setDisable(true);
    }
    
    private void updateStopwatch() {
        stopwatchSeconds++;
        long hours = stopwatchSeconds / 3600;
        long minutes = (stopwatchSeconds % 3600) / 60;
        long seconds = stopwatchSeconds % 60;
        stopwatchLabel.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
    }
    
    // Rest Timer Methods
    private void initializeRestTimer() {
        restTimerTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> updateRestTimer()));
        restTimerTimeline.setCycleCount(Animation.INDEFINITE);
        restTimerTimeline.setOnFinished(e -> {
            restTimerRunning = false;
            restTimerStartBtn.setDisable(false);
            showAlert("Rest time complete! Ready for next set.");
        });
    }
    
    @FXML
    private void handleRestTimerStart() {
        try {
            int duration = Integer.parseInt(restDurationField.getText());
            if (duration <= 0) {
                showAlert("Please enter a valid rest duration.");
                return;
            }
            startRestTimer(duration);
        } catch (NumberFormatException e) {
            showAlert("Please enter a valid number for rest duration.");
        }
    }
    
    private void startRestTimer(int duration) {
        restTimerTotal = duration;
        restTimerSeconds = duration;
        restTimerRunning = true;
        restTimerStartBtn.setDisable(true);
        restProgressBar.setProgress(1.0);
        restTimerTimeline.play();
        updateRestTimer();
    }
    
    @FXML
    private void handleRestTimerStop() {
        restTimerRunning = false;
        restTimerTimeline.stop();
        restTimerSeconds = 0;
        restTimerLabel.setText("00:00");
        restProgressBar.setProgress(0);
        restTimerStartBtn.setDisable(false);
    }
    
    private void updateRestTimer() {
        if (restTimerSeconds > 0) {
            restTimerSeconds--;
            int minutes = restTimerSeconds / 60;
            int seconds = restTimerSeconds % 60;
            restTimerLabel.setText(String.format("%02d:%02d", minutes, seconds));
            restProgressBar.setProgress((double) restTimerSeconds / restTimerTotal);
        } else {
            handleRestTimerStop();
        }
    }
    
    // HIIT Timer Methods
    private void initializeHIITTimer() {
        hiitTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> updateHIITTimer()));
        hiitTimeline.setCycleCount(Animation.INDEFINITE);
    }
    
    @FXML
    private void handleHIITStart() {
        try {
            hiitWorkDuration = Integer.parseInt(workDurationField.getText());
            hiitRestDuration = Integer.parseInt(restDurationHIITField.getText());
            hiitRounds = Integer.parseInt(roundsField.getText());
            
            if (hiitWorkDuration <= 0 || hiitRestDuration <= 0 || hiitRounds <= 0) {
                showAlert("Please enter valid values for all fields.");
                return;
            }
            
            hiitRunning = true;
            hiitCurrentRound = 1;
            hiitIsWorkPhase = true;
            hiitCurrentTime = hiitWorkDuration;
            hiitStartBtn.setDisable(true);
            workDurationField.setDisable(true);
            restDurationHIITField.setDisable(true);
            roundsField.setDisable(true);
            hiitTimeline.play();
            updateHIITTimer();
            
        } catch (NumberFormatException e) {
            showAlert("Please enter valid numbers for all HIIT timer fields.");
        }
    }
    
    @FXML
    private void handleHIITStop() {
        hiitRunning = false;
        hiitTimeline.stop();
        hiitTimerLabel.setText("Ready");
        hiitStatusLabel.setText("Round 0 / 0");
        hiitProgressBar.setProgress(0);
        hiitStartBtn.setDisable(false);
        workDurationField.setDisable(false);
        restDurationHIITField.setDisable(false);
        roundsField.setDisable(false);
        hiitCurrentRound = 0;
    }
    
    private void updateHIITTimer() {
        if (!hiitRunning) return;
        
        if (hiitCurrentTime > 0) {
            hiitCurrentTime--;
            hiitTimerLabel.setText(String.valueOf(hiitCurrentTime));
            
            int totalTime = hiitIsWorkPhase ? hiitWorkDuration : hiitRestDuration;
            hiitProgressBar.setProgress((double) (totalTime - hiitCurrentTime) / totalTime);
            
            if (hiitIsWorkPhase) {
                hiitTimerLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 56px; -fx-font-weight: bold; -fx-effect: dropshadow(gaussian, rgba(39,174,96,0.5), 15, 0.7, 0, 0);");
                hiitStatusLabel.setText("WORK - Round " + hiitCurrentRound + " / " + hiitRounds);
            } else {
                hiitTimerLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 56px; -fx-font-weight: bold; -fx-effect: dropshadow(gaussian, rgba(231,76,60,0.5), 15, 0.7, 0, 0);");
                hiitStatusLabel.setText("REST - Round " + hiitCurrentRound + " / " + hiitRounds);
            }
        } else {
            // Phase complete
            if (hiitIsWorkPhase) {
                // Switch to rest
                hiitIsWorkPhase = false;
                hiitCurrentTime = hiitRestDuration;
            } else {
                // Rest complete, move to next round
                hiitCurrentRound++;
                if (hiitCurrentRound > hiitRounds) {
                    // All rounds complete
                    hiitRunning = false;
                    handleHIITStop();
                    showAlert("HIIT workout complete! Great job!");
                    return;
                }
                hiitIsWorkPhase = true;
                hiitCurrentTime = hiitWorkDuration;
            }
        }
    }
    
    @FXML
    private void handleViewPlan(ActionEvent event) {
        SceneSwitcher.switchScene(event, "/com/example/demo/my_workout_plan.fxml", "My Workout Plan - ShapeShift");
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

