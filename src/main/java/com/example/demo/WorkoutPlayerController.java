package com.example.demo;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.util.Duration;
import javafx.stage.Stage;
import java.util.List;
import java.util.stream.Collectors;

public class WorkoutPlayerController {
    @FXML private ListView<String> exerciseListView;
    @FXML private Label workoutTitle;
    @FXML private Label timerLabel;
    @FXML private Button pauseButton;
    @FXML private Button finishButton;

    private Workout workout;
    private List<Exercise> exercises;

    private Timeline timer;
    private int totalSeconds = 0;
    private boolean paused = false;

    public void setWorkout(Workout workout) {
        this.workout = workout;
        this.exercises = workout.getExercises();

        List<String> lines = exercises.stream()
                .map(ex -> ex.getName() + " — " + ex.getDetail())
                .collect(Collectors.toList());
        exerciseListView.getItems().setAll(lines);
        workoutTitle.setText(workout.getTitle());

        startWorkoutTimer();
    }

    private void startWorkoutTimer() {
        totalSeconds = 0;
        timerLabel.setText(formatTime(totalSeconds));

        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            totalSeconds++;
            timerLabel.setText(formatTime(totalSeconds));
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
        paused = false;
        pauseButton.setText("Pause");
    }

    private String formatTime(int sec) {
        int m = sec / 60;
        int s = sec % 60;
        return String.format("%02d:%02d", m, s);
    }

    @FXML
    private void handlePauseResume() {
        if (timer == null) return;
        if (!paused) {
            timer.pause();
            pauseButton.setText("Resume");
            paused = true;
        } else {
            timer.play();
            pauseButton.setText("Pause");
            paused = false;
        }
    }

    @FXML
    private void handleFinish() {
        if (timer != null) timer.stop();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Workout Complete");
        alert.setHeaderText("Great job!");
        alert.setContentText("Total time: " + formatTime(totalSeconds));
        alert.showAndWait();

        try {
            Stage stage = (Stage) finishButton.getScene().getWindow();
            stage.setScene(new Scene(
                    javafx.fxml.FXMLLoader.load(getClass().getResource("/com/example/demo/workout_home.fxml"))
            ));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
