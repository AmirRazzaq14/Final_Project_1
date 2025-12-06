package com.example.demo;

import java.io.*;
import java.util.*;

public class DataManager {
    private static final String PROFILE_FILE = "user_profiles.dat";
    private static final String PROGRESS_FILE = "progress_data.dat";
    private static final String PLANS_FILE = "workout_plans.dat";
    private static final String SESSIONS_FILE = "workout_sessions.dat";
    private static final String SELECTED_WORKOUTS_FILE = "selected_workouts.dat";
    private static final String LOGIN_COUNT_FILE = "login_counts.dat";
    
    private static Map<String, UserProfile> profiles = new HashMap<>();
    private static Map<String, List<ProgressData>> progressData = new HashMap<>();
    private static Map<String, WorkoutPlan> workoutPlans = new HashMap<>();
    private static Map<String, List<WorkoutSession>> workoutSessions = new HashMap<>();
    private static Map<String, List<String>> selectedWorkouts = new HashMap<>();
    private static Map<String, Integer> loginCounts = new HashMap<>();
    
    static {
        loadData();
    }
    
    public static void saveProfile(UserProfile profile) {
        profiles.put(profile.getEmail(), profile);
        saveData();
    }
    
    public static UserProfile getProfile(String email) {
        return profiles.get(email);
    }
    
    public static void saveProgress(String email, ProgressData data) {
        progressData.computeIfAbsent(email, k -> new ArrayList<>()).add(data);
        saveData();
    }
    
    public static List<ProgressData> getProgress(String email) {
        return progressData.getOrDefault(email, new ArrayList<>());
    }
    
    public static void saveWorkoutPlan(String email, WorkoutPlan plan) {
        workoutPlans.put(email, plan);
        saveData();
    }
    
    public static WorkoutPlan getWorkoutPlan(String email) {
        return workoutPlans.get(email);
    }
    
    public static void saveWorkoutSession(String email, WorkoutSession session) {
        workoutSessions.computeIfAbsent(email, k -> new ArrayList<>()).add(session);
        saveData();
    }
    
    public static List<WorkoutSession> getWorkoutSessions(String email) {
        return workoutSessions.getOrDefault(email, new ArrayList<>());
    }
    
    public static void saveSelectedWorkouts(String email, List<String> workouts) {
        selectedWorkouts.put(email, new ArrayList<>(workouts));
        saveData();
    }
    
    public static List<String> getSelectedWorkouts(String email) {
        return selectedWorkouts.getOrDefault(email, new ArrayList<>());
    }
    
    public static void incrementLoginCount(String email) {
        loginCounts.put(email, loginCounts.getOrDefault(email, 0) + 1);
        saveData();
    }
    
    public static int getLoginCount(String email) {
        return loginCounts.getOrDefault(email, 0);
    }
    
    private static void saveData() {
        try {
            // Save profiles
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(PROFILE_FILE))) {
                oos.writeObject(profiles);
            }
            
            // Save progress (simplified - in production use JSON or database)
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(PROGRESS_FILE))) {
                oos.writeObject(progressData);
            }
            
            // Save plans (simplified)
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(PLANS_FILE))) {
                oos.writeObject(workoutPlans);
            }
            
            // Save workout sessions
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SESSIONS_FILE))) {
                oos.writeObject(workoutSessions);
            }
            
            // Save selected workouts
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SELECTED_WORKOUTS_FILE))) {
                oos.writeObject(selectedWorkouts);
            }
            
            // Save login counts
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(LOGIN_COUNT_FILE))) {
                oos.writeObject(loginCounts);
            }
        } catch (IOException e) {
            System.err.println("Error saving data: " + e.getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    private static void loadData() {
        try {
            // Load profiles
            File file = new File(PROFILE_FILE);
            if (file.exists()) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                    profiles = (Map<String, UserProfile>) ois.readObject();
                }
            }
            
            // Load progress
            file = new File(PROGRESS_FILE);
            if (file.exists()) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                    progressData = (Map<String, List<ProgressData>>) ois.readObject();
                }
            }
            
            // Load plans
            file = new File(PLANS_FILE);
            if (file.exists()) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                    workoutPlans = (Map<String, WorkoutPlan>) ois.readObject();
                }
            }
            
            // Load workout sessions
            file = new File(SESSIONS_FILE);
            if (file.exists()) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                    workoutSessions = (Map<String, List<WorkoutSession>>) ois.readObject();
                }
            }
            
            // Load selected workouts
            file = new File(SELECTED_WORKOUTS_FILE);
            if (file.exists()) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                    selectedWorkouts = (Map<String, List<String>>) ois.readObject();
                }
            }
            
            // Load login counts
            file = new File(LOGIN_COUNT_FILE);
            if (file.exists()) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                    loginCounts = (Map<String, Integer>) ois.readObject();
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading data: " + e.getMessage());
            // Initialize empty maps if loading fails
            profiles = new HashMap<>();
            progressData = new HashMap<>();
            workoutPlans = new HashMap<>();
            workoutSessions = new HashMap<>();
            selectedWorkouts = new HashMap<>();
            loginCounts = new HashMap<>();
        }
    }
}

