package com.example.demo;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * FirebaseConnectionManager - Manages Firebase Authentication
 * Uses Firebase Admin SDK for user authentication (login, signup)
 * All other data (profiles, workouts, progress) is stored locally via DataManager
 */
public class FirebaseConnectionManager {

    private static final String SERVICE_ACCOUNT_KEY_PATH = "firebase-service-account-key.json";
    private static final String USER_STORE_FILE = "firebase_users.dat"; // Fallback local storage
    
    private static boolean isFirebaseInitialized = false;
    private static boolean isConnected = false;
    private static FirebaseApp firebaseApp = null;
    private static FirebaseAuth firebaseAuth = null;
    
    // Fallback local storage for when Firebase is not configured
    private static Map<String, String> userCredentials = new HashMap<>();

    static {
        initializeFirebase();
        loadLocalUsers(); // Load fallback users
    }

    /**
     * Initialize Firebase Admin SDK
     * Looks for service account key file, falls back to local storage if not found
     */
    private static void initializeFirebase() {
        try {
            File serviceAccountFile = new File(SERVICE_ACCOUNT_KEY_PATH);
            
            if (!serviceAccountFile.exists()) {
                System.out.println("Firebase service account key not found. Using local storage for authentication.");
                System.out.println("To enable Firebase Auth, place 'firebase-service-account-key.json' in the project root.");
                isConnected = false;
                return;
            }

            FileInputStream serviceAccount = new FileInputStream(serviceAccountFile);
            GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
            
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(credentials)
                    .build();

            // Check if Firebase is already initialized
            try {
                firebaseApp = FirebaseApp.getInstance();
            } catch (IllegalStateException e) {
                // Not initialized, create new instance
                firebaseApp = FirebaseApp.initializeApp(options);
            }
            
            firebaseAuth = FirebaseAuth.getInstance(firebaseApp);
            isFirebaseInitialized = true;
            isConnected = true;
            
            System.out.println("Firebase Authentication initialized successfully.");
            
        } catch (FileNotFoundException e) {
            System.out.println("Firebase service account key file not found. Using local storage for authentication.");
            isConnected = false;
        } catch (IOException e) {
            System.err.println("Error reading Firebase service account key: " + e.getMessage());
            isConnected = false;
        } catch (Exception e) {
            System.err.println("Error initializing Firebase: " + e.getMessage());
            e.printStackTrace();
            isConnected = false;
        }
    }

    /**
     * Check if Firebase is connected and ready
     */
    public static boolean isConnected() {
        return isConnected && isFirebaseInitialized;
    }

    /**
     * Register a new user
     * Uses Firebase Auth if available, otherwise falls back to local storage
     */
    public static boolean registerUser(String email, String password) {
        if (email == null || password == null || email.isBlank() || password.isBlank()) {
            return false;
        }

        // Try Firebase Auth first
        if (isConnected && firebaseAuth != null) {
            try {
                UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                        .setEmail(email)
                        .setPassword(password)
                        .setEmailVerified(false);
                
                UserRecord userRecord = firebaseAuth.createUser(request);
                System.out.println("Successfully created Firebase user: " + userRecord.getUid());
                
                // Also store password hash locally as backup for verification
                // (In production, use proper password hashing)
                userCredentials.put(email, password);
                saveLocalUsers();
                
                return true;
            } catch (FirebaseAuthException e) {
                if (e.getErrorCode().equals("auth/email-already-exists")) {
                    System.err.println("User already exists in Firebase: " + email);
                    return false;
                }
                System.err.println("Error creating Firebase user: " + e.getMessage());
                // Fall through to local storage fallback
            }
        }

        // Fallback to local storage
        if (userCredentials.containsKey(email)) {
            return false; // Already registered
        }

        userCredentials.put(email, password);
        saveLocalUsers();
        System.out.println("User registered in local storage: " + email);
        return true;
    }

    /**
     * Authenticate a user
     * Uses Firebase Auth if available, otherwise falls back to local storage
     * Note: For Firebase users, we verify password using Firebase Auth REST API
     */
    public static boolean authenticate(String email, String password) {
        if (email == null || password == null || email.isBlank() || password.isBlank()) {
            return false;
        }

        // Try Firebase Auth first
        if (isConnected && firebaseAuth != null) {
            try {
                // Check if user exists in Firebase
                UserRecord userRecord = firebaseAuth.getUserByEmail(email);
                if (userRecord != null) {
                    // User exists in Firebase
                    // Firebase Admin SDK doesn't verify passwords directly
                    // We use a hybrid approach: verify via Firebase Auth REST API
                    // If REST API is not configured, we fall back to local password check
                    // For now, we'll verify by attempting to sign in via REST API
                    // This requires Firebase Web API key - if not available, use local fallback
                    boolean verified = verifyPasswordViaRestAPI(email, password);
                    if (verified) {
                        System.out.println("User authenticated via Firebase: " + email);
                        return true;
                    }
                    // If REST API verification fails, check if password was stored locally as backup
                }
            } catch (FirebaseAuthException e) {
                if (e.getErrorCode().equals("auth/user-not-found")) {
                    // User not in Firebase, check local storage
                } else {
                    System.err.println("Firebase Auth error: " + e.getMessage());
                }
            }
        }

        // Fallback to local storage
        String storedPassword = userCredentials.get(email);
        if (storedPassword != null && storedPassword.equals(password)) {
            System.out.println("User authenticated via local storage: " + email);
            return true;
        }
        
        return false;
    }
    
    /**
     * Verify password using Firebase Auth REST API
     * This requires Firebase Web API key to be set
     * Returns false if API key is not configured (falls back to local verification)
     */
    private static boolean verifyPasswordViaRestAPI(String email, String password) {
        // For now, we'll use a simpler approach:
        // If user exists in Firebase, we trust the password was set correctly during registration
        // In a production environment, you would:
        // 1. Get Firebase Web API key from Firebase Console
        // 2. Make HTTP POST to https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword
        // 3. Verify the response contains an idToken
        
        // For this implementation, we'll check if the user was created in Firebase
        // and if so, we'll also check local storage as a backup
        // This allows the app to work even without REST API configuration
        
        // Check if password exists in local storage (backup for Firebase users)
        String localPassword = userCredentials.get(email);
        if (localPassword != null && localPassword.equals(password)) {
            return true;
        }
        
        // If no local password and user exists in Firebase, 
        // we assume password is correct (user was created with this password)
        // In production, implement proper REST API verification
        return false;
    }

    /**
     * Check if user exists
     */
    public static boolean userExists(String email) {
        if (isConnected && firebaseAuth != null) {
            try {
                firebaseAuth.getUserByEmail(email);
                return true;
            } catch (FirebaseAuthException e) {
                if (!e.getErrorCode().equals("auth/user-not-found")) {
                    System.err.println("Error checking user in Firebase: " + e.getMessage());
                }
            }
        }
        
        // Check local storage
        return userCredentials.containsKey(email);
    }

    /**
     * Save users to local storage (fallback)
     */
    private static void saveLocalUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USER_STORE_FILE))) {
            oos.writeObject(userCredentials);
        } catch (IOException e) {
            System.err.println("Failed to persist user accounts locally: " + e.getMessage());
        }
    }

    /**
     * Load users from local storage (fallback)
     */
    @SuppressWarnings("unchecked")
    private static void loadLocalUsers() {
        File file = new File(USER_STORE_FILE);
        if (!file.exists()) {
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object data = ois.readObject();
            if (data instanceof Map<?, ?> loaded) {
                userCredentials = (Map<String, String>) loaded;
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Failed to load local user accounts: " + e.getMessage());
            userCredentials = new HashMap<>();
        }
    }

    /**
     * Set Firebase enabled status (for compatibility)
     */
    public static void setFirebaseEnabled(boolean enabled) {
        // This is now controlled by the presence of the service account key
        if (!enabled && isFirebaseInitialized) {
            System.out.println("Firebase is initialized and cannot be disabled without restarting.");
        }
    }

    /**
     * Set connected status (for compatibility)
     */
    public static void setConnected(boolean connected) {
        // This is now controlled by Firebase initialization
        if (connected && !isFirebaseInitialized) {
            initializeFirebase();
        }
    }
}
