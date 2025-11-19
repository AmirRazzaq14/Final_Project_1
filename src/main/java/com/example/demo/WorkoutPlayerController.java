package com.example.demo;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;

import javafx.event.ActionEvent;

import java.net.URL;
import java.util.List;

public class WorkoutPlayerController {

    @FXML
    private Label exerciseName;
    @FXML private Label exerciseDetail;
    @FXML private ImageView exerciseImage;
    @FXML private Label timerLabel;
    @FXML private ProgressBar progressBar;

    private Workout workout;
    private List<Exercise> exercises;
    private int currentIndex = 0;

    private Timeline timer;
    private int timeRemaining;

    public void setWorkout(Workout workout) {
        this.workout = workout;
        this.exercises = workout.getExercises();
        loadExercise(0);
    }

    private void loadExercise(int index) {
        currentIndex = index;
        Exercise ex = exercises.get(index);

        exerciseName.setText(ex.getName());
        exerciseDetail.setText(ex.getDetail());
        URL url = getClass().getResource(ex.getImage());
        if (url == null) {
            System.out.println("IMAGE NOT FOUND: " + ex.getImage());
        } else {
            exerciseImage.setImage(new Image(url.toExternalForm()));
        }

        System.out.println("Looking for: " + ex.getImage());
        System.out.println("Resolved: " + getClass().getResource(ex.getImage()));


        // Update progress bar
        progressBar.setProgress((double) index / exercises.size());

        // Check if this exercise uses time
        if (ex.getDetail().contains(":")) {
            startTimer(parseDuration(ex.getDetail()));
        } else {
            timerLabel.setText(ex.getDetail()); // show reps instead
            if (timer != null) timer.stop();
        }
    }

    private int parseDuration(String text) {
        // "01:30" -> 90 seconds
        if (!text.contains(":")) return 0;
        String[] parts = text.split(":");
        return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
    }

    private void startTimer(int seconds) {
        timeRemaining = seconds;

        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timeRemaining--;
            timerLabel.setText(formatTime(timeRemaining));

            if (timeRemaining <= 0) {
                timer.stop();
                goNext(null);
            }
        }));
        timer.setCycleCount(seconds);
        timer.play();
    }

    private String formatTime(int sec) {
        int m = sec / 60;
        int s = sec % 60;
        return String.format("%02d:%02d", m, s);
    }

    @FXML
    private void goNext(ActionEvent event) {
        if (currentIndex < exercises.size() - 1) {
            loadExercise(currentIndex + 1);
        } else {
            finishWorkout();
        }
    }

    @FXML
    private void goPrev(ActionEvent event) {
        if (currentIndex > 0) {
            loadExercise(currentIndex - 1);
        }
    }

    private void finishWorkout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Workout Complete!");
        alert.setContentText("Great job—you finished " + workout.getTitle() + "!");

        // Wait for OK button
        alert.showAndWait();

        // Go back to home page
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("workout_home.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) exerciseName.getScene().getWindow(); // get current window
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
