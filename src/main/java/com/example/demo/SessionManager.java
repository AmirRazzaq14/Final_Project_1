package com.example.demo;

public class SessionManager {
    private static String CurrentUserEmail;
    public static void setCurrentEmail(String email) {
        CurrentUserEmail=email;
    }
    public static String getCurrentEmail() {
        return CurrentUserEmail;
    }
    public static void ClearSession(){
        CurrentUserEmail=null;
    }
}
