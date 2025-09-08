package com.example.classmate;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

public class DraggableMaker {
    private double mouseAnchorX;
    private double mouseAnchorY;

    public void makeDraggable(Node node){makeDraggable(node, false);}
    public void makeDraggable(Node node, boolean returnToOriginalPos){
        if (returnToOriginalPos){
            double originalX = node.getTranslateX();
            double originalY = node.getTranslateY();
            node.setOnMouseReleased(mouseEvent -> {
                node.setTranslateX(originalX);
                node.setTranslateY(originalY);
            });
        }

        node.setOnMousePressed(mouseEvent -> {
            mouseAnchorX = mouseEvent.getSceneX() - node.getTranslateX();
            mouseAnchorY = mouseEvent.getSceneY() - node.getTranslateY();
        });

        node.setOnMouseDragged(mouseEvent -> {
            node.setTranslateX(mouseEvent.getSceneX() - mouseAnchorX);
            node.setTranslateY(mouseEvent.getSceneY() - mouseAnchorY);
        });
    }
}
