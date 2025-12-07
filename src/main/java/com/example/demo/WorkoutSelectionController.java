package com.example.demo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.*;

public class WorkoutSelectionController {

    private static final Map<String, List<String>> MUSCLE_GROUP_WORKOUTS = WorkoutFilterDropdown.MUSCLE_GROUP_WORKOUTS;

    @FXML
    private ComboBox<String> muscleGroupComboBox;
    @FXML
    private TextField workoutSearchField;
    @FXML
    private ListView<String> workoutListView;
    @FXML
    private ListView<String> myWorkoutsListView;

    private ObservableList<String> fullWorkoutList = FXCollections.observableArrayList(); // All filtered by muscle group
    private ObservableList<String> searchFilteredList = FXCollections.observableArrayList(); // Filtered by search
    private ObservableList<String> myWorkouts = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        muscleGroupComboBox.getItems().add("All");
        muscleGroupComboBox.getItems().addAll(MUSCLE_GROUP_WORKOUTS.keySet());
        muscleGroupComboBox.setValue("All");
        muscleGroupComboBox.setOnAction(e -> updateWorkouts());

        updateWorkouts();
        
        // Load saved workouts
        String email = LoginController.getCurrentUserEmail();
        if (email != null) {
            List<String> saved = DataManager.getSelectedWorkouts(email);
            myWorkouts.addAll(saved);
        }
        
        myWorkoutsListView.setItems(myWorkouts);

        workoutSearchField.textProperty().addListener((obs, oldVal, newVal) -> applySearchFilter(newVal));
    }

    private void updateWorkouts() {
        String selectedGroup = muscleGroupComboBox.getValue();
        fullWorkoutList.clear();

        if ("All".equals(selectedGroup)) {
            Set<String> all = new LinkedHashSet<>();
            for (List<String> wks : MUSCLE_GROUP_WORKOUTS.values()) {
                all.addAll(wks);
            }
            fullWorkoutList.setAll(all);
        } else {
            fullWorkoutList.setAll(MUSCLE_GROUP_WORKOUTS.getOrDefault(selectedGroup, Collections.emptyList()));
        }
        applySearchFilter(workoutSearchField.getText());
    }

    private void applySearchFilter(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            searchFilteredList.setAll(fullWorkoutList);
        } else {
            String lower = searchText.toLowerCase();
            searchFilteredList.setAll(
                    fullWorkoutList.filtered(w -> w.toLowerCase().contains(lower))
            );
        }
        workoutListView.setItems(searchFilteredList);
    }

    @FXML
    private void handleAddWorkout() {
        String selected = workoutListView.getSelectionModel().getSelectedItem();
        if (selected != null && !myWorkouts.contains(selected)) {
            myWorkouts.add(selected);
        }
    }

    @FXML
    private void handleRemoveWorkout() {
        String selected = myWorkoutsListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            myWorkouts.remove(selected);
        }
    }

    @FXML
    private void handleBackToHome(javafx.event.ActionEvent event) {
        // Save selected workouts before leaving
        String email = LoginController.getCurrentUserEmail();
        if (email != null && !myWorkouts.isEmpty()) {
            DataManager.saveSelectedWorkouts(email, new ArrayList<>(myWorkouts));
        }
        SceneSwitcher.switchScene(event, "/com/example/demo/workout_home.fxml", "Workout Home");
    }
}
