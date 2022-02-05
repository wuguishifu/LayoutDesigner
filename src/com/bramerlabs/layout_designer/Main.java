package com.bramerlabs.layout_designer;

import java.awt.event.KeyEvent;

public class Main {

    public Canvas canvas;
    public Listener listener;

    public static void main(String[] args) {
        new Main().start();
    }

    public Main() {
        canvas = new Canvas();
        listener = new Listener();
    }

    public void start() {
        canvas.show();
        canvas.addListener(listener);
        listener.addCanvas(canvas);

        this.run();
    }

    @SuppressWarnings("BusyWait")
    public void run() {
        while (!listener.isKeyDown(KeyEvent.VK_ESCAPE)) {
            canvas.update();
            canvas.repaint();

            try {
                Thread.sleep(1000/60);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        canvas.dispose();
    }

    public void update() {
        canvas.update();
    }

}
