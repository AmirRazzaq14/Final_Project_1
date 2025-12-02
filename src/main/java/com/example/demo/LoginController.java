package com.example.demo;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.DocumentReference;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert;
import java.util.concurrent.ExecutionException;
import static com.example.demo.SceneSwitcher.switchScene;

public class LoginController {
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML
    public void handleLogin(ActionEvent event) {
        String inputEmail = emailField.getText();
        String inputPassword = passwordField.getText();
        Firestore db = FirebaseConnectionManager.getFirestore();
        DocumentReference userDoc = db.collection("user").document(inputEmail);
        try {
            DocumentSnapshot snapshot = userDoc.get().get();
            if (!snapshot.exists()) {
                showAlert("User does not exist!");
                return;
            }

            String storedPassword = snapshot.getString("password");
            if (storedPassword != null && storedPassword.equals(inputPassword)) {
                SessionManager.setCurrentEmail(inputEmail);
                switchScene(event, "/com/example/demo/workout_home.fxml", "Workout Home");
            } else {
                showAlert("Incorrect password!");
            }

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            showAlert("Error accessing database: " + e.getMessage());
        }
    }
    @FXML
    public void handleShowRegister(ActionEvent event) {
        switchScene(event, "/com/example/demo/register.fxml", "Register - ShapeShift");
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.show();
    }
}
