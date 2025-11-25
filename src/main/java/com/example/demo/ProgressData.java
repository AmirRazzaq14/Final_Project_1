package com.example.demo;

import java.time.LocalDate;

public class ProgressData {
    private LocalDate date;
    private double weight;
    private double bodyFatPercentage;
    private int caloriesConsumed;
    private int caloriesTarget;
    private int steps;
    private PersonalRecord benchPR;
    private PersonalRecord squatPR;
    private PersonalRecord deadliftPR;
    
    public ProgressData(LocalDate date) {
        this.date = date;
    }
    
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    
    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }
    
    public double getBodyFatPercentage() { return bodyFatPercentage; }
    public void setBodyFatPercentage(double bodyFatPercentage) { this.bodyFatPercentage = bodyFatPercentage; }
    
    public int getCaloriesConsumed() { return caloriesConsumed; }
    public void setCaloriesConsumed(int caloriesConsumed) { this.caloriesConsumed = caloriesConsumed; }
    
    public int getCaloriesTarget() { return caloriesTarget; }
    public void setCaloriesTarget(int caloriesTarget) { this.caloriesTarget = caloriesTarget; }
    
    public int getSteps() { return steps; }
    public void setSteps(int steps) { this.steps = steps; }
    
    public PersonalRecord getBenchPR() { return benchPR; }
    public void setBenchPR(PersonalRecord benchPR) { this.benchPR = benchPR; }
    
    public PersonalRecord getSquatPR() { return squatPR; }
    public void setSquatPR(PersonalRecord squatPR) { this.squatPR = squatPR; }
    
    public PersonalRecord getDeadliftPR() { return deadliftPR; }
    public void setDeadliftPR(PersonalRecord deadliftPR) { this.deadliftPR = deadliftPR; }
    
    public double getCalorieAdherence() {
        if (caloriesTarget == 0) return 0;
        return (double) caloriesConsumed / caloriesTarget * 100;
    }
}





