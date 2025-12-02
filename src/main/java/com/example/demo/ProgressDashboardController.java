package com.example.demo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ProgressDashboardController {
    
    @FXML private LineChart<String, Number> weightChart;
    @FXML private LineChart<String, Number> bodyFatChart;
    @FXML private BarChart<String, Number> prChart;
    @FXML private LineChart<String, Number> calorieChart;
    @FXML private BarChart<String, Number> stepsChart;
    @FXML private HBox connectionWarningBox;
    @FXML private Label connectionWarningLabel;
    
    private String currentUserEmail;
    
    @FXML
    public void initialize() {
        currentUserEmail = SessionManager.getCurrentEmail();
        
        // Check Firebase connection
        if (!FirebaseConnectionManager.isConnected()) {
            if (connectionWarningBox != null) {
                connectionWarningBox.setVisible(true);
                if (connectionWarningLabel != null) {
                    connectionWarningLabel.setText("Firebase connection not available. Using local data storage.");
                }
            }
        } else {
            if (connectionWarningBox != null) {
                connectionWarningBox.setVisible(false);
            }
        }
        
        // Initialize charts with proper styling
        setupWeightChart();
        setupBodyFatChart();
        setupPRChart();
        setupCalorieChart();
        setupStepsChart();
        
        // Load and display data
        loadProgressData();
    }
    
    private void setupWeightChart() {
        if (weightChart != null) {
            weightChart.setTitle("Weight (lbs)");
            if (weightChart.getXAxis() != null) weightChart.getXAxis().setLabel("Date");
            if (weightChart.getYAxis() != null) weightChart.getYAxis().setLabel("Weight (lbs)");
        }
    }
    
    private void setupBodyFatChart() {
        if (bodyFatChart != null) {
            bodyFatChart.setTitle("Body Fat %");
            if (bodyFatChart.getXAxis() != null) bodyFatChart.getXAxis().setLabel("Date");
            if (bodyFatChart.getYAxis() != null) bodyFatChart.getYAxis().setLabel("Body Fat %");
        }
    }
    
    private void setupPRChart() {
        if (prChart != null) {
            prChart.setTitle("Personal Records");
            if (prChart.getXAxis() != null) prChart.getXAxis().setLabel("Exercise");
            if (prChart.getYAxis() != null) prChart.getYAxis().setLabel("Weight (lbs)");
        }
    }
    
    private void setupCalorieChart() {
        if (calorieChart != null) {
            calorieChart.setTitle("Calorie Adherence");
            if (calorieChart.getXAxis() != null) calorieChart.getXAxis().setLabel("Date");
            if (calorieChart.getYAxis() != null) {
                calorieChart.getYAxis().setLabel("Adherence %");
                if (calorieChart.getYAxis() instanceof NumberAxis) {
                    ((NumberAxis) calorieChart.getYAxis()).setUpperBound(100);
                }
            }
        }
    }
    
    private void setupStepsChart() {
        if (stepsChart != null) {
            stepsChart.setTitle("Daily Steps");
            if (stepsChart.getXAxis() != null) stepsChart.getXAxis().setLabel("Date");
            if (stepsChart.getYAxis() != null) stepsChart.getYAxis().setLabel("Steps");
        }
    }
    
    private void loadProgressData() {
        if (currentUserEmail == null) return;
        
        List<ProgressData> data = DataManager.getProgress(currentUserEmail);
        if (data == null || data.isEmpty()) {
            // Show empty state message
            return;
        }
        
        // Sort by date
        data.sort((a, b) -> a.getDate().compareTo(b.getDate()));
        
        // Weight Chart - CSS will handle colors via default-color0
        XYChart.Series<String, Number> weightSeries = new XYChart.Series<>();
        weightSeries.setName("Weight");
        for (ProgressData pd : data) {
            if (pd.getWeight() > 0) {
                String dateStr = pd.getDate().format(DateTimeFormatter.ofPattern("MM/dd"));
                weightSeries.getData().add(new XYChart.Data<>(dateStr, pd.getWeight()));
            }
        }
        if (!weightSeries.getData().isEmpty()) {
            weightChart.getData().add(weightSeries);
        }
        
        // Body Fat Chart - CSS will handle colors via default-color1
        XYChart.Series<String, Number> bodyFatSeries = new XYChart.Series<>();
        bodyFatSeries.setName("Body Fat %");
        for (ProgressData pd : data) {
            if (pd.getBodyFatPercentage() > 0) {
                String dateStr = pd.getDate().format(DateTimeFormatter.ofPattern("MM/dd"));
                bodyFatSeries.getData().add(new XYChart.Data<>(dateStr, pd.getBodyFatPercentage()));
            }
        }
        if (!bodyFatSeries.getData().isEmpty()) {
            bodyFatChart.getData().add(bodyFatSeries);
        }
        
        // PR Chart
        XYChart.Series<String, Number> prSeries = new XYChart.Series<>();
        prSeries.setName("PR Weight");
        double benchPR = 0, squatPR = 0, deadliftPR = 0;
        for (ProgressData pd : data) {
            if (pd.getBenchPR() != null && pd.getBenchPR().getWeight() > benchPR) {
                benchPR = pd.getBenchPR().getWeight();
            }
            if (pd.getSquatPR() != null && pd.getSquatPR().getWeight() > squatPR) {
                squatPR = pd.getSquatPR().getWeight();
            }
            if (pd.getDeadliftPR() != null && pd.getDeadliftPR().getWeight() > deadliftPR) {
                deadliftPR = pd.getDeadliftPR().getWeight();
            }
        }
        if (benchPR > 0) prSeries.getData().add(new XYChart.Data<>("Bench", benchPR));
        if (squatPR > 0) prSeries.getData().add(new XYChart.Data<>("Squat", squatPR));
        if (deadliftPR > 0) prSeries.getData().add(new XYChart.Data<>("Deadlift", deadliftPR));
        if (!prSeries.getData().isEmpty()) {
            prChart.getData().add(prSeries);
        }
        
        // Calorie Adherence Chart - CSS will handle colors via default-color2
        XYChart.Series<String, Number> calorieSeries = new XYChart.Series<>();
        calorieSeries.setName("Adherence %");
        for (ProgressData pd : data) {
            if (pd.getCaloriesTarget() > 0) {
                String dateStr = pd.getDate().format(DateTimeFormatter.ofPattern("MM/dd"));
                calorieSeries.getData().add(new XYChart.Data<>(dateStr, pd.getCalorieAdherence()));
            }
        }
        if (!calorieSeries.getData().isEmpty()) {
            calorieChart.getData().add(calorieSeries);
        }
        
        // Steps Chart - CSS will handle colors via default-color3
        XYChart.Series<String, Number> stepsSeries = new XYChart.Series<>();
        stepsSeries.setName("Steps");
        for (ProgressData pd : data) {
            if (pd.getSteps() > 0) {
                String dateStr = pd.getDate().format(DateTimeFormatter.ofPattern("MM/dd"));
                stepsSeries.getData().add(new XYChart.Data<>(dateStr, pd.getSteps()));
            }
        }
        if (!stepsSeries.getData().isEmpty()) {
            stepsChart.getData().add(stepsSeries);
        }
    }
    
    @FXML
    private void handleAddProgress(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/add_progress.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setScene(new Scene(root, 600, 700));
            stage.setTitle("Add Progress Entry");
            stage.showAndWait();
            
            // Refresh charts after adding data
            refreshCharts();
        } catch (IOException e) {
            showAlert("Error loading add progress screen.");
        }
    }
    
    private void refreshCharts() {
        // Clear existing data
        weightChart.getData().clear();
        bodyFatChart.getData().clear();
        prChart.getData().clear();
        calorieChart.getData().clear();
        stepsChart.getData().clear();
        
        // Reload data
        loadProgressData();
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

