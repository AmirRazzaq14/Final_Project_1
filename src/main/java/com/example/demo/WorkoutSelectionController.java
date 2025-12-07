package com.example.demo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.util.*;
import java.util.stream.Collectors;

public class WorkoutSelectionController {
    private static final Map<String, List<String>> MUSCLE_GROUP_WORKOUTS = WorkoutFilterDropdown.MUSCLE_GROUP_WORKOUTS;

    @FXML private ComboBox<String> muscleGroupComboBox;
    @FXML private TextField workoutSearchField;
    @FXML private ListView<String> workoutListView;
    @FXML private ListView<String> myWorkoutsListView;
    @FXML private Button startWorkoutButton;

    private ObservableList<String> fullWorkoutList = FXCollections.observableArrayList();
    private ObservableList<String> searchFilteredList = FXCollections.observableArrayList();
    private ObservableList<String> myWorkouts = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        muscleGroupComboBox.getItems().add("All");
        muscleGroupComboBox.getItems().addAll(MUSCLE_GROUP_WORKOUTS.keySet());
        muscleGroupComboBox.setValue("All");
        muscleGroupComboBox.setOnAction(e -> updateWorkouts());

        updateWorkouts();

        myWorkoutsListView.setItems(myWorkouts);

        workoutSearchField.textProperty().addListener((obs, oldVal, newVal) -> applySearchFilter(newVal));


        startWorkoutButton.setDisable(myWorkouts.isEmpty());
        myWorkouts.addListener((ListChangeListener<String>) c ->
                startWorkoutButton.setDisable(myWorkouts.isEmpty())
        );
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
        SceneSwitcher.switchScene(event, "/com/example/demo/workout_home.fxml", "Workout Home");
    }


    @FXML
    private void handleStartWorkout(javafx.event.ActionEvent event) {
        List<Exercise> exercises = myWorkouts.stream()
                // TODO: Replace with mapping to appropriate Exercise detail/image for each real exercise if needed
                .map(name -> new Exercise(name, "10x", "/images/default.jpg"))
                .collect(Collectors.toList());

        Workout workout = new Workout("My Custom Plan", exercises);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/WorkoutPlayer.fxml"));
            Parent root = loader.load();

            WorkoutPlayerController controller = loader.getController();
            controller.setWorkout(workout);

            Stage stage = (Stage) startWorkoutButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
