module com.taskmanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.example.taskmanager.controller to javafx.fxml;
    opens com.example.taskmanager.model to javafx.base;
    opens com.example to javafx.graphics;

    exports com.example.taskmanager.controller;
    exports com.example.taskmanager.model;
    exports com.example;
}
