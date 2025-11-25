package com.example.demo;

import java.time.LocalDate;

public class PersonalRecord {
    private String exercise;
    private double weight;
    private int reps;
    private LocalDate date;
    
    public PersonalRecord(String exercise, double weight, int reps, LocalDate date) {
        this.exercise = exercise;
        this.weight = weight;
        this.reps = reps;
        this.date = date;
    }
    
    public String getExercise() { return exercise; }
    public double getWeight() { return weight; }
    public int getReps() { return reps; }
    public LocalDate getDate() { return date; }
    
    public void update(double weight, int reps, LocalDate date) {
        if (weight > this.weight || (weight == this.weight && reps > this.reps)) {
            this.weight = weight;
            this.reps = reps;
            this.date = date;
        }
    }
    
    @Override
    public String toString() {
        return String.format("%.1f lbs x %d reps (%s)", weight, reps, date.toString());
    }
}





