package com.example.demo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;

public class WorkoutPlanViewController {
    
    @FXML private Label planTitle;
    @FXML private VBox planContainer;
    
    private WorkoutPlan plan;
    
    public void setPlan(WorkoutPlan plan) {
        this.plan = plan;
        displayPlan();
    }
    
    @FXML
    public void initialize() {
        // Will be populated by setPlan
    }
    
    private void displayPlan() {
        if (plan == null) return;
        
        planTitle.setText(plan.getPlanName());
        planContainer.getChildren().clear();
        
        for (DailyWorkout daily : plan.getWeeklyPlan()) {
            VBox dayCard = createDayCard(daily);
            planContainer.getChildren().add(dayCard);
        }
    }
    
    private VBox createDayCard(DailyWorkout daily) {
        VBox card = new VBox(15);
        card.setStyle("-fx-background-color: rgba(255,255,255,0.95); -fx-background-radius: 15; -fx-padding: 25;");
        
        HBox header = new HBox(10);
        Label dayLabel = new Label(daily.getDay().toString());
        dayLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        Label focusLabel = new Label(daily.getFocusArea());
        focusLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #667eea; -fx-font-weight: bold;");
        header.getChildren().addAll(dayLabel, focusLabel);
        
        card.getChildren().add(header);
        
        if (daily.isRestDay()) {
            Label restLabel = new Label("Rest Day - Recovery is important!");
            restLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #7f8c8d; -fx-font-style: italic;");
            card.getChildren().add(restLabel);
        } else {
            VBox exercisesBox = new VBox(10);
            for (WorkoutExercise exercise : daily.getExercises()) {
                Label exerciseLabel = new Label(exercise.toString());
                exerciseLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #2c3e50;");
                exercisesBox.getChildren().add(exerciseLabel);
            }
            card.getChildren().add(exercisesBox);
        }
        
        return card;
    }
    
    @FXML
    private void handleBack(ActionEvent event) {
        SceneSwitcher.switchScene(event, "/com/example/demo/workout_home.fxml", "Workout Home");
    }
}





