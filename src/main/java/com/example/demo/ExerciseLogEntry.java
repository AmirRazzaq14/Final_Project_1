package com.example.demo;

import java.time.LocalDate;

public class ExerciseLogEntry {
    private String exerciseName;
    private int sets;
    private int reps;
    private double weight;
    private int rpe;
    private LocalDate date;
    
    public ExerciseLogEntry(String exerciseName, int sets, int reps, double weight, int rpe, LocalDate date) {
        this.exerciseName = exerciseName;
        this.sets = sets;
        this.reps = reps;
        this.weight = weight;
        this.rpe = rpe;
        this.date = date;
    }
    
    public String getExerciseName() { return exerciseName; }
    public int getSets() { return sets; }
    public int getReps() { return reps; }
    public double getWeight() { return weight; }
    public int getRpe() { return rpe; }
    public LocalDate getDate() { return date; }
}





