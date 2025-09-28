package com.example.classmate.Controller;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class MainController extends Controller{

    @FXML
    private Button aiBuddyBtn;

    @FXML
    private Button mmGenBtn;

    @FXML
    private Button umlGenBtn;

    @FXML
    public void initialize(){
        Platform.runLater(()->{
            aiBuddyBtn.getScene().getWindow().setWidth(480);
            mmGenBtn.getScene().getWindow().setHeight(640);
            ((Stage) umlGenBtn.getScene().getWindow()).setResizable(false);
            ((Stage) umlGenBtn.getScene().getWindow()).setFullScreen(false);
        });
    }

    @FXML
    void loadAiScene(MouseEvent ignore) throws IOException {
        showScene(new Stage(), "ClassMate - AI Buddy", "View/ai-view.fxml");
    }

    @FXML
    void loadUMLScene(MouseEvent ignore) throws IOException {
        showScene(new Stage(), "ClassMate - UML Diagram Generator", "View/uml-view.fxml");
    }

    @FXML
    void btnHover(MouseEvent event) {
        Button sourceBtn = (Button) event.getSource();
        Parent p = sourceBtn.getParent();
        if (p.getChildrenUnmodifiable().size() > 1) {
            for (Node n : p.getChildrenUnmodifiable()) {
                if (n != sourceBtn) {
                    TranslateTransition tt = new TranslateTransition(Duration.millis(100), n);
                    tt.setToY(n.getLayoutY() > sourceBtn.getLayoutY() ? 10f : -10f);
                    tt.play();
                }
            }
        }
        ScaleTransition st = new ScaleTransition(Duration.millis(100), sourceBtn);
        st.setToX(1.25f);
        st.setToY(1.25f);
        st.play();
    }

    @FXML
    void btnUnhover(MouseEvent event) {
        Button sourceBtn = (Button) event.getSource();
        Parent p = sourceBtn.getParent();
        if (p.getChildrenUnmodifiable().size() > 1) {
            for (Node n : p.getChildrenUnmodifiable()) {
                if (n != sourceBtn) {
                    TranslateTransition tt = new TranslateTransition(Duration.millis(100), n);
                    tt.setToY(0f);
                    tt.play();
                }
            }
        }
        ScaleTransition st = new ScaleTransition(Duration.millis(100), sourceBtn);
        st.setToX(1f);
        st.setToY(1f);
        st.play();
    }

    @FXML
    void showMMScene(MouseEvent ignore) {
        showAlert(Alert.AlertType.INFORMATION, "ClassMate - Future Development", "Sorry, Work in Progress!", "Stay tuned for future updates!", false);
    }

    @Override
    String generateAI(String prompt) {
        return "";
    }
}
