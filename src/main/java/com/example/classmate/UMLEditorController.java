package com.example.classmate;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.embed.swing.SwingFXUtils;
import javax.imageio.ImageIO;
import java.io.*;

public class UMLEditorController extends Controller{

    @FXML
    private Label arrowLbl;

    @FXML
    private Label backBtn;

    @FXML
    private Label umlBoxLbl;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private Button saveBtn;

    @FXML
    private Pane contentPane;

    @FXML
    public void initialize(){
        scrollPane.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                //System.out.println("Scene Width changed from " + oldValue + " to " + newValue);
                scrollPane.setPrefWidth(((Pane) scrollPane.getParent()).getWidth() - 150);
            }
        });
        DraggableMaker dm = new DraggableMaker();
        dm.makeDraggable(umlBoxLbl, true);
        dm.makeDraggable(arrowLbl, true);

        umlBoxLbl.addEventHandler(MouseEvent.MOUSE_RELEASED, mouseEvent -> addElement(mouseEvent, new UMLBox()));
    }

    @FXML
    public void save(MouseEvent mouseEvent) {
        WritableImage image = scrollPane.getContent().snapshot(new SnapshotParameters(), null);
        try {
            //TODO: Save to user selected directory.
            File outputFile = new File("scrollPane_content.png");
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", outputFile);
            System.out.println("Image saved successfully!");
        } catch (IOException e) {
            System.err.println("Error saving image: " + e.getMessage());
        }
    }

    private void addElement(MouseEvent mouseEvent, Node node) {
        //Ok for now this thing will be here, but you should most definitely move it elsewhere once ready
        Bounds paneBounds = scrollPane.localToScene(scrollPane.getBoundsInLocal());
        if (!paneBounds.contains(mouseEvent.getSceneX(), mouseEvent.getSceneY())) return;

        new DraggableMaker().makeDraggable(node);
        if (node instanceof UMLBox ub){
            contentPane.getChildren().add(ub);
            Point2D point = contentPane.sceneToLocal(mouseEvent.getSceneX(), mouseEvent.getSceneY());
            ub.setTranslateX(point.getX() - ub.getWidth());
            ub.setTranslateY(point.getY() - ub.getHeight());
        }

    }
}
