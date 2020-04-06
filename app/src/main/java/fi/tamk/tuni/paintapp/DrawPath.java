package fi.tamk.tuni.paintapp;

import android.graphics.Path;

public class DrawPath {

    public int color;
    public int strokeWidth;
    public Path path;

    public DrawPath(int color, int strokeWidth, Path path) {
        this.color = color;
        this.strokeWidth = strokeWidth;
        this.path = path;
    }
}
