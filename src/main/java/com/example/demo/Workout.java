package com.example.demo;

import java.util.List;

public class Workout {
    private String title;
    private List<Exercise> exercises;

    public Workout(String title, List<Exercise> exercises) {
        this.title = title;
        this.exercises = exercises;
    }

    public String getTitle() { return title; }
    public List<Exercise> getExercises() { return exercises; }
}
