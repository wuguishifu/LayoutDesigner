package com.bramerlabs.layout_designer.components;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class Box implements DrawComponent {

    private int x1, y1, x2, y2, w, h;
    private int xm, ym;
    private Color color = new Color(0, 0, 0);
    private int cornerRadius = 10;
    private int cpr = 4;

    public boolean selected = false;

    public Box() {

    }

    public Box clone() {
        return new Box(x1, y1, x2, y2);
    }

    public Box(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        w = x2 - x1;
        h = y2 - y1;
        xm = x1 + w / 2;
        ym = y1 + h / 2;
    }

    public void setP1(int x1, int y1) {
        this.x1 = x1;
        this.y1 = y1;
    }

    public void setP2(int x2, int y2) {
        this.x2 = x2;
        this.y2 = y2;
        w = x2 - x1;
        h = y2 - y1;
        xm = x1 + w / 2;
        ym = y1 + h / 2;
    }

    public void updatePoints() {
        if (x2 < x1) {
            x1 += x2;
            x2 = x1 - x2;
            x1 -= x2;
        }

        if (y2 < y1) {
            y1 += y2;
            y2 = y1 - y2;
            y1 -= y2;
        }

        w = x2 - x1;
        h = y2 - y1;
        xm = x1 + w / 2;
        ym = y1 + h / 2;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public boolean inBounds(int mouseX, int mouseY) {
        if (selected) {
            return (x2 < x1 ? (mouseX <= x1 + cpr && mouseX >= x2 - cpr) : (mouseX <= x2 + cpr && mouseX >= x1 - cpr))
            && (y2 < y1 ? (mouseY <= y1 + cpr && mouseY >= y2 - cpr) : (mouseY <= y2 + cpr && mouseY >= y1 - cpr));
        } else {
            return (x2 < x1 ? (mouseX <= x1 && mouseX >= x2) : (mouseX <= x2 && mouseX >= x1))
                    && (y2 < y1 ? (mouseY <= y1 && mouseY >= y2) : (mouseY <= y2 && mouseY >= y1));
        }
    }

    public int[][] getControlPoints() {
        return new int[][]{
                {x1, y1}, // tl
                {xm, y1}, // tm
                {x2, y1}, // tr
                {x2, ym}, // mr
                {x2, y2}, // br
                {xm, y2}, // bm
                {x1, y2}, // bl
                {x1, ym}, // ml
        };
    }

    public int getSelectedControlPoint(int mouseX, int mouseY) {
        int[][] controlPoints = getControlPoints();
        for (int i = 0; i < 8; i++) {
            int[] point = controlPoints[i];
            int dx = mouseX - point[0];
            int dy = mouseY - point[1];
            double r = Math.sqrt(dx * dx + dy * dy);
            if (r < cpr) {
                return i;
            }
        }
        if (inBounds(mouseX, mouseY)) return 8;
        return -1;
    }

    public void moveControlPoint(int cp, int x, int y) {
        switch (cp) {
            case 0 -> updatePoints(x, y, x2, y2);
            case 1 -> updatePoints(x1, y, x2, y2);
            case 2 -> updatePoints(x1, y, x, y2);
            case 3 -> updatePoints(x1, y1, x, y2);
            case 4 -> updatePoints(x1, y1, x, y);
            case 5 -> updatePoints(x1, y1, x2, y);
            case 6 -> updatePoints(x, y1, x2, y);
            case 7 -> updatePoints(x, y1, x2, y2);
        }
    }

    public void move(int dx, int dy) {
        this.x1 += dx;
        this.y1 += dy;
        this.x2 += dx;
        this.y2 += dy;
        updatePoints();
    }

    private void updatePoints(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        w = x2 - x1;
        h = y2 - y1;
        xm = x1 + w / 2;
        ym = y1 + h / 2;
    }

    @Override
    public void paint(Graphics2D g) {
        g.setStroke(new BasicStroke(2));
        g.setColor(color);
        RoundRectangle2D rect = new RoundRectangle2D.Float(w < 0 ? x2 : x1,
                h < 0 ? y2 : y1, Math.abs(w), Math.abs(h),
                cornerRadius, cornerRadius);
        g.draw(rect);
        if (selected) {
            int[][] controlPoints = getControlPoints();
            g.setStroke(new BasicStroke(1));
            g.setColor(Color.WHITE);
            for (int[] point : controlPoints) {
                g.fillRect(point[0] - cpr,  point[1] - cpr,
                        2 * cpr, 2 * cpr);
            }
            g.setColor(Color.BLACK);
            for (int[] point : controlPoints) {
                g.drawRect(point[0] - cpr,  point[1] - cpr,
                        2 * cpr, 2 * cpr);
            }
        }
    }
}
