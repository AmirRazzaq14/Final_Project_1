package com.example.demo;

public class Exercise {
    private String name;
    private String detail;
    private String image;

    public Exercise(String name, String detail, String image) {
        this.name = name;
        this.detail = detail;
        this.image = image;
    }

    public String getName() { return name; }
    public String getDetail() { return detail; }
    public String getImage() { return image; }
}
