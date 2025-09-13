module com.example.classmate {
    requires javafx.controls;
    requires javafx.fxml;
    requires google.genai;
    requires javafx.graphics;
    requires java.compiler;
    requires javafx.base;
    requires java.desktop;
    requires javafx.swing;
    requires org.fxmisc.richtext;


    opens com.example.classmate to javafx.fxml;
    exports com.example.classmate;
    exports com.example.classmate.Model;
    opens com.example.classmate.Model to javafx.fxml;
    exports com.example.classmate.Controller;
    opens com.example.classmate.Controller to javafx.fxml;
}