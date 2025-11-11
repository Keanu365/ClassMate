package com.example.classmate.Model;

import com.example.classmate.Controller.UMLEditorController;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.fxmisc.richtext.InlineCssTextArea;


import java.util.ArrayList;
import java.util.List;

public class UMLBox extends VBox implements Selectable, Resizable, Formattable {


    private static List<InlineCssTextArea> allAreas = new ArrayList<>();

    public Color fontColor = Color.BLACK;
    public double fontSize = 12;
    public Color borderColor = Color.BLACK;
    public double borderWidth = 1;
    public PolyArrow arrow = null;
    private boolean isInterface = false;

    private static int idCounter = 0;
    private int id;
    public int getID() {return id;}

    public UMLBox() {
        this("Name\n", "Fields\n", "Methods\n", idCounter++);
    }

    public UMLBox(UMLClass uc){
        this(uc.getName(), uc.getFields(), uc.getMethods(), idCounter++);
        isInterface = uc.getUMLClass().isInterface();
    }
    public UMLBox(String name, String fields, String methods, int id) {
        super();
        this.id = id;
        //Border stuff :3
        BorderStroke borderStroke = new BorderStroke(
                Color.BLACK,                      // stroke color
                BorderStrokeStyle.SOLID,          // stroke style
                null,                             // rounded corners
                new BorderWidths(1)            // thickness (2px)
        );
        this.setBorder(new Border(borderStroke));
        this.setPrefWidth(200);
        //Text stuff :3
        String[] texts = new String[]{name, fields, methods};
        for (String text : texts) {
            InlineCssTextArea textArea = new InlineCssTextArea();
            allAreas.add(textArea);
            textArea.replaceText(text);
            textArea.getStyleClass().add("uml_label");
            textArea.setStyle("-fx-font-size: 12px;");
            textArea.borderProperty().bind(this.borderProperty());
            textArea.prefWidthProperty().bind(this.prefWidthProperty());
            textArea.setWrapText(true);
            textArea.prefHeightProperty().bind(textArea.totalHeightEstimateProperty());
            textArea.textProperty().addListener((_, _, _) -> {
                if (!textArea.getText().endsWith("\n")) textArea.replaceText(textArea.getText() + "\n");
                if (arrow != null) arrow.updateArrow();
            });
            textArea.showParagraphAtTop(0);
            if (this.getChildren().isEmpty()) {//i.e. title text area
                textArea.textProperty().addListener((_, _, _) ->
                        this.isInterface = textArea.getText().startsWith("<<interface>>"));
            } else textArea.setOnKeyPressed(keyEvent -> {
                if (keyEvent.getCode() == KeyCode.ENTER) {
                    this.format();
                }
            });
            this.getChildren().add(textArea);
            Platform.runLater(this::format);
        }
        new DraggableMaker().makeDraggable(this);
        DoubleProperty[] properties = new DoubleProperty[3];
        for (int i = 0; i < 3; i++){
            InlineCssTextArea textArea = (InlineCssTextArea) this.getChildren().get(i);
            properties[i] = textArea.prefHeightProperty();
        }
        this.prefHeightProperty().bind(properties[0].add(properties[1]).add(properties[2]));
    }

    List<String> fieldsToUnderline = new ArrayList<>();
    List<String> methodsToUnderline = new ArrayList<>();
    List<String> methodsToItalicise = new ArrayList<>();

    public void format(){
        for (Node node : this.getChildren()){
            //Format inside InlineCSSTextArea
            List<String> toUnderline;
            if (node.equals(this.getChildren().get(1))) toUnderline = fieldsToUnderline;
            else toUnderline = methodsToUnderline;

            InlineCssTextArea textArea = (InlineCssTextArea) node;


            textArea.setStyle(0, textArea.getText().length(), "-fx-underline: false; -fx-font-style: normal;");
//
//            if (!node.equals(this.getChildren().getFirst())) formatHelper(textArea, toUnderline, "{s}", "\\{s}", "-fx-underline: true");
//            if (!node.equals(this.getChildren().get(1))) formatHelper(textArea, methodsToItalicise, "{a}", "\\{a}", "-fx-font-style: italic;");

            //Format other things
            this.setFontColor(this.fontColor);
            this.setFontSize(this.fontSize);
            this.setBorderColor(this.borderColor);
            this.setBorderWidth(this.borderWidth);

        }
    }



    private void formatHelper(InlineCssTextArea textArea, List<String> toFormat, String matchStr, String matchRegex, String style){
        Platform.runLater(() -> {
            while (textArea.getText().contains(matchStr)) {
                int start = textArea.getText().indexOf(matchStr);
                textArea.replaceText(textArea.getText().replaceFirst(matchRegex, ""));
                int end = textArea.getText().indexOf(matchStr, start);
                textArea.replaceText(textArea.getText().replaceFirst(matchRegex, ""));
                toFormat.add(textArea.getText().substring(start, end));
            }
            for (String string : toFormat) {
                int start = textArea.getText().indexOf(string);
                if (start == -1) {
                    toFormat.remove(string);
                    continue;
                }
                textArea.setStyle(start, start + string.length(), style);
            }
        });
    }

    public static void formatSelected(InlineCssTextArea textArea, String style){
        Platform.runLater(() -> {
            String matchStr = textArea.getSelectedText();
            int start = textArea.getText().indexOf(matchStr);
            int end = start + matchStr.length();
            textArea.setStyle(start, start + matchStr.length(), style);
        });
    }


    public static InlineCssTextArea[] getAllAreas() {
        return allAreas.toArray(new InlineCssTextArea[0]);
    }



    public void format(String formatStr){
        String[] tokens = formatStr.split("&");
        this.id = Integer.parseInt(tokens[1]);
        String[] colStr = tokens[2].split("-");
        Double[] color = new Double[]{
                Double.parseDouble(colStr[0]),
                Double.parseDouble(colStr[1]),
                Double.parseDouble(colStr[2]),
                Double.parseDouble(colStr[3])
        };
        this.setFontColor(new Color(color[0], color[1], color[2], color[3]));
        this.setFontSize(Double.parseDouble(tokens[3]));
        colStr = tokens[4].split("-");
        color = new Double[]{
                Double.parseDouble(colStr[0]),
                Double.parseDouble(colStr[1]),
                Double.parseDouble(colStr[2]),
                Double.parseDouble(colStr[3])
        };
        this.setBorderColor(new Color(color[0], color[1], color[2], color[3]));
        this.setBorderWidth(Double.parseDouble(tokens[5]));
        this.setTranslateX(Double.parseDouble(tokens[6]));
        this.setTranslateY(Double.parseDouble(tokens[7]));
        ((InlineCssTextArea) this.getChildren().getFirst()).replaceText(tokens[8]);
        ((InlineCssTextArea) this.getChildren().get(1)).replaceText(tokens[9]);
        ((InlineCssTextArea) this.getChildren().getLast()).replaceText(tokens[10]);
        Platform.runLater(this::format);
    }

    public String getFormat(){
        String name = ((InlineCssTextArea) this.getChildren().getFirst()).getText();
        String fields = ((InlineCssTextArea) this.getChildren().get(1)).getText();
        String methods = ((InlineCssTextArea) this.getChildren().getLast()).getText();
        return String.format("B&%d&%.3f-%.3f-%.3f-%.3f&%.3f&%.3f-%.3f-%.3f-%.3f&%.3f&%.3f&%.3f&%s&%s&%s",// &Attribute, - is secondary delimiter
                this.id,
                fontColor.getRed(), fontColor.getGreen(), fontColor.getBlue(), fontColor.getOpacity(),
                fontSize,
                borderColor.getRed(), borderColor.getGreen(), borderColor.getBlue(), borderColor.getOpacity(),
                borderWidth,
                this.getTranslateX(),
                this.getTranslateY(),
                textHelper(name),
                textHelper(fields),
                textHelper(methods));
        //Must change this to include formatting
    }
    private String textHelper(String string){
        return (string.length() <= 1 ? string : string.substring(0, string.length() - 1).replace("\n", "\\n"));
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
        //Warning: Must be implemented before draggable
        if (!resizable) {
            this.setOnMouseEntered(_ -> {});
            this.setOnMousePressed(_ -> {});
            this.setOnMouseDragged(_ -> {});
            this.setOnMouseReleased(_ -> {});
        }
        else {
            resize();
        }
    }

    public void select(){
        UMLEditorController controller = (UMLEditorController) this.getScene().getUserData();
        controller.showProperties(this, true, true);
    }

    private double mouseX = 0;
    private boolean resizing = false;
    private double anchorX = 0;

    public void resize(){
        //Since height is auto-calibrated, this method will only support width resizing.
        //Of course, if more time was given, height resizing would have been implemented.
        this.setOnMouseEntered(event -> {
            double x = event.getX();
            double w = this.getWidth();

            double margin = Resizable.BORDER_THRESHOLD; // px threshold for borders

            if (x < margin) {
                this.setCursor(Cursor.W_RESIZE);
            } else if (x > w - margin) {
                this.setCursor(Cursor.E_RESIZE);
            } else this.setCursor(Cursor.DEFAULT);
        });
        this.setOnMousePressed(_ -> {
            resizing = this.getCursor() == Cursor.E_RESIZE || this.getCursor() == Cursor.W_RESIZE;
            anchorX = this.getPoint(this.getCursor() == Cursor.W_RESIZE ? Midpoint.RIGHT : Midpoint.LEFT).getX();
        });
        this.setOnMouseDragged(mouseEvent -> {
            if (!resizing) return;
            Pane pane = (Pane) this.getParent();
            mouseX = pane.sceneToLocal(mouseEvent.getSceneX(), mouseEvent.getSceneY()).getX();
            if (this.getCursor() == Cursor.W_RESIZE) this.setTranslateX(mouseX);
            double newWidth = Math.abs(mouseX - anchorX);
            this.setPrefWidth(newWidth);
        });
        this.setOnMouseReleased(_ -> {
            resizing = false;
            this.setCursor(Cursor.DEFAULT);
        });
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

    public double getFontSize(){return this.fontSize;}
    public void setFontSize(double fontSize){
        this.fontSize = fontSize;
        String css = "-fx-font-size: " + fontSize + "px;";
        for (Node n: this.getChildren()){
            InlineCssTextArea ta = (InlineCssTextArea) n;
            ta.setStyle(ta.getStyle() + css);
        }
    }

    public double getBorderWidth(){return this.borderWidth;}
    public void setBorderWidth(double width){
        this.borderWidth = width;
        BorderStroke borderStroke = new BorderStroke(
                borderColor,                      // stroke color
                BorderStrokeStyle.SOLID,          // stroke style
                null,                             // rounded corners
                new BorderWidths(width)           // thickness (2px)
        );
        this.setBorder(new Border(borderStroke));
    }

    public Color getBorderColor(){return this.borderColor;}
    public void setBorderColor(Color newColor){
        this.borderColor = newColor;
        BorderStroke borderStroke = new BorderStroke(
                borderColor,                      // stroke color
                BorderStrokeStyle.SOLID,          // stroke style
                null,                             // rounded corners
                new BorderWidths(borderWidth)           // thickness (2px)
        );
        this.setBorder(new Border(borderStroke));
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
