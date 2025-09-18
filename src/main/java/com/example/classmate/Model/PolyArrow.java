package com.example.classmate.Model;

import com.example.classmate.Controller.UMLEditorController;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;

public class PolyArrow extends Group implements Selectable{
    private final Polyline arrow = new Polyline();
    private final Polygon arrowHead = new Polygon();

    private Color strokeColor =  Color.BLACK;
    private double yOffsetLimit = 20;
    private final UMLBox from;
    private final UMLBox to;

    public PolyArrow(UMLBox from, UMLBox to){
        this.from = from;
        this.to = to;
        this.getChildren().addAll(arrow, arrowHead);
        arrowHead.getPoints().addAll(0.0, 0.0,
                -16.0, -8.0,
                -16.0,  8.0);
        this.arrow.setStrokeWidth(3.0);
        this.arrow.setStroke(strokeColor);
        this.arrowHead.setStyle("-fx-background-color: black;");
        this.updateArrow();
        ReadOnlyDoubleProperty[] properties = new ReadOnlyDoubleProperty[]{
                from.translateXProperty(), from.translateYProperty(),
                to.translateXProperty(), to.translateYProperty(),
                from.widthProperty(), from.heightProperty(),
                to.widthProperty(), to.heightProperty(),
        };
        for (ReadOnlyDoubleProperty property : properties) {
            property.addListener((_, _, _) -> this.updateArrow());
        }
    }

    public void setSelectable(boolean selectable){
        if (!selectable) this.setOnMouseClicked(_ -> {});
        else {
            this.setOnMouseClicked(_ -> select());
        }
    }

    public void select(){
        UMLEditorController controller = (UMLEditorController) this.getScene().getUserData();
        controller.showProperties(this, true, false);
    }

    public Color getStrokeColor(){return this.strokeColor;}
    public void setStrokeColor(Color strokeColor){
        this.strokeColor = strokeColor;
        this.arrow.setStroke(strokeColor);
        this.arrowHead.setFill(strokeColor);
    }
    public double getStrokeWidth(){return this.arrow.getStrokeWidth();}
    public void setStrokeWidth(double width){
        this.arrow.setStrokeWidth(width);
        this.yOffsetLimit *= width / 3.0;
        this.updateArrow();
    }

    public void updateArrow() {
        //Remember Y is inverted, i.e. the lower down it is the greater the value
//        double avgWidth = (from.getWidth() + to.getWidth()) / 2.0;
//        if (from.getMiddlePoint().getX() + avgWidth <= to.getMiddlePoint().getX()) {
//            this.elbowShape();
//        }else FIXME
        if (from.getTopMiddlePoint().getY() > to.getBottomMiddlePoint().getY() + yOffsetLimit * 2.0) {
            this.elbowShape();
        }else if (from.getBottomMiddlePoint().getY() + yOffsetLimit * 2.0 < to.getTopMiddlePoint().getY()) {
            this.elbowShape();
        }else if (from.getMiddlePoint().getY() > to.getMiddlePoint().getY()) {
            this.uShape();
        }else{
            this.nShape();
        }
        arrowHead.setScaleX(this.arrow.getStrokeWidth() / 3.0);
        arrowHead.setScaleY(this.arrow.getStrokeWidth() / 3.0);
        //TODO: you need to implement resize and save and tutorial and letting the user underline
    }

    private void elbowShape(){
        Point2D start, end;
        if (from.getTranslateY() > to.getTranslateY()) {
            start = from.getTopMiddlePoint();
            end = to.getBottomMiddlePoint();
        }else{
            start = from.getBottomMiddlePoint();
            end = to.getTopMiddlePoint();
        }
        double midY = (start.getY() + end.getY()) / 2;
        this.arrow.getPoints().setAll(
                start.getX(), start.getY(),
                start.getX(), midY,
                end.getX(), midY,
                end.getX(), end.getY()
        );
        arrowHead.setRotate(start.getY() < end.getY() ? 90 : -90);
        arrowHead.setTranslateX(end.getX() + 8);
        arrowHead.setTranslateY(end.getY() + (arrowHead.getRotate() > 0 ? -5 : 5));
    }
    private void uShape(){
        Point2D start = from.getBottomMiddlePoint();
        Point2D end = to.getBottomMiddlePoint();
        double maxY = Math.max(start.getY(), end.getY()) + yOffsetLimit;
        this.arrow.getPoints().setAll(
                start.getX(), start.getY(),
                start.getX(), maxY,
                end.getX(), maxY,
                end.getX(), end.getY()
        );
        arrowHead.setRotate(-90);
        arrowHead.setTranslateX(end.getX() + 8);
        arrowHead.setTranslateY(end.getY() + 5);
    }
    private void nShape(){
        Point2D start = from.getTopMiddlePoint();
        Point2D end = to.getTopMiddlePoint();
        double minY = Math.min(start.getY(), end.getY()) - yOffsetLimit;
        this.arrow.getPoints().setAll(
                start.getX(), start.getY(),
                start.getX(), minY,
                end.getX(), minY,
                end.getX(), end.getY()
        );
        arrowHead.setRotate(90);
        arrowHead.setTranslateX(end.getX() + 8);
        arrowHead.setTranslateY(end.getY() + (arrowHead.getRotate() > 0 ? -5 : 5));
    }
}
