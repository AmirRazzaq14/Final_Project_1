package com.example.demo;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

public class DailyWorkout {
    private DayOfWeek day;
    private String focusArea; // "Upper Body", "Lower Body", "Full Body", "Cardio", "Rest"
    private List<WorkoutExercise> exercises;
    private boolean isRestDay;
    
    public DailyWorkout(DayOfWeek day, String focusArea) {
        this.day = day;
        this.focusArea = focusArea;
        this.exercises = new ArrayList<>();
        this.isRestDay = "Rest".equals(focusArea);
    }
    
    public DayOfWeek getDay() { return day; }
    public String getFocusArea() { return focusArea; }
    public List<WorkoutExercise> getExercises() { return exercises; }
    public boolean isRestDay() { return isRestDay; }
    
    public void addExercise(WorkoutExercise exercise) {
        exercises.add(exercise);
    }
}





