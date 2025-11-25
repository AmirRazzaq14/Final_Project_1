package com.example.demo;

import java.util.ArrayList;
import java.util.List;

public class UserProfile {
    private String email;
    private String goal; // "bulk", "cut", "maintain"
    private String experienceLevel; // "beginner", "intermediate", "advanced"
    private int daysPerWeek; // 3-6
    private List<String> injuries; // List of injury areas to avoid
    private double currentWeight;
    private double targetWeight;
    private double bodyFatPercentage;
    
    public UserProfile(String email) {
        this.email = email;
        this.injuries = new ArrayList<>();
        this.daysPerWeek = 3;
    }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getGoal() { return goal; }
    public void setGoal(String goal) { this.goal = goal; }
    
    public String getExperienceLevel() { return experienceLevel; }
    public void setExperienceLevel(String experienceLevel) { this.experienceLevel = experienceLevel; }
    
    public int getDaysPerWeek() { return daysPerWeek; }
    public void setDaysPerWeek(int daysPerWeek) { this.daysPerWeek = daysPerWeek; }
    
    public List<String> getInjuries() { return injuries; }
    public void setInjuries(List<String> injuries) { this.injuries = injuries; }
    
    public double getCurrentWeight() { return currentWeight; }
    public void setCurrentWeight(double currentWeight) { this.currentWeight = currentWeight; }
    
    public double getTargetWeight() { return targetWeight; }
    public void setTargetWeight(double targetWeight) { this.targetWeight = targetWeight; }
    
    public double getBodyFatPercentage() { return bodyFatPercentage; }
    public void setBodyFatPercentage(double bodyFatPercentage) { this.bodyFatPercentage = bodyFatPercentage; }
    
    public boolean isComplete() {
        return goal != null && experienceLevel != null && daysPerWeek > 0;
    }
}





