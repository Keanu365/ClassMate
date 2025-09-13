package com.example.classmate.Model;

import com.example.classmate.Controller.UMLEditorController;
import javafx.scene.Node;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.fxmisc.richtext.InlineCssTextArea;

import java.util.Collections;
import java.util.List;

public class UMLBox extends VBox implements Selectable{
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
            while (textArea.getText().contains("(static)")) {
                int start = textArea.getText().indexOf("(static)");
                textArea.replaceText(textArea.getText().replaceFirst("\\(static\\)", ""));
                int end = textArea.getText().indexOf("\n", start);
                textArea.setStyle(start, end, "-fx-underline: true;");
            }
            textArea.setPrefHeight(Math.max(1, lines.length) * getFontSize(textArea) * 2.5);// + 2 * this.getBorderWidth());
            textArea.borderProperty().bind(this.borderProperty());
            textArea.prefWidthProperty().bind(this.prefWidthProperty());
            textArea.setWrapText(true);
            this.getChildren().add(textArea);
            textArea.textProperty().addListener((obs, oldText, newText) -> {
                int newLines = newText.split("\\n").length;
                textArea.setPrefHeight(Math.max(1, newLines) * getFontSize(textArea) * 2.5);// + 2 * this.getBorderWidth());
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
    public boolean isEditable(){return ((InlineCssTextArea) this.getChildren().getFirst()).isEditable();}

    public void setSelectable(boolean selectable){
        if (!selectable) this.setOnMouseClicked(_ -> {});
        else {
            this.setOnMouseClicked(_ -> select());
        }
    }

    public void select(){
        UMLEditorController controller = (UMLEditorController) this.getScene().getUserData();
        controller.showProperties(this, true, true);
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
}
