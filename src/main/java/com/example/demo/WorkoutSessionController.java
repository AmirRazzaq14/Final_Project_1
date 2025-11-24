package com.example.demo;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.List;

public class WorkoutSessionController {

    @FXML private Label titleLabel;
    @FXML private ListView<String> exerciseList;
    @FXML private Label timerLabel;
    @FXML private Button pauseButton;

    private Timeline timer;
    private int secondsElapsed = 0;
    private boolean paused = false;
    private int totalDurationSeconds = 0;

    public void setExercises(List<String> exercises, String title) {
        titleLabel.setText("Workout: " + title);
        exerciseList.setItems(FXCollections.observableArrayList(exercises));
        totalDurationSeconds = estimateDuration(exercises); // or set another way
        secondsElapsed = 0;
        updateTimerLabel();
        startTimer();
    }

    private int estimateDuration(List<String> exercises) {
        return exercises.size() * 60;
    }

    private void updateTimerLabel() {
        int secs = secondsElapsed % 60;
        int mins = secondsElapsed / 60;
        timerLabel.setText(String.format("%02d:%02d", mins, secs));
    }

    private void startTimer() {
        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (!paused) {
                secondsElapsed++;
                updateTimerLabel();
            }
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    @FXML
    private void togglePause(ActionEvent event) {
        paused = !paused;
        pauseButton.setText(paused ? "Resume" : "Pause");
    }

    @FXML
    private void handleFinish(ActionEvent event) {
        if (timer != null) timer.stop();
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Workout Complete!\nTotal Time: " + timerLabel.getText(), ButtonType.OK);
        alert.showAndWait();
        Stage stage = (Stage) exerciseList.getScene().getWindow();
        try {
            SceneSwitcher.switchScene(event, "/com/example/demo/workout_home.fxml", "Workout Home");
        } catch (Exception e) {
            e.printStackTrace();
            stage.close();
        }
    }
}
