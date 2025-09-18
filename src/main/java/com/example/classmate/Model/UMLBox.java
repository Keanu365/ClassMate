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

    public UMLBox() {
        this("Name", "Fields", "Methods");
    }
    public UMLBox(UMLClass uc){
        this(uc.getName(), uc.getFields(), uc.getMethods());
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
                String[] lines = text.split("\\n");
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
                this.updatePrefHeight();
                textArea.borderProperty().bind(this.borderProperty());
                textArea.prefWidthProperty().bindBidirectional(this.prefWidthProperty());
                textArea.setWrapText(true);
                this.getChildren().add(textArea);
                textArea.textProperty().addListener((obs, oldText, newText) -> {
                    this.updatePrefHeight();
                });
            }
            new DraggableMaker().makeDraggable(this);
    }

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
            node.setOnMouseMoved(event -> handleMouseMoved(node, event));
        }
    }

    private void handleMouseMoved(Node node, MouseEvent event) {
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

    public static double getFontSize(InlineCssTextArea textArea){
        int start = textArea.getStyle().lastIndexOf("-fx-font-size:") + 14;
        int end = textArea.getStyle().indexOf("p", start); //We will always be using px
        return Double.parseDouble(textArea.getStyle().substring(start, end).replace(" ",""));
    }
    public double getBorderWidth(){
        return this.getBorder().getStrokes().getFirst().getWidths().getTop();
    }

    public void updatePrefHeight(){this.updatePrefHeight(this.getBorderWidth());}
    public void updatePrefHeight(double borderWidth){ //Required to accommodate for UMLEditorController:274
        for (Node n : this.getChildren()){
            InlineCssTextArea textArea = (InlineCssTextArea) n;
            int lines = textArea.getText().split("\\n").length;
            textArea.setPrefHeight(Math.max(1, lines) * getFontSize(textArea) * 2.5 + 2 * borderWidth);
        }
    }

    public Point2D getTopMiddlePoint(){
        Bounds bounds = this.getBoundsInLocal();
        return this.getParent().sceneToLocal(
                this.localToScene(bounds.getMinX() + bounds.getWidth() / 2, bounds.getMinY())
        );
    }
    public Point2D getBottomMiddlePoint(){
        Bounds bounds = this.getBoundsInLocal();
        return this.getParent().sceneToLocal(
                this.localToScene(bounds.getMinX() + bounds.getWidth() / 2, bounds.getMaxY())
        );
    }
    public Point2D getMiddlePoint(){
        return new Point2D(
                this.getTopMiddlePoint().getX(),
                (this.getTopMiddlePoint().getY() + this.getBottomMiddlePoint().getY()) / 2.0
        );
    }
}
