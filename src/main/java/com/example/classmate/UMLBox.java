package com.example.classmate;

import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class UMLBox extends VBox {
    public UMLBox() {
        this("Name", "Fields", "Methods");
    }
    public UMLBox(UMLClass uc){
        this(uc.getName(), uc.getFields(), uc.getMethods());
    }
    public UMLBox(String name, String fields, String methods) {
        BorderStroke borderStroke = new BorderStroke(
                Color.BLACK,                      // stroke color
                BorderStrokeStyle.SOLID,          // stroke style
                null,                             // rounded corners
                new BorderWidths(1)            // thickness (2px)
        );
        this.setBorder(new Border(borderStroke));
        String[] texts = new String[]{name, fields, methods};
        for (String text : texts) {
            TextArea textArea = new TextArea(text);
            textArea.getStyleClass().add("uml_label");
            textArea.setPrefRowCount(1);
            textArea.setWrapText(true); //FIXME
            this.getChildren().add(textArea);
            textArea.textProperty().addListener((obs, oldText, newText) -> {
                int lines = newText.split("\\n").length;
                textArea.setPrefRowCount(Math.max(1, lines));
            });
        }
    }
}
