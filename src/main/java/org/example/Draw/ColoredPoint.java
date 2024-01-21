package org.example.Draw;

import java.awt.*;

public class ColoredPoint {
    private Point point;
    private Color color;

    public ColoredPoint(Point point, Color color) {
        this.point = point;
        this.color = color;
    }

    public Point getPoint() {
        return point;
    }

    public Color getColor() {
        return color;
    }
}
