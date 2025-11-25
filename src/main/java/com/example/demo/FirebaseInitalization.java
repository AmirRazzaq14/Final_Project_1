package com.example.demo;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import java.io.FileInputStream;
import java.io.IOException;

public class FirebaseInitalization {
    private static boolean initialized = false;
    private static Firestore db;

    public static Firestore getFirestore() {
        if (!initialized) {
            initializeFirebase();
        }
        return db;
    }
    private static void initializeFirebase() {
try{
            FileInputStream serviceAccount =
                    new FileInputStream("src/main/resources/com/example/demo/key.json");

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://console.firebase.google.com/u/0/project/shapeshift-184c8/database/shapeshift-184c8-default-rtdb/data/~2F")
                    .build();

            FirebaseApp.initializeApp(options);

        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }

 return FirestoreClient.getFirestore();
}
