package com.example.demo;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

public class WorkoutPlanGenerator {
    
    private static final Map<String, Map<String, List<String>>> EXERCISE_LIBRARY = new HashMap<>();
    
    static {
        // Upper Body exercises
        Map<String, List<String>> upperBody = new HashMap<>();
        upperBody.put("beginner", Arrays.asList("Push-Ups", "Incline Push-Ups", "Arm Raises", "Chest Press Motion"));
        upperBody.put("intermediate", Arrays.asList("Push-Ups", "Incline Push-Ups", "Dumbbell Chest Press", "Barbell Row", "Dumbbell Curl"));
        upperBody.put("advanced", Arrays.asList("Bench Press", "Incline Bench Press", "Barbell Row", "Overhead Press", "Dumbbell Row"));
        
        // Lower Body exercises
        Map<String, List<String>> lowerBody = new HashMap<>();
        lowerBody.put("beginner", Arrays.asList("Squats", "Lunges", "Calf Raises", "Leg Raises"));
        lowerBody.put("intermediate", Arrays.asList("Squats", "Lunges", "Romanian Deadlift", "Leg Press", "Calf Raises"));
        lowerBody.put("advanced", Arrays.asList("Barbell Squat", "Romanian Deadlift", "Leg Press", "Hip Thrust", "Front Squat"));
        
        // Full Body exercises
        Map<String, List<String>> fullBody = new HashMap<>();
        fullBody.put("beginner", Arrays.asList("Jumping Jacks", "Squats", "Push-Ups", "Burpees", "Plank"));
        fullBody.put("intermediate", Arrays.asList("Burpees", "Squats", "Push-Ups", "Mountain Climbers", "Plank"));
        fullBody.put("advanced", Arrays.asList("Burpees", "Jump Squat", "Push-Ups", "Plank", "Mountain Climbers"));
        
        // Cardio exercises
        Map<String, List<String>> cardio = new HashMap<>();
        cardio.put("beginner", Arrays.asList("Run in Place", "High Knees", "Jumping Jacks", "Skipping"));
        cardio.put("intermediate", Arrays.asList("Run in Place", "High Knees", "Burpees", "Jumping Jacks"));
        cardio.put("advanced", Arrays.asList("Run in Place", "Burpees", "High Knees", "Mountain Climbers"));
        
        // Core exercises
        Map<String, List<String>> core = new HashMap<>();
        core.put("beginner", Arrays.asList("Plank", "Bicycle Crunches", "Leg Raises", "Crunches"));
        core.put("intermediate", Arrays.asList("Plank", "Bicycle Crunches", "Leg Raises", "Russian Twist", "Side Plank"));
        core.put("advanced", Arrays.asList("Plank", "Hanging Leg Raise", "Russian Twist", "Side Plank", "Ab Wheel"));
        
        EXERCISE_LIBRARY.put("Upper Body", upperBody);
        EXERCISE_LIBRARY.put("Lower Body", lowerBody);
        EXERCISE_LIBRARY.put("Full Body", fullBody);
        EXERCISE_LIBRARY.put("Cardio", cardio);
        EXERCISE_LIBRARY.put("Core", core);
    }
    
    public static WorkoutPlan generatePlan(UserProfile profile) {
        String planName = profile.getGoal().substring(0, 1).toUpperCase() + profile.getGoal().substring(1) + " Plan";
        WorkoutPlan plan = new WorkoutPlan(planName, LocalDate.now(), profile.getGoal());
        
        List<String> focusAreas = determineFocusAreas(profile);
        int daysPerWeek = profile.getDaysPerWeek();
        
        // Generate weekly schedule
        DayOfWeek[] days = {DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, 
                           DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY};
        
        int areaIndex = 0;
        for (int i = 0; i < daysPerWeek && i < days.length; i++) {
            String focusArea = focusAreas.get(areaIndex % focusAreas.size());
            DailyWorkout workout = createDailyWorkout(days[i], focusArea, profile);
            plan.addDailyWorkout(workout);
            areaIndex++;
        }
        
        // Add rest days
        for (int i = daysPerWeek; i < 7; i++) {
            DailyWorkout restDay = new DailyWorkout(days[i], "Rest");
            plan.addDailyWorkout(restDay);
        }
        
        return plan;
    }
    
    private static List<String> determineFocusAreas(UserProfile profile) {
        List<String> areas = new ArrayList<>();
        String goal = profile.getGoal();
        int days = profile.getDaysPerWeek();
        
        if (days <= 3) {
            // Full body splits
            areas.add("Full Body");
        } else if (days == 4) {
            // Upper/Lower split
            areas.add("Upper Body");
            areas.add("Lower Body");
        } else if (days >= 5) {
            // Push/Pull/Legs or more specific
            if ("bulk".equals(goal)) {
                areas.add("Upper Body");
                areas.add("Lower Body");
                areas.add("Upper Body");
                areas.add("Lower Body");
                if (days >= 6) {
                    areas.add("Cardio");
                    areas.add("Core");
                }
            } else if ("cut".equals(goal)) {
                areas.add("Full Body");
                areas.add("Cardio");
                areas.add("Full Body");
                areas.add("Cardio");
                if (days >= 6) {
                    areas.add("Core");
                    areas.add("Cardio");
                }
            } else { // maintain
                areas.add("Upper Body");
                areas.add("Lower Body");
                areas.add("Full Body");
                areas.add("Cardio");
                if (days >= 6) {
                    areas.add("Core");
                }
            }
        }
        
        return areas;
    }
    
    private static DailyWorkout createDailyWorkout(DayOfWeek day, String focusArea, UserProfile profile) {
        DailyWorkout workout = new DailyWorkout(day, focusArea);
        
        if (workout.isRestDay()) {
            return workout;
        }
        
        String experience = profile.getExperienceLevel();
        List<String> availableExercises = EXERCISE_LIBRARY.getOrDefault(focusArea, 
            EXERCISE_LIBRARY.get("Full Body")).getOrDefault(experience, 
            EXERCISE_LIBRARY.get("Full Body").get("beginner"));
        
        // Filter out exercises that might aggravate injuries
        List<String> safeExercises = filterForInjuries(availableExercises, profile.getInjuries());
        
        // Determine sets and reps based on goal and experience
        int sets = getSetsForLevel(experience, profile.getGoal());
        int reps = getRepsForGoal(profile.getGoal(), experience);
        
        // Select 4-6 exercises for the workout
        int numExercises = Math.min(6, safeExercises.size());
        Collections.shuffle(safeExercises);
        
        for (int i = 0; i < numExercises; i++) {
            String exerciseName = safeExercises.get(i);
            WorkoutExercise exercise = new WorkoutExercise(exerciseName, sets, reps);
            workout.addExercise(exercise);
        }
        
        return workout;
    }
    
    private static List<String> filterForInjuries(List<String> exercises, List<String> injuries) {
        if (injuries == null || injuries.isEmpty()) {
            return new ArrayList<>(exercises);
        }
        
        List<String> safe = new ArrayList<>();
        Map<String, List<String>> injuryMap = new HashMap<>();
        injuryMap.put("knee", Arrays.asList("Squat", "Lunge", "Leg Press", "Jump"));
        injuryMap.put("shoulder", Arrays.asList("Push-Up", "Press", "Raise", "Fly"));
        injuryMap.put("back", Arrays.asList("Deadlift", "Row", "Extension"));
        injuryMap.put("wrist", Arrays.asList("Push-Up", "Press", "Curl"));
        
        for (String exercise : exercises) {
            boolean isSafe = true;
            for (String injury : injuries) {
                List<String> avoidExercises = injuryMap.getOrDefault(injury.toLowerCase(), Collections.emptyList());
                for (String avoid : avoidExercises) {
                    if (exercise.toLowerCase().contains(avoid.toLowerCase())) {
                        isSafe = false;
                        break;
                    }
                }
                if (!isSafe) break;
            }
            if (isSafe) {
                safe.add(exercise);
            }
        }
        
        return safe.isEmpty() ? new ArrayList<>(exercises) : safe;
    }
    
    private static int getSetsForLevel(String experience, String goal) {
        if ("bulk".equals(goal)) {
            return experience.equals("beginner") ? 3 : experience.equals("intermediate") ? 4 : 5;
        } else if ("cut".equals(goal)) {
            return experience.equals("beginner") ? 2 : 3;
        } else {
            return experience.equals("beginner") ? 3 : 4;
        }
    }
    
    private static int getRepsForGoal(String goal, String experience) {
        if ("bulk".equals(goal)) {
            return experience.equals("beginner") ? 8 : experience.equals("intermediate") ? 6 : 5;
        } else if ("cut".equals(goal)) {
            return experience.equals("beginner") ? 15 : 12;
        } else {
            return experience.equals("beginner") ? 10 : 8;
        }
    }
    
    public static WorkoutPlan updatePlan(WorkoutPlan currentPlan, UserProfile profile, 
                                       Map<String, Double> weightProgress) {
        // Update weights based on progression
        for (DailyWorkout daily : currentPlan.getWeeklyPlan()) {
            if (daily.isRestDay()) continue;
            
            for (WorkoutExercise exercise : daily.getExercises()) {
                String exerciseName = exercise.getName();
                if (weightProgress.containsKey(exerciseName)) {
                    exercise.setWeight(weightProgress.get(exerciseName));
                }
            }
        }
        
        return currentPlan;
    }
}





