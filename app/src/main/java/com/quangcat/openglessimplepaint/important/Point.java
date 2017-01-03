package com.quangcat.openglessimplepaint.important;


public class Point {

    private final float x;
    private final float y;
    private final boolean newStroke;

    public Point(float x, float y, boolean newStroke) {
        this.x = x;
        this.y = y;
        this.newStroke = newStroke;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public boolean isNewStroke() {
        return newStroke;
    }

}
