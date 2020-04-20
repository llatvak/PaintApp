package fi.tamk.tuni.paintapp;

import android.graphics.Path;

/**
 * Class to hold custom path initialization and attributes.
 *
 * @author Lauri Latva-Kyyny
 * @version 1.0
 */
public class DrawPath {

    public int color;
    public int strokeWidth;
    public Path path;

    /**
     * Parameter constructor to initialize path attributes.
     *
     * @param color current color
     * @param strokeWidth current width
     * @param path current path
     */
    public DrawPath(int color, int strokeWidth, Path path) {
        this.color = color;
        this.strokeWidth = strokeWidth;
        this.path = path;
    }
}
