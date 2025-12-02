package com.example.demo;

public class UserCred {

    public String email;
    public String password;

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

