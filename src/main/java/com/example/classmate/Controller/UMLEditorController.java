package com.example.classmate.Controller;

import com.example.classmate.Model.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.embed.swing.SwingFXUtils;

import javax.imageio.ImageIO;
import java.io.*;
import java.util.*;

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
    private GridPane gridPane;

    @FXML
    private ToggleButton selectToggle;
    @FXML
    private ToggleButton moveToggle;
    @FXML
    private ToggleButton panToggle;
    @FXML
    private ToggleButton editTextToggle;
    @FXML
    private ToggleButton resizeToggle;
    private final ArrayList<ToggleButton> modeToggles = new ArrayList<>(5);

    EditMode editMode;

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
        contentPane.setPrefSize(100000, 100000);

        int counter = 1;
        if (UMLController.umlClasses != null) {
            for (UMLClass uc : UMLController.umlClasses) {
                if (uc.getName().equals("null")) continue;
                UMLBox ub = new UMLBox(uc);
                contentPane.getChildren().add(ub);
                ub.setTranslateX(50 * counter);
                ub.setTranslateY(50 * counter);
                ub.setEditable(false);
                counter++;
            }
        }

        Collections.addAll(modeToggles, selectToggle, moveToggle, panToggle,  editTextToggle, resizeToggle);
        umlBoxLbl.addEventHandler(MouseEvent.MOUSE_RELEASED, mouseEvent -> addElement(mouseEvent, new UMLBox()));
        editMode = EditMode.SELECT;
        selectToggle.setSelected(true);
        setEditMode(editMode);
    }

    @FXML
    public void save(MouseEvent mouseEvent) {
        Bounds bounds = getContentBounds(contentPane);

        SnapshotParameters params = new SnapshotParameters();
        params.setViewport(new Rectangle2D(
                bounds.getMinX()-10.0,
                bounds.getMinY()-10.0,
                bounds.getWidth()+10.0,
                bounds.getHeight()+10.0
        ));
        WritableImage image = contentPane.snapshot(params, null);
        try {
            //TODO: Save to user selected directory.
            File outputFile = new File("scrollPane_content.png");
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", outputFile);
            System.out.println("Image saved successfully!");
        } catch (Exception e) {
            System.err.println("Error saving image: " + e.getMessage());
        }
    }

    @FXML
    private void modeToggle(MouseEvent mouseEvent){
        for (ToggleButton btn : modeToggles) {
            btn.setSelected(false);
        }
        ToggleButton selectedToggle = (ToggleButton) mouseEvent.getSource();
        selectedToggle.setSelected(true);
        editMode = EditMode.getEditModes()[modeToggles.indexOf(selectedToggle)];
        setEditMode(editMode);
    }

    @FXML
    private void checkKeyPress(KeyEvent keyEvent){
        ToggleButton selectedToggle;
        switch(keyEvent.getCode()){
            case DIGIT1:
                selectedToggle = selectToggle;
                editMode = EditMode.SELECT; break;
            case DIGIT2:
                selectedToggle = moveToggle;
                editMode = EditMode.MOVE; break;
            case DIGIT3:
                selectedToggle = panToggle;
                editMode = EditMode.PAN; break;
            case DIGIT4:
                selectedToggle = editTextToggle;
                editMode = EditMode.EDIT_TEXT; break;
            case DIGIT5:
                selectedToggle = resizeToggle;
                editMode = EditMode.RESIZE; break;
            default: return;
        }
        for (ToggleButton btn : modeToggles) {
            btn.setSelected(btn == selectedToggle);
        }
        setEditMode(editMode);
    }

    private void addElement(MouseEvent mouseEvent, Node node) {
        //Ok for now this thing will be here, but you should most definitely move it elsewhere once ready
        Bounds paneBounds = scrollPane.localToScene(scrollPane.getBoundsInLocal());
        if (!paneBounds.contains(mouseEvent.getSceneX(), mouseEvent.getSceneY())) return;

        if (node instanceof UMLBox ub){
            contentPane.getChildren().add(ub);
            Point2D point = contentPane.sceneToLocal(mouseEvent.getSceneX(), mouseEvent.getSceneY());
            ub.setTranslateX(point.getX() - ub.getWidth());
            ub.setTranslateY(point.getY() - ub.getHeight());
        }
        setEditMode(editMode);
    }

    private Bounds getContentBounds(Pane contentPane) {
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double maxY = Double.MIN_VALUE;

        for (Node node : contentPane.getChildren()) {
            Bounds b = node.getBoundsInParent();
            minX = Math.min(minX, b.getMinX());
            minY = Math.min(minY, b.getMinY());
            maxX = Math.max(maxX, b.getMaxX());
            maxY = Math.max(maxY, b.getMaxY());
        }

        return new BoundingBox(minX, minY, maxX - minX, maxY - minY);
    }

    private void setEditMode(EditMode editMode){
        switch(editMode){
            case SELECT:
                scrollPane.setCursor(Cursor.DEFAULT);
                modeChanger(false, false, false); break;
                //TODO: Code for being able to edit color and whatnot
            case MOVE:
                scrollPane.setCursor(Cursor.MOVE);
                modeChanger(false, true, false); break;
            case PAN:
                scrollPane.setCursor(Cursor.OPEN_HAND);
                modeChanger(true, false, false); break;
            case EDIT_TEXT:
                scrollPane.setCursor(Cursor.TEXT);
                modeChanger(true, false, true); break;
            case RESIZE:
                scrollPane.setCursor(Cursor.SE_RESIZE);
                modeChanger(false, false, false); //TODO: WORK ON THIS
            default: break;
        }
    }
    private void modeChanger(boolean pannable, boolean draggable, boolean editable){
        scrollPane.setPannable(pannable);
        List<Node> nodes = contentPane.getChildren();
        for (Node node : nodes) {
            if (node instanceof UMLBox umlBox){
                if (draggable) new DraggableMaker().makeDraggable(umlBox);
                else DraggableMaker.reset(umlBox);
                umlBox.setEditable(editable);
            }
        }
    }
}
