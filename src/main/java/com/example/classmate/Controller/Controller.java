package com.example.classmate.Controller;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public abstract class Controller {

    @FXML
    void menu(MouseEvent event) throws IOException {
        //TODO: Cool animation
        Node source = (Node)event.getSource();
        Scene scene = source.getScene();
        Stage stage = (Stage)scene.getWindow();
        showScene(stage, "ClassMate", "/com/example/classmate/View/main-menu.fxml");
    }

    static void showScene(Stage stage, String title, String path) throws IOException {
        showScene(stage, title, path, stage.getWidth(), stage.getHeight());
    }

    static void showScene(Stage stage, String title, String path, double w, double h) throws IOException {
        //Stage stage = new Stage();
        Parent root = FXMLLoader.load(Controller.class.getResource(path));
        stage.setScene(new Scene(root));
        stage.setTitle(title);
        stage.getIcons().add(new javafx.scene.image.Image("file:" + System.getProperty("user.dir") + "/src/main/resources/com/example/classmate/View/icon.png"));

        stage.setWidth(w);
        stage.setHeight(h);
        stage.show();
    }

    static boolean showAlert(Alert.AlertType at, String title, String header, String content, boolean reqConfirm) {
        Alert alert = new Alert(at);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        if (reqConfirm) {
            ButtonType yesBtn = new ButtonType("Yes");
            ButtonType noBtn = new ButtonType("No");
            alert.getButtonTypes().setAll(yesBtn, noBtn);
            ButtonType bt = alert.showAndWait().orElse(noBtn);
            return bt.equals(yesBtn);
        } else {
            alert.showAndWait();
            return true;
        }
    }

    static String generateAI(String prompt){
        Client client = new Client();

        GenerateContentResponse response =
                client.models.generateContent(
                        "gemini-2.5-flash",
                        "You are an AI assistant named ClassMate designed to help users with their programming issues, particularly in Java. Please avoid unnecessary formatting such as bolding or underlining. How would you respond to the following question?\n" + prompt,
                        null);

        return response.text();
    }

    static void fadeScene(Scene scene, int fromValue, double duration){
        FadeTransition ft = new FadeTransition(Duration.millis(duration), scene.getRoot());
        ft.setFromValue(fromValue);
        ft.setToValue(1 - fromValue);
        ft.play();
    }

    static void fadeScene(Scene scene, int fromValue) {fadeScene(scene, fromValue, 10000);}
}
