package com.bramerlabs.layout_designer;

import com.bramerlabs.layout_designer.components.Box;
import com.bramerlabs.layout_designer.components.DrawComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Stack;

public class Canvas implements KeyListener, MouseMotionListener, MouseListener {

    private final JFrame frame;
    private final JPanel panel;

    private final boolean[] keysDown;
    private final boolean[] keysDownLast;

    private final boolean[] buttonsDown;
    private final boolean[] buttonsDownLast;

    private final ArrayList<DrawComponent> components;

    private final Stack<Action[]> undo;
    private final Stack<Action[]> redo;

    private DrawComponent componentInitial;
    private DrawComponent componentFinal;

    private Box selectedBox = null;
    private int selectedPoint = -1;

    public Canvas() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = new Dimension((int) (screenSize.width * 0.75), (int) (screenSize.height * 0.75));

        keysDown = new boolean[KeyEvent.KEY_LAST];
        keysDownLast = new boolean[KeyEvent.KEY_LAST];
        buttonsDown = new boolean[MouseEvent.MOUSE_LAST];
        buttonsDownLast = new boolean[MouseEvent.MOUSE_LAST];

        components = new ArrayList<>();

        undo = new Stack<>();
        redo = new Stack<>();

        frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        panel = new JPanel() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, panel.getWidth(), panel.getHeight());
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                for (DrawComponent component : components) {
                    component.paint(g2d);
                }
            }
        };
        panel.setPreferredSize(windowSize);

        frame.addKeyListener(this);
        panel.addMouseMotionListener(this);
        panel.addMouseListener(this);

        frame.add(panel);
        frame.pack();

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public void update() {
        System.arraycopy(keysDown, 0, keysDownLast, 0, keysDown.length);
        System.arraycopy(buttonsDown, 0, buttonsDownLast, 0, buttonsDown.length);
    }

    public void repaint() {
        panel.repaint();
    }

    public boolean keyPressed(int keycode) {
        return keysDown[keycode] && !keysDownLast[keycode];
    }

    public boolean keyReleased(int keycode) {
        return !keysDown[keycode] && keysDownLast[keycode];
    }

    public boolean buttonPressed(int buttonCode) {
        return buttonsDown[buttonCode] && !buttonsDownLast[buttonCode];
    }

    public boolean buttonReleased(int buttonCode) {
        return !buttonsDown[buttonCode] && buttonsDownLast[buttonCode];
    }

    public void dispose() {
        frame.dispose();
    }

    private void addHistory(DrawComponent input, DrawComponent output) {
        Action action = new Action(this.components, output, input);
        Action inverse = new Action(this.components, input, output);
        undo.push(new Action[]{action, inverse});
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        keysDown[e.getKeyCode()] = true;

        switch (e.getKeyCode()) {
            case KeyEvent.VK_R:
                Box box = new Box(100, 100, 200, 200);
                components.add(box); // add rectangle if user presses 'r'
                addHistory(null, box);
                break;
            case KeyEvent.VK_BACK_SPACE:
                if (selectedBox != null) {
                    components.remove(selectedBox);
                    addHistory(selectedBox, null);
                }
                break;
        }

        if (keysDown[KeyEvent.VK_CONTROL]) {
            Action[] actions;
            switch (e.getKeyCode()) {
                case KeyEvent.VK_Z:
                    if (!undo.empty()) {
                        actions = undo.pop();
                        this.redo.push(actions);
                        actions[1].performAction();
                    }
                    break;
                case KeyEvent.VK_Y:
                    if (!redo.empty()) {
                        actions = redo.pop();
                        this.undo.push(actions);
                        actions[0].performAction();
                    }
                    break;
            }
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {
        keysDown[e.getKeyCode()] = false;

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        buttonsDown[e.getButton()] = true;

        if (e.getButton() == MouseEvent.BUTTON1) {
            if (selectedBox != null) {
                selectedPoint = selectedBox.getSelectedControlPoint(e.getX(), e.getY());
                componentInitial = selectedBox.clone();
            }

            boolean selected = false;
            for (DrawComponent comp : components) {
                if (comp instanceof Box) {
                    ((Box) comp).selected = false;
                }
            }

            for (DrawComponent comp : components) {
                if (comp instanceof Box) {
                    if (((Box) comp).inBounds(e.getX(), e.getY())) {
                        ((Box) comp).selected = true;
                        selectedBox = (Box) comp;
                        selected = true;
                        break;
                    }
                }
            }

            if (!selected) {
                selectedBox = null;
                selectedPoint = -1;
            }
        }

        px = e.getX();
        py = e.getY();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        buttonsDown[e.getButton()] = false;

        if (e.getButton() == MouseEvent.BUTTON1) {
            if (selectedBox != null) {
                selectedBox.updatePoints();
                componentFinal = selectedBox.clone();
                addHistory(componentInitial, componentFinal);
            }

            if (selectedPoint != -1) {
                selectedPoint = -1;
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    int px, py;
    @Override
    public void mouseDragged(MouseEvent e) {
        if (selectedPoint == 8) {
            selectedBox.move(e.getX() - px, e.getY() - py);
        } else if (selectedPoint != -1) {
            selectedBox.moveControlPoint(selectedPoint, e.getX(), e.getY());
        }

        px = e.getX();
        py = e.getY();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (selectedPoint != -1) {
            selectedBox.moveControlPoint(selectedPoint, e.getX(), e.getY());
        }
    }

    public record Action(ArrayList<DrawComponent> components, DrawComponent add, DrawComponent remove) {
        public void performAction() {
            if (add != null) {
                this.components.add(add);
            }
            if (remove != null) {
                this.components.remove(remove);
            }
        }
    }

}
