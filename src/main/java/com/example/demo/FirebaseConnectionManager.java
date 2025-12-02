package com.example.demo;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import java.io.FileInputStream;
import java.io.IOException;

public class FirebaseConnectionManager {
    private static boolean isConnected = false;
    private static boolean firebaseEnabled = false;
    public static boolean isConnected() {
        // For now, always return false since Firebase isn't implemented yet
        // When Firebase is implemented, check actual connection status here
        return isConnected && firebaseEnabled;
    }

    public static void setConnected(boolean connected) {
        isConnected = connected;
    }

    public static void setFirebaseEnabled(boolean enabled) {
        firebaseEnabled = enabled;
    }

    private static Firestore db;
    public static Firestore getFirestore() {
        if (db == null) {
            initializeFirebase();
        }
        return db;
    }





    private static void initializeFirebase() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                FileInputStream serviceAccount =
                        new FileInputStream("src/main/resources/com/example/demo/key.json");

                FirebaseOptions options = new FirebaseOptions.Builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .setProjectId("shapeshift-184c8")
                        .build();

                FirebaseApp.initializeApp(options);
            }

            db = FirestoreClient.getFirestore();

        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }
    public static void showConnectionWarning() {
        // This will be called by pages that need Firebase
        // For now, we'll show warnings in controllers
    }
}




