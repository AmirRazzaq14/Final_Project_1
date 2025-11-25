package com.example.demo;

public class WorkoutExercise {
    private String name;
    private int sets;
    private int reps;
    private double weight; // Optional, for progressive overload
    private int restSeconds;
    private String notes;
    
    public WorkoutExercise(String name, int sets, int reps) {
        this.name = name;
        this.sets = sets;
        this.reps = reps;
        this.restSeconds = 60; // Default 60 seconds
        this.weight = 0;
    }
    
    public WorkoutExercise(String name, int sets, int reps, double weight, int restSeconds) {
        this.name = name;
        this.sets = sets;
        this.reps = reps;
        this.weight = weight;
        this.restSeconds = restSeconds;
    }
    
    public String getName() { return name; }
    public int getSets() { return sets; }
    public int getReps() { return reps; }
    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }
    public int getRestSeconds() { return restSeconds; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    @Override
    public String toString() {
        if (weight > 0) {
            return String.format("%s: %d sets x %d reps @ %.1f lbs", name, sets, reps, weight);
        }
        return String.format("%s: %d sets x %d reps", name, sets, reps);
    }
}





