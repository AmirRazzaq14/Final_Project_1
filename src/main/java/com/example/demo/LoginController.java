package com.example.demo;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert;
import javafx.event.ActionEvent;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import static com.example.demo.SceneSwitcher.switchScene;

public class LoginController {
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    @FXML
    public void handleLogin(ActionEvent event) {

        String inputEmail = emailField.getText();
        String inputPassword = passwordField.getText();
        if (inputEmail.isEmpty() || inputPassword.isEmpty()) {
            showAlert("Both fields are required!");
            return;
        }

        try {
            Firestore db = FirebaseInitalization.getFirestore();
            DocumentReference docRef = db.collection("users").document(inputEmail);
            if (!docRef.get().get().exists()) {
                showAlert("Email not registered!");
                return;
            }
            UserCred user = docRef.get().get().toObject(UserCred.class);
            if (user != null && user.getPassword().equals(inputPassword)) {
                showAlert("Login successful!");
                switchScene(event, "/com/example/demo/workout_home.fxml", "Workout Home");
            } else {
                showAlert("Incorrect password!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error: " + e.getMessage());
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
