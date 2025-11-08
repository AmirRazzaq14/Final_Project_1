package com.example.demo;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class WorkoutHomeController {

    @FXML
    private VBox workoutSection;
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
            Label name = new Label(w[0]);
            name.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");
            Label detail = new Label(w[1]);
            detail.setStyle("-fx-text-fill: white;");
            VBox card = new VBox(name, detail);
            card.setStyle("-fx-background-color: rgba(255,255,255,0.15); -fx-background-radius: 10; -fx-padding: 10;");
            workoutList.getChildren().add(card);
        }
    }
}

