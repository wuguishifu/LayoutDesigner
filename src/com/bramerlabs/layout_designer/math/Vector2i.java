package com.bramerlabs.layout_designer.math;

public class Vector2i {

    public int x, y;

    public Vector2i(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static Vector2i add(Vector2i u, Vector2i v) {
        return new Vector2i(u.x + v.x, u.y + v.y);
    }

    public static float length(Vector2i v) {
        return (float) Math.sqrt(v.x * v.x + v.y * v.y);
    }

}
