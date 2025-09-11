package com.example.classmate.Controller;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
    void loadAiScene(MouseEvent event) throws IOException {
        Node source = (Node)event.getSource();
        Scene scene = source.getScene();
        scene.getStylesheets().add(getClass().getResource("/com/example/classmate/View/styles.css").toExternalForm());
        Stage stage = (Stage)scene.getWindow();
        fadeScene(scene, 1);
        showScene(stage, "ClassMate - AI Buddy", "/com/example/classmate/View/ai-view.fxml");
    }

    @FXML
    void loadUMLScene(MouseEvent event) throws IOException {
        Node source = (Node)event.getSource();
        Scene scene = source.getScene();
        scene.getStylesheets().add(getClass().getResource("/com/example/classmate/View/styles.css").toExternalForm());
        Stage stage = (Stage)scene.getWindow();
        fadeScene(scene, 1);
        showScene(stage, "ClassMate - UML Diagram Generator", "/com/example/classmate/View/uml-view.fxml");
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
                    //tt.setInterpolator(Interpolator.EASE_OUT);
                    tt.play();
                }
            }
        }
        ScaleTransition st = new ScaleTransition(Duration.millis(100), sourceBtn);
        st.setToX(1.25f);
        st.setToY(1.25f);
        //st.setInterpolator(Interpolator.EASE_OUT);
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
                    //tt.setInterpolator(Interpolator.EASE_OUT);
                    tt.play();
                }
            }
        }
        ScaleTransition st = new ScaleTransition(Duration.millis(100), sourceBtn);
        st.setToX(1f);
        st.setToY(1f);
        //st.setInterpolator(Interpolator.EASE_OUT);
        st.play();
    }

}
