package org.example.Draw;

import lombok.Getter;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
@Component
@Getter
public class DrawPanel extends JPanel {
    private final ArrayList<ColoredPoint> points = new ArrayList<>();
    private Color pointColor = Color.BLACK;


    private Double maxX = Double.MIN_VALUE;
    private Double maxY = Double.MIN_VALUE;

    public void addPoint(Point point){
//        points.add(point);
        ColoredPoint coloredPoint = new ColoredPoint(point,pointColor);
        points.add(coloredPoint);
        repaint();//重绘面板
    }
    public void setPointColor(Color color) {
        this.pointColor = color;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (ColoredPoint point : points) {
            g.setColor(point.getColor());
            Point p = point.getPoint();
            g.drawOval(p.x, p.y, 5, 5); // 绘制点
            maxX = Math.max(maxX,p.x);
            maxY = Math.max(maxY,p.y);
        }
    }
}
