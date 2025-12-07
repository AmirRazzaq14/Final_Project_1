package com.example.demo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class ProgressDashboardController {
    
    @FXML private Label latestWeightLabel;
    @FXML private Label weightChangeLabel;
    @FXML private Label latestBodyFatLabel;
    @FXML private Label bodyFatChangeLabel;
    @FXML private Label calorieSummaryLabel;
    @FXML private Label stepsSummaryLabel;
    @FXML private Label lastEntryLabel;
    @FXML private VBox emptyStateContainer;
    @FXML private HBox summaryCardsContainer;
    @FXML private HBox welcomeBannerContainer;
    @FXML private Label welcomeMessageLabel;
    @FXML private VBox progressInsightsContainer;
    @FXML private Label streakLabel;
    @FXML private Label weeklyProgressLabel;
    @FXML private Label averagePerWeekLabel;
    @FXML private Label totalEntriesCountLabel;
    @FXML private Label overallTrendLabel;
    @FXML private Label consistencyLabel;
    @FXML private Label totalEntriesLabel;
    @FXML private HBox highlightsContainer;
    @FXML private Label daysTrackingLabel;
    @FXML private Label totalWeightChangeLabel;
    @FXML private Label bestCalorieDayLabel;
    @FXML private Label highestStepsLabel;
    @FXML private VBox historyContainer;
    @FXML private VBox progressHistoryContainer;
    @FXML private VBox chartContainer;
    
    private String currentUserEmail;
    
    @FXML
    public void initialize() {
        currentUserEmail = SessionManager.getCurrentEmail();
        
        // Load and display data
        loadProgressData();
    }
    
    private void loadProgressData() {
        if (currentUserEmail == null) {
            showEmptyState();
            return;
        }
        
        List<ProgressData> data = DataManager.getProgress(currentUserEmail);
        if (data == null || data.isEmpty()) {
            showEmptyState();
            return;
        }
        
        // Sort by date (newest first)
        data.sort((a, b) -> b.getDate().compareTo(a.getDate()));
        
        // Show summary cards
        if (summaryCardsContainer != null) {
            summaryCardsContainer.setVisible(true);
        }
        if (emptyStateContainer != null) {
            emptyStateContainer.setVisible(false);
        }
        if (welcomeBannerContainer != null) {
            welcomeBannerContainer.setVisible(true);
        }
        if (progressInsightsContainer != null) {
            progressInsightsContainer.setVisible(true);
        }
        if (highlightsContainer != null) {
            highlightsContainer.setVisible(true);
        }
        
        // Update welcome banner
        updateWelcomeBanner(data);
        
        // Update progress insights
        updateProgressInsights(data);
        
        // Update progress highlights
        updateProgressHighlights(data);
        
        // Calculate and display summary statistics
        updateSummaryCards(data);
        
        // Display progress history
        displayProgressHistory(data);
        
        // Display chart (will handle visibility internally)
        displayChart(data);
    }
    
    private void updateSummaryCards(List<ProgressData> data) {
        if (data.isEmpty()) {
            showEmptyState();
            return;
        }
        
        // Latest weight
        ProgressData latest = data.get(0);
        if (latest.getWeight() > 0) {
            latestWeightLabel.setText(String.format("%.1f lbs", latest.getWeight()));
            if (data.size() > 1) {
                ProgressData previous = data.get(1);
                if (previous.getWeight() > 0) {
                    double change = latest.getWeight() - previous.getWeight();
                    if (change > 0) {
                        weightChangeLabel.setText(String.format("+%.1f lbs", change));
                        weightChangeLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 12px;");
                    } else if (change < 0) {
                        weightChangeLabel.setText(String.format("%.1f lbs", change));
                        weightChangeLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 12px;");
                    } else {
                        weightChangeLabel.setText("No change");
                        weightChangeLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 12px;");
                    }
                }
            }
        } else {
            latestWeightLabel.setText("--");
        }
        
        // Latest body fat
        if (latest.getBodyFatPercentage() > 0) {
            latestBodyFatLabel.setText(String.format("%.1f%%", latest.getBodyFatPercentage()));
            latestBodyFatLabel.setWrapText(true);
        } else {
            latestBodyFatLabel.setText("--");
        }
        
        // Calorie adherence average
        double totalAdherence = 0;
        int count = 0;
        for (ProgressData pd : data) {
            if (pd.getCaloriesTarget() > 0) {
                totalAdherence += pd.getCalorieAdherence();
                count++;
            }
        }
        if (count > 0) {
            calorieSummaryLabel.setText(String.format("%.0f%%", totalAdherence / count));
            calorieSummaryLabel.setWrapText(true);
        } else {
            calorieSummaryLabel.setText("--");
        }
        
        // Steps average
        double totalSteps = 0;
        int stepsCount = 0;
        for (ProgressData pd : data) {
            if (pd.getSteps() > 0) {
                totalSteps += pd.getSteps();
                stepsCount++;
            }
        }
        if (stepsCount > 0) {
            stepsSummaryLabel.setText(String.format("%.0f", totalSteps / stepsCount));
        } else {
            stepsSummaryLabel.setText("--");
        }
        
        // Last entry date
        lastEntryLabel.setText(latest.getDate().format(DateTimeFormatter.ofPattern("MMM d, yyyy")));
    }
    
    private void displayProgressHistory(List<ProgressData> data) {
        if (progressHistoryContainer == null) return;
        
        progressHistoryContainer.getChildren().clear();
        
        // Show last 20 entries (increased for larger window)
        int entriesToShow = Math.min(20, data.size());
        for (int i = 0; i < entriesToShow; i++) {
            ProgressData pd = data.get(i);
            VBox entryCard = createHistoryEntryCard(pd);
            progressHistoryContainer.getChildren().add(entryCard);
        }
        
        if (historyContainer != null) {
            historyContainer.setVisible(true);
        }
    }
    
    private VBox createHistoryEntryCard(ProgressData pd) {
        VBox card = new VBox(8);
        card.setStyle("-fx-background-color: rgba(102,126,234,0.08); -fx-background-radius: 12; -fx-padding: 15; -fx-border-color: rgba(102,126,234,0.2); -fx-border-width: 1; -fx-border-radius: 12;");
        
        HBox header = new HBox(15);
        Label dateLabel = new Label(pd.getDate().format(DateTimeFormatter.ofPattern("MMM d, yyyy")));
        dateLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #000000;");
        header.getChildren().add(dateLabel);
        
        VBox details = new VBox(8);
        
        if (pd.getWeight() > 0) {
            Label weightLabel = new Label("Weight: " + String.format("%.1f lbs", pd.getWeight()));
            weightLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2c3e50;");
            details.getChildren().add(weightLabel);
        }
        
        if (pd.getBodyFatPercentage() > 0) {
            Label bfLabel = new Label("Body Fat: " + String.format("%.1f%%", pd.getBodyFatPercentage()));
            bfLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2c3e50;");
            details.getChildren().add(bfLabel);
        }
        
        if (pd.getCaloriesTarget() > 0) {
            Label calLabel = new Label("Calories: " + pd.getCaloriesConsumed() + " / " + pd.getCaloriesTarget() + 
                                      " (" + String.format("%.0f%%", pd.getCalorieAdherence()) + ")");
            calLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2c3e50;");
            details.getChildren().add(calLabel);
        }
        
        if (pd.getSteps() > 0) {
            Label stepsLabel = new Label("Steps: " + pd.getSteps());
            stepsLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2c3e50;");
            details.getChildren().add(stepsLabel);
        }
        
        card.getChildren().addAll(header, details);
        return card;
    }
    
    private void showEmptyState() {
        if (emptyStateContainer != null) {
            emptyStateContainer.setVisible(true);
        }
        if (summaryCardsContainer != null) {
            summaryCardsContainer.setVisible(false);
        }
        if (welcomeBannerContainer != null) {
            welcomeBannerContainer.setVisible(false);
        }
        if (progressInsightsContainer != null) {
            progressInsightsContainer.setVisible(false);
        }
        if (highlightsContainer != null) {
            highlightsContainer.setVisible(false);
        }
        if (historyContainer != null) {
            historyContainer.setVisible(false);
        }
        if (chartContainer != null) {
            chartContainer.setVisible(false);
        }
        // Keep welcome back message visible even in empty state
    }
    
    private void updateWelcomeBanner(List<ProgressData> data) {
        if (welcomeBannerContainer == null || data == null || data.isEmpty()) {
            return;
        }
        
        // Update total entries count
        if (totalEntriesLabel != null) {
            int totalEntries = data.size();
            totalEntriesLabel.setText("📊 " + totalEntries + " " + (totalEntries == 1 ? "entry" : "entries") + " logged");
        }
        
        // Set motivational message based on progress
        if (welcomeMessageLabel != null && !data.isEmpty()) {
            ProgressData latest = data.get(0);
            String message = "💪 Keep pushing forward! Your progress is inspiring.";
            
            // Customize message based on weight loss/gain
            if (data.size() > 1 && latest.getWeight() > 0) {
                ProgressData previous = data.get(1);
                if (previous.getWeight() > 0) {
                    double change = latest.getWeight() - previous.getWeight();
                    if (change < -5) {
                        message = "🔥 Amazing progress! You're crushing your goals!";
                    } else if (change < 0) {
                        message = "💪 Great work! Keep up the momentum!";
                    } else if (change > 5) {
                        message = "📈 Building strength! Every step counts!";
                    }
                }
            }
            
            welcomeMessageLabel.setText(message);
        }
    }
    
    private void updateProgressInsights(List<ProgressData> data) {
        if (progressInsightsContainer == null || data == null || data.isEmpty()) {
            return;
        }
        
        // Calculate current streak (consecutive days with entries)
        int streak = calculateStreak(data);
        if (streakLabel != null) {
            streakLabel.setText("🔥 Current Streak: " + streak + " " + (streak == 1 ? "day" : "days"));
        }
        
        // Calculate weekly progress
        LocalDate weekAgo = LocalDate.now().minusDays(7);
        long weeklyEntries = data.stream()
            .filter(pd -> !pd.getDate().isBefore(weekAgo))
            .count();
        if (weeklyProgressLabel != null) {
            weeklyProgressLabel.setText("📈 This Week's Progress: " + weeklyEntries + " " + (weeklyEntries == 1 ? "entry" : "entries"));
        }
        
        // Average per week and total entries
        int totalEntries = data.size();
        if (data.size() > 0) {
            // Find date range
            LocalDate earliest = data.stream()
                .map(ProgressData::getDate)
                .min(LocalDate::compareTo)
                .orElse(LocalDate.now());
            LocalDate latest = data.stream()
                .map(ProgressData::getDate)
                .max(LocalDate::compareTo)
                .orElse(LocalDate.now());
            
            long totalWeeks = Math.max(1, ChronoUnit.WEEKS.between(earliest, latest) + 1);
            double avgPerWeek = (double) totalEntries / totalWeeks;
            
            if (averagePerWeekLabel != null) {
                averagePerWeekLabel.setText("📊 Avg/Week: " + String.format("%.1f", avgPerWeek));
            }
        } else {
            if (averagePerWeekLabel != null) {
                averagePerWeekLabel.setText("📊 Avg/Week: 0");
            }
        }
        
        if (totalEntriesCountLabel != null) {
            totalEntriesCountLabel.setText("Total: " + totalEntries + " " + (totalEntries == 1 ? "entry" : "entries"));
        }
        
        // Overall trend
        if (data.size() >= 2) {
            ProgressData latest = data.get(0);
            ProgressData first = data.get(data.size() - 1);
            if (latest.getWeight() > 0 && first.getWeight() > 0) {
                double trend = latest.getWeight() - first.getWeight();
                String trendText = trend < 0 ? "Downward (Losing)" : (trend > 0 ? "Upward (Gaining)" : "Stable");
                String trendEmoji = trend < 0 ? "📉" : (trend > 0 ? "📈" : "➡️");
                if (overallTrendLabel != null) {
                    overallTrendLabel.setText("📊 Overall Trend: " + trendEmoji + " " + trendText);
                }
            } else {
                if (overallTrendLabel != null) {
                    overallTrendLabel.setText("📊 Overall Trend: Tracking in progress");
                }
            }
        } else {
            if (overallTrendLabel != null) {
                overallTrendLabel.setText("📊 Overall Trend: Need more data");
            }
        }
        
        // Consistency score (based on how regularly entries are logged)
        double consistency = calculateConsistency(data);
        if (consistencyLabel != null) {
            consistencyLabel.setText("⭐ Consistency Score: " + String.format("%.0f%%", consistency));
        }
    }
    
    private int calculateStreak(List<ProgressData> data) {
        if (data.isEmpty()) return 0;
        
        // Sort by date (newest first)
        List<ProgressData> sorted = new java.util.ArrayList<>(data);
        sorted.sort((a, b) -> b.getDate().compareTo(a.getDate()));
        
        int streak = 0;
        LocalDate expectedDate = LocalDate.now();
        
        for (ProgressData pd : sorted) {
            if (pd.getDate().equals(expectedDate) || pd.getDate().equals(expectedDate.minusDays(1))) {
                streak++;
                expectedDate = pd.getDate().minusDays(1);
            } else {
                break;
            }
        }
        
        return streak;
    }
    
    private double calculateConsistency(List<ProgressData> data) {
        if (data.size() < 2) return 0;
        
        // Find date range
        LocalDate earliest = data.stream()
            .map(ProgressData::getDate)
            .min(LocalDate::compareTo)
            .orElse(LocalDate.now());
        LocalDate latest = data.stream()
            .map(ProgressData::getDate)
            .max(LocalDate::compareTo)
            .orElse(LocalDate.now());
        
        long totalDays = ChronoUnit.DAYS.between(earliest, latest) + 1;
        if (totalDays == 0) return 0;
        
        // Calculate percentage of days with entries
        return (double) data.size() / totalDays * 100;
    }
    
    private void updateProgressHighlights(List<ProgressData> data) {
        if (highlightsContainer == null || data == null || data.isEmpty()) {
            return;
        }
        
        // Data is already sorted newest first, so get oldest and newest
        ProgressData latest = data.get(0); // Newest (first in list)
        
        // Find oldest entry
        ProgressData first = data.get(0);
        for (ProgressData pd : data) {
            if (pd.getDate().isBefore(first.getDate())) {
                first = pd;
            }
        }
        
        // Days tracking
        if (daysTrackingLabel != null) {
            long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(first.getDate(), latest.getDate());
            daysTrackingLabel.setText("📅 " + (daysBetween + 1) + " " + (daysBetween == 0 ? "day" : "days"));
        }
        
        // Total weight change
        if (totalWeightChangeLabel != null) {
            if (first.getWeight() > 0 && latest.getWeight() > 0) {
                double totalChange = latest.getWeight() - first.getWeight();
                String sign = totalChange >= 0 ? "+" : "";
                String color = totalChange < 0 ? "#27ae60" : (totalChange > 0 ? "#e74c3c" : "#7f8c8d");
                totalWeightChangeLabel.setText("⚖️ " + sign + String.format("%.1f lbs", totalChange));
                totalWeightChangeLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 20px; -fx-font-weight: bold; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 3, 0.5, 0, 1);");
            } else {
                totalWeightChangeLabel.setText("⚖️ --");
            }
        }
        
        // Best calorie day (highest adherence)
        if (bestCalorieDayLabel != null) {
            double bestAdherence = 0;
            for (ProgressData pd : data) {
                if (pd.getCaloriesTarget() > 0) {
                    double adherence = pd.getCalorieAdherence();
                    if (adherence > bestAdherence) {
                        bestAdherence = adherence;
                    }
                }
            }
            if (bestAdherence > 0) {
                bestCalorieDayLabel.setText("🔥 " + String.format("%.0f%%", bestAdherence));
            } else {
                bestCalorieDayLabel.setText("🔥 --");
            }
        }
        
        // Highest steps
        if (highestStepsLabel != null) {
            int highestSteps = 0;
            for (ProgressData pd : data) {
                if (pd.getSteps() > highestSteps) {
                    highestSteps = pd.getSteps();
                }
            }
            if (highestSteps > 0) {
                highestStepsLabel.setText("👣 " + highestSteps);
            } else {
                highestStepsLabel.setText("👣 --");
            }
        }
    }
    
    @FXML
    private void handleAddProgress(ActionEvent event) {
        SceneSwitcher.switchScene(event, "/com/example/demo/add_progress.fxml", "Add Progress Entry - ShapeShift");
    }
    
    private void displayChart(List<ProgressData> data) {
        if (chartContainer == null) return;
        
        chartContainer.getChildren().clear();
        
        // Sort by date (oldest first for chart)
        List<ProgressData> sortedData = new java.util.ArrayList<>(data);
        sortedData.sort((a, b) -> a.getDate().compareTo(b.getDate()));
        
        if (sortedData.isEmpty()) {
            chartContainer.setVisible(false);
            chartContainer.setManaged(false);
            return;
        }
        
        // Apply CSS styling
        String cssUrl = null;
        try {
            cssUrl = getClass().getResource("/com/example/demo/chart-styles.css").toExternalForm();
        } catch (Exception e) {
            // CSS file not found, continue without it
        }
        
        // Prepare date strings for X-axis
        java.util.List<String> dateLabels = new java.util.ArrayList<>();
        for (ProgressData pd : sortedData) {
            dateLabels.add(pd.getDate().format(DateTimeFormatter.ofPattern("MMM d")));
        }
        
        // 1. Weight Trend - Line Chart (alone)
        createWeightChart(sortedData, dateLabels, cssUrl);
        
        // 2. Body Fat % - Line Chart (alone)
        createBodyFatChart(sortedData, dateLabels, cssUrl);
        
        // 3. Calories Consumed - Bar Chart with optional target overlay
        createCaloriesChart(sortedData, dateLabels, cssUrl);
        
        // 4. Steps - Bar Chart
        createStepsChart(sortedData, dateLabels, cssUrl);
        
        chartContainer.setVisible(true);
        chartContainer.setManaged(true);
    }
    
    private void createWeightChart(List<ProgressData> data, java.util.List<String> dateLabels, String cssUrl) {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Date");
        xAxis.setTickLabelFill(Color.BLACK);
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Weight (lbs)");
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(100);
        yAxis.setUpperBound(300);
        yAxis.setTickUnit(10);
        yAxis.setTickLabelFont(javafx.scene.text.Font.font(12));
        yAxis.setTickLabelFill(Color.BLACK);
        
        LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Weight Trend");
        chart.setPrefHeight(250);
        chart.setMinHeight(250);
        chart.setPrefWidth(1100);
        chart.setMinWidth(1100);
        chart.setStyle("-fx-background-color: transparent;");
        chart.setLegendVisible(false);
        chart.setAnimated(true);
        chart.setCreateSymbols(true);
        
        if (cssUrl != null) {
            chart.getStylesheets().add(cssUrl);
        }
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Weight (lbs)");
        
        double minWeight = Double.MAX_VALUE;
        double maxWeight = Double.MIN_VALUE;
        
        for (int i = 0; i < data.size(); i++) {
            ProgressData pd = data.get(i);
            if (pd.getWeight() > 0) {
                String dateLabel = dateLabels.get(i);
                xAxis.getCategories().add(dateLabel);
                series.getData().add(new XYChart.Data<>(dateLabel, pd.getWeight()));
                if (pd.getWeight() < minWeight) minWeight = pd.getWeight();
                if (pd.getWeight() > maxWeight) maxWeight = pd.getWeight();
            }
        }
        
        if (!series.getData().isEmpty()) {
            // Set Y-axis bounds with minimum of 100
            double lowerBound = 100;
            double upperBound = Math.max(maxWeight + 10, 200);
            yAxis.setLowerBound(lowerBound);
            yAxis.setUpperBound(upperBound);
            yAxis.setTickUnit(Math.max(5, (upperBound - lowerBound) / 10));
            
            xAxis.setTickLabelFont(javafx.scene.text.Font.font(11));
            xAxis.setTickLabelRotation(-45);
            chart.getData().add(series);
            Label title = new Label("1. Weight Trend");
            title.setStyle("-fx-text-fill: #2c3e50; -fx-font-size: 20px; -fx-font-weight: bold; -fx-padding: 0 0 3 0;");
            VBox chartBox = new VBox(3);
            chartBox.getChildren().addAll(title, chart);
            chartContainer.getChildren().add(chartBox);
        }
    }
    
    private void createBodyFatChart(List<ProgressData> data, java.util.List<String> dateLabels, String cssUrl) {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Date");
        xAxis.setTickLabelFill(Color.BLACK);
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Body Fat (%)");
        yAxis.setAutoRanging(true);
        yAxis.setTickLabelFont(javafx.scene.text.Font.font(12));
        yAxis.setTickLabelFill(Color.BLACK);
        
        LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Body Fat %");
        chart.setPrefHeight(250);
        chart.setMinHeight(250);
        chart.setPrefWidth(1100);
        chart.setMinWidth(1100);
        chart.setStyle("-fx-background-color: transparent;");
        chart.setLegendVisible(false);
        chart.setAnimated(true);
        chart.setCreateSymbols(true);
        
        if (cssUrl != null) {
            chart.getStylesheets().add(cssUrl);
        }
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Body Fat (%)");
        
        for (int i = 0; i < data.size(); i++) {
            ProgressData pd = data.get(i);
            if (pd.getBodyFatPercentage() > 0) {
                String dateLabel = dateLabels.get(i);
                xAxis.getCategories().add(dateLabel);
                series.getData().add(new XYChart.Data<>(dateLabel, pd.getBodyFatPercentage()));
            }
        }
        
        if (!series.getData().isEmpty()) {
            xAxis.setTickLabelFont(javafx.scene.text.Font.font(11));
            xAxis.setTickLabelRotation(-45);
            chart.getData().add(series);
            Label title = new Label("2. Body Fat %");
            title.setStyle("-fx-text-fill: #2c3e50; -fx-font-size: 20px; -fx-font-weight: bold; -fx-padding: 0 0 3 0;");
            VBox chartBox = new VBox(3);
            chartBox.getChildren().addAll(title, chart);
            chartContainer.getChildren().add(chartBox);
        }
    }
    
    private void createCaloriesChart(List<ProgressData> data, java.util.List<String> dateLabels, String cssUrl) {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Date");
        xAxis.setTickLabelFill(Color.BLACK);
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Calories");
        yAxis.setAutoRanging(true);
        yAxis.setTickLabelFont(javafx.scene.text.Font.font(12));
        yAxis.setTickLabelFill(Color.BLACK);
        
        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Calories Consumed");
        chart.setPrefHeight(250);
        chart.setMinHeight(250);
        chart.setPrefWidth(1100);
        chart.setMinWidth(1100);
        chart.setStyle("-fx-background-color: transparent;");
        chart.setLegendVisible(true);
        chart.setAnimated(true);
        
        if (cssUrl != null) {
            chart.getStylesheets().add(cssUrl);
        }
        
        XYChart.Series<String, Number> consumedSeries = new XYChart.Series<>();
        consumedSeries.setName("Calories Consumed");
        
        XYChart.Series<String, Number> targetSeries = new XYChart.Series<>();
        targetSeries.setName("Calorie Target");
        
        java.util.Set<String> datesAdded = new java.util.HashSet<>();
        for (int i = 0; i < data.size(); i++) {
            ProgressData pd = data.get(i);
            String dateLabel = dateLabels.get(i);
            
            if (pd.getCaloriesConsumed() > 0) {
                if (!datesAdded.contains(dateLabel)) {
                    xAxis.getCategories().add(dateLabel);
                    datesAdded.add(dateLabel);
                }
                consumedSeries.getData().add(new XYChart.Data<>(dateLabel, pd.getCaloriesConsumed()));
            }
            if (pd.getCaloriesTarget() > 0) {
                if (!datesAdded.contains(dateLabel)) {
                    xAxis.getCategories().add(dateLabel);
                    datesAdded.add(dateLabel);
                }
                targetSeries.getData().add(new XYChart.Data<>(dateLabel, pd.getCaloriesTarget()));
            }
        }
        
        if (!consumedSeries.getData().isEmpty() || !targetSeries.getData().isEmpty()) {
            xAxis.setTickLabelFont(javafx.scene.text.Font.font(11));
            xAxis.setTickLabelRotation(-45);
            if (!consumedSeries.getData().isEmpty()) {
                chart.getData().add(consumedSeries);
            }
            if (!targetSeries.getData().isEmpty()) {
                chart.getData().add(targetSeries);
            }
            Label title = new Label("3. Calories Consumed");
            title.setStyle("-fx-text-fill: #2c3e50; -fx-font-size: 20px; -fx-font-weight: bold; -fx-padding: 0 0 3 0;");
            VBox chartBox = new VBox(3);
            chartBox.getChildren().addAll(title, chart);
            chartContainer.getChildren().add(chartBox);
        }
    }
    
    private void createStepsChart(List<ProgressData> data, java.util.List<String> dateLabels, String cssUrl) {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Date");
        xAxis.setTickLabelFill(Color.BLACK);
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Steps");
        yAxis.setAutoRanging(true);
        yAxis.setTickLabelFont(javafx.scene.text.Font.font(12));
        yAxis.setTickLabelFill(Color.BLACK);
        
        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Daily Steps");
        chart.setPrefHeight(250);
        chart.setMinHeight(250);
        chart.setPrefWidth(1100);
        chart.setMinWidth(1100);
        chart.setStyle("-fx-background-color: transparent;");
        chart.setLegendVisible(false);
        chart.setAnimated(true);
        
        if (cssUrl != null) {
            chart.getStylesheets().add(cssUrl);
        }
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Steps");
        
        for (int i = 0; i < data.size(); i++) {
            ProgressData pd = data.get(i);
            if (pd.getSteps() > 0) {
                String dateLabel = dateLabels.get(i);
                xAxis.getCategories().add(dateLabel);
                series.getData().add(new XYChart.Data<>(dateLabel, pd.getSteps()));
            }
        }
        
        if (!series.getData().isEmpty()) {
            xAxis.setTickLabelFont(javafx.scene.text.Font.font(11));
            xAxis.setTickLabelRotation(-45);
            chart.getData().add(series);
            Label title = new Label("4. Steps");
            title.setStyle("-fx-text-fill: #2c3e50; -fx-font-size: 20px; -fx-font-weight: bold; -fx-padding: 0 0 3 0;");
            VBox chartBox = new VBox(3);
            chartBox.getChildren().addAll(title, chart);
            chartContainer.getChildren().add(chartBox);
        }
    }
    
    @FXML
    private void handleBack(ActionEvent event) {
        SceneSwitcher.switchScene(event, "/com/example/demo/workout_home.fxml", "Workout Home");
    }
}
