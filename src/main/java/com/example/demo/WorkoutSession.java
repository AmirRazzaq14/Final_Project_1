package com.example.demo;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class WorkoutSession implements Serializable {
    private static final long serialVersionUID = 1L;
    private LocalDate date;
    private LocalTime time;
    private List<ExerciseLogEntry> exercises;
    private long durationSeconds;
    
    public WorkoutSession(LocalDate date, LocalTime time, List<ExerciseLogEntry> exercises, long durationSeconds) {
        this.date = date;
        this.time = time;
        this.exercises = exercises;
        this.durationSeconds = durationSeconds;
    }
    
    public LocalDate getDate() { return date; }
    public LocalTime getTime() { return time; }
    public List<ExerciseLogEntry> getExercises() { return exercises; }
    public long getDurationSeconds() { return durationSeconds; }
}





