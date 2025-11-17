package com.example.demo;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.*;

public class WorkoutFilterDropdown extends Application {


    public static final Map<String, List<String>> MUSCLE_GROUP_WORKOUTS = new LinkedHashMap<>();
    static {
        MUSCLE_GROUP_WORKOUTS.put("Chest", Arrays.asList("Bench Press", "Cable Bench Press", "Decline Bench Press", "Decline Push-Up", "Dumbbell Chest Fly", "Dumbbell Chest Press", "Incline Bench Press", "Incline Dumbbell Press", "Machine Chest Fly", "Machine Chest Press", "Pec Deck", "Push-Up", "Smith Machine Bench Press", "Smith Machine Incline Bench Press", "Standing Cable Chest Fly" ));
        MUSCLE_GROUP_WORKOUTS.put("Core", Arrays.asList("Ball Slams", "Bicycle Crunch", "Crunch", "Hanging Knee Raise", "Hanging Leg Raise", "Plank", "Machine Crunch", "Mountain Climbers", "Plank", "Side Plank", "Sit-Up", "Russian Twist"));
        MUSCLE_GROUP_WORKOUTS.put("Legs", Arrays.asList("Barbell Lunge", "Box Jump", "Box Squat", "Adducter", "Abducter", "Dumbbell Lunge", "Dumbbell Squat", "Front Squat", "Jump Squat", "Jumping Lunge", "Leg Extension", "Leg Press", "Lying Leg Curl", "Romanian Deadlift", "Smith Machine Squat", "Squat", "Step Up", "Hip Thrust", "Standing Calf Raise", "Seated Calf Raise"));
        MUSCLE_GROUP_WORKOUTS.put("Biceps", Arrays.asList("Barbell Curl", "Cable Curl", "Dumbbell Curl", "Hammer Curl", "Preacher Curl"));
        MUSCLE_GROUP_WORKOUTS.put("Triceps", Arrays.asList("Bench Dip", "Close-Grip Push-Up", "Dumbbell Lying Triceps Extension", "Overhead Cable Extension", "Tricep Pushdown"));
        MUSCLE_GROUP_WORKOUTS.put("Back", Arrays.asList("Back Extension", "Barbell Row", "Cable Seated Row", "Chin-Up", "Dumbbell Row", "Good Morning", "Inverted Row"));
        MUSCLE_GROUP_WORKOUTS.put("Cardio", Arrays.asList("Running", "Rowing", "Cycling", "Stair Master"));
    }

    @Override
    public void start(Stage primaryStage) {

        ComboBox<String> muscleGroupComboBox = new ComboBox<>();
        muscleGroupComboBox.getItems().add("All"); // Option to view all
        muscleGroupComboBox.getItems().addAll(MUSCLE_GROUP_WORKOUTS.keySet());
        muscleGroupComboBox.setPromptText("Select muscle group");
        muscleGroupComboBox.setValue("All");


        ComboBox<String> workoutComboBox = new ComboBox<>();
        workoutComboBox.setPromptText("Select workout");
        updateWorkouts(workoutComboBox, "All");


        muscleGroupComboBox.setOnAction(event -> {
            String selectedGroup = muscleGroupComboBox.getValue();
            updateWorkouts(workoutComboBox, selectedGroup);
        });

        VBox root = new VBox(12, muscleGroupComboBox, workoutComboBox);
        root.setStyle("-fx-padding: 20;");

        primaryStage.setScene(new Scene(root, 350, 120));
        primaryStage.setTitle("Filter Workouts by Muscle Group");
        primaryStage.show();
    }


    private void updateWorkouts(ComboBox<String> workoutComboBox, String muscleGroup) {
        ObservableList<String> workouts;
        if ("All".equals(muscleGroup)) {
            Set<String> allWorkouts = new LinkedHashSet<>(); // avoid duplicates
            for (List<String> groupWorkouts : MUSCLE_GROUP_WORKOUTS.values()) {
                allWorkouts.addAll(groupWorkouts);
            }
            workouts = FXCollections.observableArrayList(allWorkouts);
        } else {
            workouts = FXCollections.observableArrayList(MUSCLE_GROUP_WORKOUTS.getOrDefault(muscleGroup, Collections.emptyList()));
        }
        workoutComboBox.setItems(workouts);
        workoutComboBox.getSelectionModel().clearSelection();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
