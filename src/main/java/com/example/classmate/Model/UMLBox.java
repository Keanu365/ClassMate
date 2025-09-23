package com.example.classmate.Model;

import com.example.classmate.Controller.UMLEditorController;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.fxmisc.richtext.InlineCssTextArea;

import java.util.ArrayList;
import java.util.List;

public class UMLBox extends VBox implements Selectable, Resizable {
    public Color fontColor = Color.BLACK;
    private boolean isInterface = false;

    public UMLBox() {
        this("Name\n", "Fields\n", "Methods\n");
    }
    public UMLBox(UMLClass uc){
        this(uc.getName(), uc.getFields(), uc.getMethods());
        isInterface = uc.getUMLClass().isInterface();
    }
    public UMLBox(String name, String fields, String methods) {
        super();
        //Border stuff :3
        BorderStroke borderStroke = new BorderStroke(
                Color.BLACK,                      // stroke color
                BorderStrokeStyle.SOLID,          // stroke style
                null,                             // rounded corners
                new BorderWidths(1)            // thickness (2px)
        );
        this.setBorder(new Border(borderStroke));
        this.setStyle("-fx-pref-width: 200px; -fx-border-color: black");
        //Text stuff :3
        String[] texts = new String[]{name, fields, methods};
            for (String text : texts) {
                InlineCssTextArea textArea = new InlineCssTextArea();
                textArea.replaceText(text);
                textArea.getStyleClass().add("uml_label");
                textArea.setStyle("-fx-font-size: 12px;");
                List<int[]> list = new ArrayList<>();
                while (textArea.getText().contains("(static)")) {
                    int start = textArea.getText().indexOf("(static)");
                    textArea.replaceText(textArea.getText().replaceFirst("\\(static\\)", ""));
                    int end = textArea.getText().indexOf("\n", start);
                    list.add(new int[]{start,end});
                }
                for (int[] i : list) {
                    textArea.setStyle(i[0], i[1], "-fx-underline: true;");
                }
//                this.updatePrefHeight();
                textArea.borderProperty().bind(this.borderProperty());
                textArea.prefWidthProperty().bindBidirectional(this.prefWidthProperty());
                textArea.setWrapText(true);
//                textArea.prefHeightProperty().bind(textArea.totalHeightEstimateProperty());
                textArea.prefHeightProperty().bind(textArea.totalHeightEstimateProperty());
                textArea.textProperty().addListener((_, _, _) -> {
                    if (!textArea.getText().endsWith("\n")) textArea.replaceText(textArea.getText() + "\n");
                });
                textArea.showParagraphAtTop(0);
                this.getChildren().add(textArea);
            }
            new DraggableMaker().makeDraggable(this);
    }

    public boolean isInterface(){return this.isInterface;}

    public void setEditable(boolean editable){
        for (Node n : this.getChildren()){
            InlineCssTextArea textArea = (InlineCssTextArea) n;
            textArea.setEditable(editable);
            textArea.setMouseTransparent(!editable);
        }
    }

    public void setSelectable(boolean selectable){
        if (!selectable) this.setOnMouseClicked(_ -> {});
        else {
            this.setOnMouseClicked(_ -> select());
        }
    }

    public void setResizable(boolean resizable){
        if (!resizable) {
            for (Node node : this.getChildren()){
                node.setOnMouseMoved(_ -> {});
            }
        }
        else {
            resize();
        }
    }

    public void select(){
        UMLEditorController controller = (UMLEditorController) this.getScene().getUserData();
        controller.showProperties(this, true, true);
    }

    public void resize(){
        //TODO: WORK ON THIS
        for (Node node : this.getChildren()){
            //InlineCssTextArea textArea = (InlineCssTextArea) node;
            node.setOnMouseMoved(this::handleMouseMoved);
        }
    }

    private void handleMouseMoved(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();
        double w = this.getWidth();
        double h = this.getHeight();

        double margin = 5; // px threshold for borders

        if (x < margin || x > w - margin) {
            System.out.println("ew border");
        }else if (y < margin || y > h - margin){
            System.out.println("ns border");
        }
        else {
            System.out.println("no border");
        }
    }

    public Color getFontColor(){return this.fontColor;}
    public void setFontColor(Color newColor){
        this.fontColor = newColor;
        String css = String.format("-fx-fill: rgb(%d,%d,%d);",
                (int)(newColor.getRed() * 255),
                (int)(newColor.getGreen() * 255),
                (int)(newColor.getBlue() * 255));
        for (Node node : this.getChildren()){
            InlineCssTextArea textArea = (InlineCssTextArea) node;
            textArea.setStyle(0, textArea.getText().length(), css);
        }
    }

    public enum Midpoint {
        TOP,
        BOTTOM,
        LEFT,
        RIGHT,
        CENTRE
    }
    public Point2D getPoint(Midpoint point){
        Bounds bounds = this.getBoundsInLocal();
        return switch (point) {
            case TOP -> this.getParent().sceneToLocal(
                    this.localToScene(bounds.getMinX() + bounds.getWidth() / 2, bounds.getMinY())
            );
            case BOTTOM -> this.getParent().sceneToLocal(
                    this.localToScene(bounds.getMinX() + bounds.getWidth() / 2, bounds.getMaxY())
            );
            case LEFT -> this.getParent().sceneToLocal(
                    this.localToScene(bounds.getMinX(), bounds.getMinY() + bounds.getHeight() / 2)
            );
            case RIGHT -> this.getParent().sceneToLocal(
                    this.localToScene(bounds.getMaxX(), bounds.getMinY() + bounds.getHeight() / 2)
            );
            case CENTRE -> this.getParent().sceneToLocal(
                    this.localToScene(bounds.getMinX() + bounds.getWidth() / 2, bounds.getMinY() + bounds.getHeight() / 2)
            );
        };
    }

    public double calcHeight(){
        double height = 0;
        for (Node node : this.getChildren()){
            InlineCssTextArea textArea = (InlineCssTextArea) node;
            height += textArea.getPrefHeight();
        }
        return height;
    }

    @Override
    public String toString(){
        String retStr = "";
        for (Node node : this.getChildren()){
            InlineCssTextArea textArea = (InlineCssTextArea) node;
            retStr += textArea.getText();
            retStr += "\n\n";
        }
        return retStr;
    }

    @Override
    public boolean equals(Object obj){
        if (obj instanceof UMLBox other) return this.toString().equals(other.toString());
        else return false;
    }
}
