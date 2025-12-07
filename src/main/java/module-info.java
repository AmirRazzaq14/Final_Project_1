module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    // Firebase dependencies for authentication only
    // Using automatic module names (derived from JAR names)
    requires firebase.admin;
    requires com.google.auth;
    requires com.google.auth.oauth2;
    // Note: google.cloud.firestore is NOT required - using local storage for data

    opens com.example.demo to javafx.fxml;
    exports com.example.demo;
}