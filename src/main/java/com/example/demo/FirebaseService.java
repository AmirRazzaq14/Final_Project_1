package com.example.demo;

import java.util.HashMap;
import java.util.Map;

/**
 * Firebase Service for authentication and data management
 * This service handles user registration, login, and data persistence
 */
public class FirebaseService {
    private static FirebaseService instance;
    private Map<String, UserCred> users = new HashMap<>();
    private boolean firebaseEnabled = false;
    
    private FirebaseService() {
        // Initialize with existing users from RegisterController if any
        if (RegisterController.userList != null) {
            for (UserCred user : RegisterController.userList) {
                if (user.getEmail() != null) {
                    users.put(user.getEmail(), user);
                }
            }
        }
    }
    
    public static synchronized FirebaseService getInstance() {
        if (instance == null) {
            instance = new FirebaseService();
        }
        return instance;
    }
    
    /**
     * Register a new user
     */
    public boolean registerUser(String email, String password, String firstName, String lastName) {
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            return false;
        }
        
        // Check if user already exists
        if (users.containsKey(email)) {
            return false;
        }
        
        // Create new user
        UserCred newUser = new UserCred(email, password);
        users.put(email, newUser);
        
        // Also add to RegisterController's list for backward compatibility
        if (!RegisterController.userList.contains(newUser)) {
            RegisterController.userList.add(newUser);
        }
        
        // If Firebase is enabled, save to Firebase here
        if (firebaseEnabled) {
            // TODO: Implement Firebase registration
            // saveUserToFirebase(email, password, firstName, lastName);
        }
        
        return true;
    }
    
    /**
     * Authenticate a user
     */
    public boolean loginUser(String email, String password) {
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            return false;
        }
        
        UserCred user = users.get(email);
        if (user != null && user.getPassword().equals(password)) {
            // If Firebase is enabled, verify with Firebase
            if (firebaseEnabled) {
                // TODO: Implement Firebase authentication
                // return authenticateWithFirebase(email, password);
            }
            return true;
        }
        
        return false;
    }
    
    /**
     * Check if user exists
     */
    public boolean userExists(String email) {
        return users.containsKey(email);
    }
    
    /**
     * Enable Firebase integration
     */
    public void enableFirebase(boolean enabled) {
        this.firebaseEnabled = enabled;
        FirebaseConnectionManager.setFirebaseEnabled(enabled);
        if (enabled) {
            FirebaseConnectionManager.setConnected(true);
        }
    }
    
    /**
     * Get user credentials
     */
    public UserCred getUser(String email) {
        return users.get(email);
    }
}
