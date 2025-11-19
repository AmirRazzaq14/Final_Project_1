package com.example.demo;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.util.List;

public class WorkoutHomeController {

    private void openWorkout(String workoutName) {
        try {
            Workout workout = getWorkoutData(workoutName);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("WorkoutPlayer.fxml"));
            Parent root = loader.load();

            WorkoutPlayerController controller = loader.getController();
            controller.setWorkout(workout);

            Stage stage = (Stage) workoutList.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML private VBox workoutSection;
    @FXML private VBox workoutList;
    @FXML private Label workoutTitle;

    @FXML
    private void handleCutting() { showWorkouts("Cutting"); }

    @FXML
    private void handleBulking() { showWorkouts("Bulking"); }

    @FXML
    private void handleMaintain() { showWorkouts("Maintaining"); }

    private void showWorkouts(String goal) {

        workoutSection.setVisible(true);
        workoutList.getChildren().clear();

        String[][] workouts;
        switch (goal) {
            case "Cutting":
                workouts = new String[][] {
                        {"Full Body Burn", "10 Exercises | 30 mins"},
                        {"Cardio Blast", "12 Exercises | 25 mins"}
                };
                break;
            case "Bulking":
                workouts = new String[][] {
                        {"Upper Strength", "8 Exercises | 40 mins"},
                        {"Leg Power", "10 Exercises | 45 mins"}
                };
                break;
            default:
                workouts = new String[][] {
                        {"Balanced Core", "10 Exercises | 35 mins"},
                        {"Mobility Flow", "8 Exercises | 20 mins"}
                };
        }

        for (String[] w : workouts) {
            String workoutName = w[0];

            Label name = new Label(workoutName);
            name.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");

            Label detail = new Label(w[1]);
            detail.setStyle("-fx-text-fill: white;");

            VBox card = new VBox(name, detail);
            card.setStyle("-fx-background-color: rgba(255,255,255,0.15); -fx-background-radius: 10; -fx-padding: 10;");
            card.setOnMouseClicked(e -> openWorkout(workoutName));

            workoutList.getChildren().add(card);
        }
    }

   
    private Workout getWorkoutData(String workoutName) {

        switch (workoutName) {

            case "Full Body Burn":
                return new Workout("Full Body Burn", List.of(
                        new Exercise("Warm Up", "05:00", "/images/warmup.jpg"),
                        new Exercise("Jumping Jacks", "12x", "/images/jumping-jacks.jpg"),
                        new Exercise("Skipping", "15x", "/images/skipping.jpg"),
                        new Exercise("Squats", "20x", "/images/squats.jpg")

                ));

            case "Cardio Blast":
                return new Workout("Cardio Blast", List.of(
                        new Exercise("Run in Place", "03:00", "/images/run.jpg"),
                        new Exercise("High Knees", "20x", "/images/knees.jpg"),
                        new Exercise("Burpees", "10x", "/images/burpees.jpg")
                ));

            case "Upper Strength":
                return new Workout("Upper Strength", List.of(
                        new Exercise("Push-Ups", "15x", "/images/pushups.jpg"),
                        new Exercise("Incline Push-Ups", "12x", "/images/incline-pushups.jpg"),
                        new Exercise("Arm Raises", "00:45", "/images/arm-raises.jpg"),
                        new Exercise("Chest Press Motion", "20x", "/images/chest-press.jpg")
                ));

            case "Leg Power":
                return new Workout("Leg Power", List.of(
                        new Exercise("Warm Up", "03:00", "/images/leg-warmup.jpg"),
                        new Exercise("Squats", "20x", "/images/weighted-squats.jpg"),
                        new Exercise("Lunges", "12x", "/images/lunges.jpg"),
                        new Exercise("Calf Raises", "20x", "/images/calf-raises.jpg")
                ));


            case "Balanced Core":
                return new Workout("Balanced Core", List.of(
                        new Exercise("Plank", "01:00", "/images/plank.jpg"),
                        new Exercise("Bicycle Crunches", "20x", "/images/bicycle.jpg"),
                        new Exercise("Leg Raises", "15x", "/images/leg-raises.jpg")
                ));

            case "Mobility Flow":
                return new Workout("Mobility Flow", List.of(
                        new Exercise("Cobra Stretch", "01:00", "/images/cobra.jpg"),
                        new Exercise("Torso Twist", "20x", "/images/twist.jpg"),
                        new Exercise("Side Bends", "15x", "/images/side-bend.jpg")
                ));

        }

        return null;
    }

    public void handleSelectWorkouts(ActionEvent event) {
        SceneSwitcher.switchScene(event, "/com/example/demo/workouts.fxml", "Select Workouts");
    }
}

