package com.example.demo;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class FirebaseConnectionManager {

    private static final String USER_STORE_FILE = "firebase_users.dat";

    private static boolean isConnected = true;
    private static boolean firebaseEnabled = true;
    private static Map<String, String> userCredentials = new HashMap<>();

    static {
        loadUsers();
    }

    public static boolean isConnected() {

        return isConnected && firebaseEnabled;
    }


    public static void setConnected(boolean connected) {
        isConnected = connected;
    }


    public static void setFirebaseEnabled(boolean enabled) {
        firebaseEnabled = enabled;
    }


        public static boolean registerUser(String email, String password) {
            if (email == null || password == null || email.isBlank() || password.isBlank()) {
                return false;
            }

            if (userCredentials.containsKey(email)) {
                return false; // Already registered
            }

            userCredentials.put(email, password);
            saveUsers();
            return true;
        }

        public static boolean authenticate(String email, String password) {
            if (email == null || password == null || email.isBlank() || password.isBlank()) {
                return false;
            }

            String storedPassword = userCredentials.get(email);
            return storedPassword != null && storedPassword.equals(password);
        }

        private static void saveUsers() {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USER_STORE_FILE))) {
                oos.writeObject(userCredentials);
            } catch (IOException e) {
                System.err.println("Failed to persist user accounts: " + e.getMessage());
            }
        }

        @SuppressWarnings("unchecked")
        private static void loadUsers() {
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
                System.err.println("Failed to load Firebase user accounts: " + e.getMessage());
                userCredentials = new HashMap<>();
            }
        }
    }

