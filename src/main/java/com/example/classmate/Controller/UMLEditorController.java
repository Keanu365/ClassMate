package com.example.classmate.Controller;

import com.example.classmate.Model.*;
import javafx.collections.ObservableList;
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
    private static final double spacing = 250;

    @FXML
    public void initialize(){
        scrollPane.widthProperty().addListener((_, _, _) -> scrollPane.setPrefWidth(((Pane) scrollPane.getParent()).getWidth() - 150));
        DraggableMaker dm = new DraggableMaker();
        dm.makeDraggable(umlBoxLbl, true);
        contentPane.setPrefSize(10000, 10000);

        if (UMLController.umlClasses != null) {
            ArrayList<UMLClass> umlClasses = new ArrayList<>(List.of(UMLController.umlClasses));
            double currentTranslateX = 4000 - spacing;
            double currentTranslateY = 5000;
            ObservableList<Node> children = contentPane.getChildren();
            for (UMLClass uc : umlClasses) {
                if (uc.getName().equals("null")) continue;
                currentTranslateX += spacing;
                UMLBox ub = new UMLBox(uc);
                for (Node node : children){
                    if (node instanceof UMLBox otherUb){
                        if (ub.equals(otherUb)){
                            ub = otherUb;
                            break;
                        }
                    }
                }
                if (!children.contains(ub)) {
                    children.add(ub);
                    ub.setTranslateX(currentTranslateX);
                    ub.setTranslateY(currentTranslateY);
                }
                Class<?> parentClass = uc.getSuperclass();
                Class<?>[] implementedInterfaces = uc.getInterfaces();
                //All code in for loop after this point deals with parent classes/interfaces
                UMLClass parent_uc = new UMLClass(parentClass);
                boolean parentPresent = false;
                for (UMLClass umlClass : umlClasses) {
                    try {
                        if (parent_uc.equals(umlClass)) {parentPresent = true; break;}
                    } catch (Exception e) {
                        break;
                    }
                }
                if (parentPresent) {
                    UMLBox parent_ub = new UMLBox(parent_uc);
                    addParentUMLBox(parent_ub, ub, currentTranslateX);
                }
                for (Class<?> implementedInterface : implementedInterfaces) {
                    int count = 0;
                    for (UMLClass umlClass : umlClasses) {
                        if (implementedInterface.isAssignableFrom(umlClass.getUMLClass())) {
                            UMLClass interfaceClass = new UMLClass(implementedInterface);
                            addParentUMLBox(new UMLBox(interfaceClass), ub, currentTranslateX + spacing * count);
                            count++;
                        }
                    }
                }
            }
        }
        Collections.addAll(modeToggles, selectToggle, moveToggle, panToggle,  editTextToggle, resizeToggle);
        umlBoxLbl.addEventHandler(MouseEvent.MOUSE_RELEASED, mouseEvent -> addElement(mouseEvent, new UMLBox()));
        editMode = EditMode.SELECT;
        selectToggle.setSelected(true);
        setEditMode(editMode);
    }

    private void addParentUMLBox(UMLBox parent, UMLBox child, double translateX){
        ObservableList<Node> children = contentPane.getChildren();
        for (Node node : children){
            if (node instanceof UMLBox otherUb){
                if (parent.equals(otherUb)){
                    parent = otherUb;
                    break;
                }
            }
        }
        if (!children.contains(parent)) {
            children.add(parent);
        }
        parent.setTranslateX(translateX - spacing);
        parent.setTranslateY(child.getTranslateY() - parent.calcHeight() - spacing);
        children.add(new PolyArrow(child, parent));
    }

    @FXML
    public void save() {
        Bounds bounds = getContentBounds(contentPane);

        SnapshotParameters params = new SnapshotParameters();
        params.setViewport(new Rectangle2D(
                bounds.getMinX()-10.0,
                bounds.getMinY()-10.0,
                bounds.getWidth()+20.0,
                bounds.getHeight()+20.0
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
    private void drawArrow() {
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
                modeChanger(true, false, true, false, false); break;
            case RESIZE:
                scrollPane.setCursor(Cursor.DEFAULT);
                modeChanger(false, false, false, true, true);
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
            node.setCursor(scrollPane.getCursor());
            if (node instanceof UMLBox umlBox){
                umlBox.setResizable(resizable); //Must do this before draggable
                if (draggable) new DraggableMaker().makeDraggable(umlBox);
                umlBox.setEditable(editable);
                umlBox.setSelectable(selectable);
            }
            else if (node instanceof PolyArrow arrow){
                arrow.setSelectable(selectable);
            }
        }
    }

    public void showProperties(Node node, boolean showBox1, boolean showBox2){
        //showBox1 will practically always be true anyway.
        String regex = "^[1-9]\\d*(\\.\\d+)?$";
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
            borderColorPicker.setOnAction(_ -> {
                Color newColor = borderColorPicker.getValue();
                String css = String.format("rgb(%d,%d,%d);",
                        (int)(newColor.getRed() * 255),
                        (int)(newColor.getGreen() * 255),
                        (int)(newColor.getBlue() * 255));
                umlBox.setStyle(umlBox.getStyle() + "; -fx-border-color: " + css);
            });
            borderWidthField.setOnAction(_ -> {
                try {
                    if (borderWidthField.getText().matches(regex)) throw new IllegalArgumentException();
                    double borderWidth = Double.parseDouble(borderWidthField.getText());
                    umlBox.setStyle(umlBox.getStyle() + "; -fx-border-width: " + borderWidth + "px");
//                    umlBox.updatePrefHeight(borderWidth);
                } catch (IllegalArgumentException e) {
                    showAlert(Alert.AlertType.ERROR, "ClassMate - Input Error", "Error in input!", "Please only enter a number greater than 0!", false);
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "ClassMate - Error", "Unexpected Error", "An unexpected error occurred. Please try again later.", false);
                }
            });
            fontColorPicker.setOnAction(_ -> {
                Color newColor = fontColorPicker.getValue();
                umlBox.setFontColor(newColor);
            });
            fontSizeField.setOnAction(_ -> {
                try {
                    if (borderWidthField.getText().matches(regex)) throw new IllegalArgumentException();
                    double size = Double.parseDouble(fontSizeField.getText());
                    for (Node n: umlBox.getChildren()){
                        InlineCssTextArea ta = (InlineCssTextArea) n;
                        ta.setStyle(ta.getStyle() + "; -fx-font-size: " + size + "px");
//                        umlBox.updatePrefHeight();
                    }
                } catch (IllegalArgumentException e) {
                    showAlert(Alert.AlertType.ERROR, "ClassMate - Input Error", "Error in input!", "Please only enter a number greater than 0!", false);
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "ClassMate - Error", "Unexpected Error", "An unexpected error occurred. Please try again later.", false);
                }
            });
        }else if (node instanceof PolyArrow arrow){
            borderColorPicker.setValue(arrow.getStrokeColor());
            borderWidthField.setText(arrow.getStrokeWidth() + "");
            borderColorPicker.setOnAction(_ -> arrow.setStrokeColor(borderColorPicker.getValue()));
            borderWidthField.setOnAction(_ -> {
                try {
                    if (borderWidthField.getText().matches(regex)) throw new IllegalArgumentException();
                    arrow.setStrokeWidth(Double.parseDouble(borderWidthField.getText()));
                } catch (IllegalArgumentException e) {
                    showAlert(Alert.AlertType.ERROR, "ClassMate - Input Error", "Error in input!", "Please only enter a number greater than 0!", false);
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "ClassMate - Error", "Unexpected Error", "An unexpected error occurred. Please try again later.", false);
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
            if (node instanceof PolyArrow arrow) arrow.detach();
        }
    }
}
