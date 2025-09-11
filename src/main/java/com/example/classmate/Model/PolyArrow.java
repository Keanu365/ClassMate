package com.example.classmate.Model;

import javafx.geometry.Point2D;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;

public class PolyArrow extends Polyline {
    private static final double ARROW_LENGTH = 10;
    private static final double ARROW_ANGLE  = Math.toRadians(25);

    private final Polygon arrowHead = new Polygon();

    public PolyArrow() {
        arrowHead.getPoints().addAll(0.0, 0.0,
                -ARROW_LENGTH, -ARROW_LENGTH / 2,
                -ARROW_LENGTH,  ARROW_LENGTH / 2);
    }

    public void updateArrow(Point2D start, Point2D end) {
        // Update main line
        getPoints().setAll(start.getX(), start.getY(),
                end.getX(),   end.getY());

        // Position arrowhead
        double angle = Math.atan2(end.getY() - start.getY(), end.getX() - start.getX());
        arrowHead.setRotate(Math.toDegrees(angle));
        arrowHead.setLayoutX(end.getX());
        arrowHead.setLayoutY(end.getY());
    }

    public Polygon getArrowHead() {
        return arrowHead;
    }
}
