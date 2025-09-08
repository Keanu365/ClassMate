module com.example.classmate {
    requires javafx.controls;
    requires javafx.fxml;
    requires google.genai;
    requires javafx.graphics;
    requires java.compiler;
    requires javafx.base;
    requires java.desktop;
    requires javafx.swing;


    opens com.example.classmate to javafx.fxml;
    exports com.example.classmate;
}