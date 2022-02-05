package com.bramerlabs.layout_designer;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Canvas {

    public JFrame frame;
    public JPanel panel;
    public Listener listener;

    public int[] start;
    public ArrayList<Integer[]> rectangles;
    public int[] tempRectangle;

    public int resolution = 20; // pixel size of the grid

    public Canvas() {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension defaultWindowSize = new Dimension((int) (screen.width * 0.75), (int) (screen.height * 0.75));

        rectangles = new ArrayList<>();
        tempRectangle = new int[4];

        frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        panel = new JPanel() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // repaint the background white
                g2d.setColor(new Color(255, 255, 255));
                g2d.fillRect(0, 0, defaultWindowSize.width, defaultWindowSize.height);

                g2d.setColor(new Color(197, 238, 252));
                for (int i = 0; i < defaultWindowSize.width; i += resolution) {
                    g2d.drawLine(i, 0, i, defaultWindowSize.height);
                }
                for (int i = 0; i < defaultWindowSize.height; i += resolution) {
                    g2d.drawLine(0, i, defaultWindowSize.width, i);
                }

                g2d.setColor(new Color(0, 0, 0));
                for (Integer[] r : rectangles) {
                    int w = Math.abs(r[2]);
                    int h = Math.abs(r[3]);
                    int x = r[0];
                    int y = r[1];
                    if (r[2] < 0) x += r[2];
                    if (r[3] < 0) y += r[3];
                    g2d.drawRect(x, y, w, h);
                }

                int w = Math.abs(tempRectangle[2]);
                int h = Math.abs(tempRectangle[3]);
                int x = tempRectangle[0];
                int y = tempRectangle[1];
                if (tempRectangle[2] < 0) x += tempRectangle[2];
                if (tempRectangle[3] < 0) y += tempRectangle[3];
                g2d.drawRect(x, y, w, h);

            }
        };
        panel.setPreferredSize(defaultWindowSize);

        frame.add(panel);
        frame.pack();

        frame.setLocationRelativeTo(null);
    }

    public void setStart(int[] start) {
        this.start = start;
        this.tempRectangle[0] = start[0];
        this.tempRectangle[1] = start[1];
    }

    public void setTemp(int[] temp) {
        this.tempRectangle[2] = temp[0] - tempRectangle[0];
            this.tempRectangle[3] = temp[1] - tempRectangle[1];
    }

    public void setEnd(int[] end) {
        rectangles.add(new Integer[]{start[0], start[1], end[0] - start[0], end[1] - start[1]});
        this.start = null;
    }

    public void show() {
        frame.setVisible(true);
    }

    public void repaint() {
        panel.repaint();
    }

    public void update() {

    }

    public void dispose() {
        frame.dispose();
    }

    public void addListener(Listener listener) {
        this.listener = listener;
        frame.addKeyListener(listener);
        panel.addMouseMotionListener(listener);
        panel.addMouseListener(listener);
    }

}
