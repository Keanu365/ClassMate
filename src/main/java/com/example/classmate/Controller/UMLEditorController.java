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
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import org.fxmisc.richtext.InlineCssTextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.paint.Color;

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
    private Button delBtn;

    @FXML
    private StackPane stackPane;

    @FXML
    private ColorPicker borderColorPicker;
    @FXML
    private TextField borderWidthField;
    @FXML
    private ColorPicker fontColorPicker;
    @FXML
    private TextField fontSizeField;
    @FXML
    private VBox propBox1;
    @FXML
    private VBox propBox2;
    @FXML
    private Label propertiesLbl;

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
        //dm.makeDraggable(arrowLbl, true);
        contentPane.setPrefSize(10000, 10000);

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
        //arrowLbl.addEventHandler(MouseEvent.MOUSE_RELEASED, mouseEvent -> addElement(mouseEvent, new PolyArrow(mouseEvent.getX(), mouseEvent.getY())));
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
    private void drawArrow(MouseEvent mouseEvent) {
        for (Node node: stackPane.getChildren()) {
            if (node != scrollPane) node.setDisable(true);
        }
        modeChanger(false, false, false, false, false);
        gridPane.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                for (Node node2 : stackPane.getChildren()) {
                    node2.setDisable(false);
                    setEditMode(editMode);
                }
                gridPane.setOnKeyPressed(this::checkKeyPress);
            }
        });
        for (Node node : contentPane.getChildren()) {
            //Recursion to get "from" and "to", then draw a PolyArrow.
            if (node instanceof UMLBox ub) {
                ub.setOnMouseClicked(e -> {
                    UMLBox from = (UMLBox) e.getSource();
                    for (Node node1 : contentPane.getChildren()) {
                        if (node1 instanceof UMLBox ub1) {
                            ub1.setOnMouseClicked(e1 -> {
                                UMLBox to = (UMLBox) e1.getSource();
                                try {
                                    if (from == to) throw new Exception("Arrow cannot be drawn to and from the same box!");
                                    PolyArrow arrow = new PolyArrow(from, to);
                                    ArrayList<PolyArrow> others = new ArrayList<>();
                                    for (Node node2 : contentPane.getChildren()) {
                                        if (node2 instanceof PolyArrow other) {
                                            if (arrow.equals(other)) throw new Exception("This arrow has already been drawn!");
                                            others.add(other);
                                        }
                                    }
                                    if (arrow.checkCyclic(others.toArray(new PolyArrow[0]))){
                                        boolean draw = showAlert(Alert.AlertType.WARNING, "Arrow Drawing - Warning", "Cyclic Relationship Warning", "Warning: Drawing this arrow will result in a cyclic relationship! Do you wish to proceed?", true);
                                        if (draw) contentPane.getChildren().add(arrow);
                                    }else contentPane.getChildren().add(arrow);
                                } catch (Exception ex) {
                                    showAlert(Alert.AlertType.ERROR, "Arrow Drawing - Error", "Arrow Drawing Operation Cancelled", ex.getMessage(), false);
                                } finally {
                                    for (Node node2 : stackPane.getChildren()) {
                                        node2.setDisable(false);
                                        setEditMode(editMode);
                                    }
                                    gridPane.setOnKeyPressed(this::checkKeyPress);
                                }
                            });
                        }
                    }
                });
            }
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
            case ESCAPE:
                propertiesLbl.setVisible(editMode ==  EditMode.SELECT);
                propBox1.setVisible(false);
                propBox2.setVisible(false);
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
        Point2D point = contentPane.sceneToLocal(mouseEvent.getSceneX(), mouseEvent.getSceneY());
        if (node instanceof UMLBox ub){
            contentPane.getChildren().add(ub);
            ub.setTranslateX(point.getX() - ub.getWidth());
            ub.setTranslateY(point.getY() - ub.getHeight());
        }else if (node instanceof PolyArrow arrow){
            contentPane.getChildren().add(arrow);
            Point2D end = new Point2D(point.getX() + 50, point.getY());
            arrow.updateArrow();
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
                modeChanger(false, false, false, true, false); break;
            case MOVE:
                scrollPane.setCursor(Cursor.MOVE);
                modeChanger(false, true, false, false, false); break;
            case PAN:
                scrollPane.setCursor(Cursor.OPEN_HAND);
                modeChanger(true, false, false, false, false); break;
            case EDIT_TEXT:
                scrollPane.setCursor(Cursor.TEXT);
                modeChanger(true, false, true, false, true); break;
            case RESIZE:
                scrollPane.setCursor(Cursor.SE_RESIZE);
                modeChanger(false, false, false, false, true);
            default: break;
        }
    }
    private void modeChanger(boolean pannable, boolean draggable, boolean editable, boolean selectable, boolean resizable){
        scrollPane.setPannable(pannable);
        propertiesLbl.setVisible(selectable);
        gridPane.setOnKeyPressed(this::checkKeyPress);
        propBox1.setVisible(false);
        propBox2.setVisible(false);
        List<Node> nodes = contentPane.getChildren();
        for (Node node : nodes) {
            if (node instanceof UMLBox umlBox){
                if (draggable) new DraggableMaker().makeDraggable(umlBox);
                else DraggableMaker.reset(umlBox);
                umlBox.setEditable(editable);
                umlBox.setSelectable(selectable);
                umlBox.setResizable(resizable);
            }
            else if (node instanceof PolyArrow arrow){
                arrow.setSelectable(selectable);
            }
        }
    }

    public void showProperties(Node node, boolean showBox1, boolean showBox2){
        //showBox1 will practically always be true anyway.
        if (!(showBox1 || showBox2)) return;
        propBox1.setVisible(showBox1);
        propBox2.setVisible(showBox2);
        if (node instanceof UMLBox umlBox){
            BorderStroke strokes = umlBox.getBorder().getStrokes().getFirst();
            borderColorPicker.setValue((Color) strokes.getTopStroke());
            borderWidthField.setText(strokes.getWidths().getTop() * 1 + "");
            InlineCssTextArea textArea = (InlineCssTextArea) umlBox.getChildrenUnmodifiable().getFirst();
            fontColorPicker.setValue(umlBox.getFontColor());
            //Find font size
            int start = textArea.getStyle().lastIndexOf("-fx-font-size:") + 14;
            int end = textArea.getStyle().indexOf("p", start); //We will always be using px
            fontSizeField.setText(Double.parseDouble(textArea.getStyle().substring(start, end).replace(" ","")) + "");
            borderColorPicker.setOnAction(e -> {
                Color newColor = borderColorPicker.getValue();
                String css = String.format("rgb(%d,%d,%d);",
                        (int)(newColor.getRed() * 255),
                        (int)(newColor.getGreen() * 255),
                        (int)(newColor.getBlue() * 255));
                umlBox.setStyle(umlBox.getStyle() + "; -fx-border-color: " + css);
            });
            borderWidthField.setOnAction(e -> {
                try {
                    double borderWidth = Double.parseDouble(borderWidthField.getText());
                    umlBox.setStyle(umlBox.getStyle() + "; -fx-border-width: " + borderWidth + "px");
                    umlBox.updatePrefHeight(borderWidth);
                } catch (NumberFormatException ex) {
                    showAlert(Alert.AlertType.ERROR, "ClassMate - Input Error", "Error in input!", "Please enter a valid integer/decimal only!", false);
                }
            });
            fontColorPicker.setOnAction(e -> {
                Color newColor = fontColorPicker.getValue();
                umlBox.setFontColor(newColor);
            });
            fontSizeField.setOnAction(e -> {
                try {
                    double size = Double.parseDouble(fontSizeField.getText());
                    for (Node n: umlBox.getChildren()){
                        InlineCssTextArea ta = (InlineCssTextArea) n;
                        ta.setStyle(ta.getStyle() + "; -fx-font-size: " + size + "px");
                        umlBox.updatePrefHeight();
                    }
                } catch (NumberFormatException ex) {
                    showAlert(Alert.AlertType.ERROR, "ClassMate - Input Error", "Error in input!", "Please enter a valid integer/decimal only!", false);
                }
            });
        }else if (node instanceof PolyArrow arrow){
            borderColorPicker.setValue(arrow.getStrokeColor());
            borderWidthField.setText(arrow.getStrokeWidth() + "");
            borderColorPicker.setOnAction(_ -> arrow.setStrokeColor(borderColorPicker.getValue()));
            borderWidthField.setOnAction(_ -> {
                try {
                    arrow.setStrokeWidth(Double.parseDouble(borderWidthField.getText()));
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "ClassMate - Input Error", "Error in input!", "Please enter a valid integer/decimal only!", false);
                }
            });
        }
        delBtn.setOnMouseClicked(_ -> delete(node));
        gridPane.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.DELETE ||  keyEvent.getCode() == KeyCode.BACK_SPACE) {
                delete(node);
            }
        });
        gridPane.addEventHandler(KeyEvent.KEY_PRESSED, this::checkKeyPress);
    }

    private void delete(Node node){
        boolean confirmed = showAlert(Alert.AlertType.CONFIRMATION, "Node Deletion - Confirmation", "Confirm Delete", "Are you sure you want to delete this node?", true);
        if (confirmed){
            ((Pane) node.getParent()).getChildren().remove(node);
            propBox1.setVisible(false);
            propBox2.setVisible(false);
            gridPane.setOnKeyPressed(this::checkKeyPress);
        }
    }
}
