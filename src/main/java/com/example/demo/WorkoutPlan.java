package com.example.demo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class WorkoutPlan {
    private String planName;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<DailyWorkout> weeklyPlan;
    private String goal;
    private int currentWeek;
    
    public WorkoutPlan(String planName, LocalDate startDate, String goal) {
        this.planName = planName;
        this.startDate = startDate;
        this.goal = goal;
        this.weeklyPlan = new ArrayList<>();
        this.currentWeek = 1;
        this.endDate = startDate.plusWeeks(4); // Default 4-week plan
    }
    
    public String getPlanName() { return planName; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public List<DailyWorkout> getWeeklyPlan() { return weeklyPlan; }
    public String getGoal() { return goal; }
    public int getCurrentWeek() { return currentWeek; }
    public void setCurrentWeek(int currentWeek) { this.currentWeek = currentWeek; }
    
    public void addDailyWorkout(DailyWorkout workout) {
        weeklyPlan.add(workout);
    }
}





