module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires firebase.admin;
    requires com.google.auth.oauth2;
    requires google.cloud.firestore;


    opens com.example.demo to javafx.fxml;
    exports com.example.demo;
}