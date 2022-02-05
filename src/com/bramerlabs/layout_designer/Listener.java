package com.bramerlabs.layout_designer;

import java.awt.event.*;

public class Listener implements MouseMotionListener, MouseListener, KeyListener {

    public boolean[] keyButtonsDown;
    public boolean[] mouseButtonsDown;
    public int mouseX;
    public int mouseY;

    public Canvas canvas;

    public Listener() {
        keyButtonsDown = new boolean[KeyEvent.KEY_LAST];
        mouseButtonsDown = new boolean[MouseEvent.MOUSE_LAST];
        mouseX = 0;
        mouseY = 0;
    }

    public void addCanvas(Canvas canvas) {
        this.canvas = canvas;
    }

    public boolean isKeyDown(int keyCode) {
        return keyButtonsDown[keyCode];
    }

    public boolean isMouseButtonDown(int buttonCode) {
        return mouseButtonsDown[buttonCode];
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        keyButtonsDown[e.getKeyCode()] = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keyButtonsDown[e.getKeyCode()] = false;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        mouseButtonsDown[e.getButton()] = true;
        if (e.getButton() == MouseEvent.BUTTON1) {
            canvas.setStart(new int[]{e.getX(), e.getY()});
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mouseButtonsDown[e.getButton()] = false;
        if (canvas.start != null) {
            canvas.setEnd(new int[]{e.getX(), e.getY()});
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        if (mouseButtonsDown[MouseEvent.BUTTON1]) {
            canvas.setTemp(new int[]{mouseX, mouseY});
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }
}
