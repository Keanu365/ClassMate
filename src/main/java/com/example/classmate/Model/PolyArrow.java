package com.example.classmate.Model;

import com.example.classmate.Model.UMLBox.Midpoint;
import com.example.classmate.Controller.UMLEditorController;
import javafx.application.Platform;
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
    private double maxOffset = 25;
    private final UMLBox from;
    private final UMLBox to;

    public PolyArrow(UMLBox from, UMLBox to){
        this.from = from;
        this.to = to;
        from.arrow = this;
        to.arrow = this;
        this.getChildren().addAll(arrow, arrowHead);
        arrowHead.getPoints().addAll(0.0, 0.0,
                -16.0, -8.0,
                -16.0,  8.0);
        this.arrow.setStrokeWidth(3.0);
        this.arrow.setStroke(strokeColor);
        if (to.isInterface() && !from.isInterface()) this.arrow.getStrokeDashArray().addAll(1.0, 8.0);
        else if (!to.isInterface() && from.isInterface()) throw new IllegalArgumentException("Interfaces cannot extend non-interfaces!");
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
        this.maxOffset *= width / 3.0;
        this.updateArrow();
    }

    public void updateArrow() {
        //Remember Y is inverted, i.e. the lower down it is the greater the value
        Platform.runLater(() -> {
            if (from.getPoint(Midpoint.TOP).getY() > to.getPoint(Midpoint.BOTTOM).getY() + maxOffset * 2.0) {
                this.elbowShape();
            }else if (from.getPoint(Midpoint.BOTTOM).getY() + maxOffset * 2.0 < to.getPoint(Midpoint.TOP).getY()) {
                this.elbowShape();
            }else {
                double distance = Math.abs(from.getPoint(Midpoint.CENTRE).getX() - to.getPoint(Midpoint.CENTRE).getX());
                distance -= from.getWidth() / 2.0;
                distance -= to.getWidth() / 2.0;
                if (distance < maxOffset * 2.0) {
                    Point2D toMidpoint = to.getPoint(Midpoint.CENTRE);
                    if (from.getPoint(Midpoint.TOP).getY() > toMidpoint.getY() + maxOffset || from.getPoint(Midpoint.BOTTOM).getY() + maxOffset < toMidpoint.getY()) {
                        distance += (from.getWidth() + to.getWidth()) / 4.0; //2*2
                        distance -= maxOffset;
                        if (distance < 0) this.cShape();
                        else this.LShape();
                    } else this.uShape();
                } else this.zShape();
            }
            arrowHead.setScaleX(this.arrow.getStrokeWidth() / 3.0);
            arrowHead.setScaleY(this.arrow.getStrokeWidth() / 3.0);
            if (to.isInterface() && !from.isInterface()) this.arrow.getStrokeDashArray().setAll(1.0, 8.0);
            else this.arrow.getStrokeDashArray().setAll();
        });
        //TODO: you need to implement save and tutorial and letting the user underline
    }

    private void elbowShape(){
        Point2D start, end;
        if (from.getTranslateY() > to.getTranslateY()) {
            start = from.getPoint(Midpoint.TOP);
            end = to.getPoint(Midpoint.BOTTOM);
        }else{
            start = from.getPoint(Midpoint.BOTTOM);
            end = to.getPoint(Midpoint.TOP);
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
    private void zShape(){
        Point2D start, end;
        if (from.getTranslateX() > to.getTranslateX()) {
            start = from.getPoint(Midpoint.LEFT);
            end = to.getPoint(Midpoint.RIGHT);
        }else{
            start = from.getPoint(Midpoint.RIGHT);
            end = to.getPoint(Midpoint.LEFT);
        }
        double midX = (start.getX() + end.getX()) / 2;
        this.arrow.getPoints().setAll(
                start.getX(), start.getY(),
                midX, start.getY(),
                midX, end.getY(),
                end.getX(), end.getY()
        );
        arrowHead.setRotate(start.getX() < end.getX() ? 0 : 180);
        arrowHead.setTranslateX(end.getX() + (arrowHead.getRotate() > 0 ? 12 : 2));
        arrowHead.setTranslateY(end.getY());
    }
    private void LShape(){
        Point2D fromMidpoint = from.getPoint(Midpoint.CENTRE);
        Point2D toMidpoint = to.getPoint(Midpoint.CENTRE);
        Point2D start = from.getPoint(fromMidpoint.getY() > toMidpoint.getY() ? Midpoint.TOP : Midpoint.BOTTOM);
        Point2D end = to.getPoint(fromMidpoint.getX() > toMidpoint.getX() ? Midpoint.RIGHT : Midpoint.LEFT);
        this.arrow.getPoints().setAll(
                start.getX(), start.getY(),
                start.getX(), end.getY(),
                end.getX(), end.getY()
        );
        arrowHead.setRotate(start.getX() < end.getX() ? 0 : 180);
        arrowHead.setTranslateX(end.getX() + (arrowHead.getRotate() > 0 ? 12 : 2));
        arrowHead.setTranslateY(end.getY());
    }
    private void uShape(){
        Point2D start = from.getPoint(Midpoint.BOTTOM);
        Point2D end = to.getPoint(Midpoint.BOTTOM);
        double maxY = Math.max(start.getY(), end.getY()) + maxOffset;
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
    private void cShape(){
        Point2D start = from.getPoint(Midpoint.LEFT);
        Point2D end = to.getPoint(Midpoint.LEFT);
        double minX = Math.min(start.getX(), end.getX()) - maxOffset;
        this.arrow.getPoints().setAll(
                start.getX(), start.getY(),
                minX, start.getY(),
                minX, end.getY(),
                end.getX(), end.getY()
        );
        arrowHead.setRotate(0);
        arrowHead.setTranslateX(end.getX() + 2);
        arrowHead.setTranslateY(end.getY());
    }

    public void detach(){
        //Detach arrow from UMLBoxes.
        this.from.arrow = null;
        this.to.arrow = null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PolyArrow other) {
            return this.from == other.from && this.to == other.to;
        }
        return false;
    }
    public boolean checkCyclic(PolyArrow[] others){
        UMLBox currentTo = this.to;
        for (int i = 0; i < others.length; i++) {
            if (others[i].from == currentTo){
                currentTo = others[i].to;
                if (this.from == currentTo) return true;
                i = 0; //Check the entire list again.
            }
        }
        return this.from == currentTo;
    }
}
