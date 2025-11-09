package com.example.demo;

public class UserCred {

    private String email;
    private String password;

    public UserCred(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}

