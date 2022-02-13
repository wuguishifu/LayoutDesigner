package com.bramerlabs.layout_designer;

import java.awt.event.KeyEvent;

public class LayoutDesigner {

    private Canvas canvas;

    public static void main(String[] args) {
        new LayoutDesigner().run();
    }

    @SuppressWarnings("BusyWait")
    public void run() {
        canvas = new Canvas();
        while (!canvas.keyPressed(KeyEvent.VK_ESCAPE)) {
            canvas.update();
            canvas.repaint();
            try {
                Thread.sleep(1000 / 60);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        canvas.dispose();
    }
}
