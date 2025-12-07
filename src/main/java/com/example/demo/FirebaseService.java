package com.example.demo;

/**
 * Firebase Service for authentication only
 * This service handles user registration and login using Firebase Auth
 * All other data (profiles, workouts, progress) is stored locally via DataManager
 */
public class FirebaseService {
    private static FirebaseService instance;
    
    private FirebaseService() {
        // Initialize Firebase connection
        FirebaseConnectionManager.setConnected(true);
    }
    
    public static synchronized FirebaseService getInstance() {
        if (instance == null) {
            instance = new FirebaseService();
        }
        return instance;
    }
    
    /**
     * Register a new user using Firebase Auth
     * Falls back to local storage if Firebase is not configured
     */
    public boolean registerUser(String email, String password, String firstName, String lastName) {
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            return false;
        }
        
        // Use FirebaseConnectionManager for registration
        boolean success = FirebaseConnectionManager.registerUser(email, password);
        
        if (success) {
            // Also add to RegisterController's list for backward compatibility
            UserCred newUser = new UserCred(email, password);
            if (!RegisterController.userList.contains(newUser)) {
                RegisterController.userList.add(newUser);
            }
        }
        
        return success;
    }
    
    /**
     * Authenticate a user using Firebase Auth
     * Falls back to local storage if Firebase is not configured
     */
    public boolean loginUser(String email, String password) {
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            return false;
        }
        
        // Use FirebaseConnectionManager for authentication
        return FirebaseConnectionManager.authenticate(email, password);
    }
    
    /**
     * Check if user exists in Firebase or local storage
     */
    public boolean userExists(String email) {
        return FirebaseConnectionManager.userExists(email);
    }
    
    /**
     * Enable Firebase integration
     * Note: Firebase is automatically enabled if service account key is found
     */
    public void enableFirebase(boolean enabled) {
        FirebaseConnectionManager.setFirebaseEnabled(enabled);
        if (enabled) {
            FirebaseConnectionManager.setConnected(true);
        }
    }
    
    /**
     * Get user credentials (for backward compatibility)
     * Note: In Firebase mode, we don't store passwords locally
     */
    public UserCred getUser(String email) {
        // Return a UserCred object for backward compatibility
        // Password is not stored when using Firebase Auth
        return new UserCred(email, "");
    }
    
    /**
     * Check if Firebase is connected
     */
    public boolean isFirebaseConnected() {
        return FirebaseConnectionManager.isConnected();
    }
}
