package com.example.classmate.Model;

import javafx.scene.Node;
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
        super();
        //Border stuff :3 TODO: let user choose border stroke size
        BorderStroke borderStroke = new BorderStroke(
                Color.BLACK,                      // stroke color
                BorderStrokeStyle.SOLID,          // stroke style
                null,                             // rounded corners
                new BorderWidths(1)            // thickness (2px)
        );
        this.setBorder(new Border(borderStroke));
        //Text stuff :3
        String[] texts = new String[]{name, fields, methods};
        for (String text : texts) {
            TextArea textArea = new TextArea(text);
            textArea.getStyleClass().add("uml_label");
            textArea.setPrefRowCount(Math.max(1, text.split("\\n").length));
            textArea.setWrapText(true); //FIXME
            this.getChildren().add(textArea);
            textArea.textProperty().addListener((obs, oldText, newText) -> {
                int lines = newText.split("\\n").length;
                textArea.setPrefRowCount(Math.max(1, lines));
            });
        }
        new DraggableMaker().makeDraggable(this);
    }

    public void setEditable(boolean editable){
        for (Node n : this.getChildren()){
            TextArea textArea = (TextArea) n;
            textArea.setEditable(editable);
            textArea.setMouseTransparent(!editable);
        }
    }
    public boolean isEditable(){return ((TextArea) this.getChildren().getFirst()).isEditable();}
}
