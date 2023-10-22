package me.cometkaizo.util;

import java.awt.*;
import java.awt.geom.Area;
import java.util.Arrays;

public class ShapeUtils {
    public static final double EPSILON = 1e-5;
    /**
     * Expand or shrink a shape in all directions by a defined offset.
     *
     * @param s      Shape
     * @param offset Offset
     * @return New shape that was expanded or shrunk by the specified amount
     */
    public static Area grow(final Shape s, final double offset) {
        return grow(s, offset, BasicStroke.JOIN_MITER, 10f);
    }
    /**
     * Expand or shrink a shape in all directions by a defined offset.
     *
     * @param s          Shape
     * @param offset     Offset to expand/shrink
     * @param join       Method for handling edges (see BasicStroke)
     * @param miterLimit Limit for miter joining method
     * @return New shape that is expanded or shrunk by the specified amount
     */
    public static Area grow(final Shape s, final double offset, int join,
                            float miterLimit) {
        Area shape = new Area(s);

        if (MathUtils.almostEquals(offset, 0.0, EPSILON)) {
            return shape;
        }

        Stroke stroke = new BasicStroke((float) Math.abs(2.0 * offset),
                BasicStroke.CAP_SQUARE, join, miterLimit);
        Area strokeShape = new Area(stroke.createStrokedShape(s));

        if (offset > 0.0) {
            shape.add(strokeShape);
        } else {
            shape.subtract(strokeShape);
        }

        return shape;
    }

    /**
     * Calculates whether a point is contained inside a polygonal boundary
     * See: <a href="http://www.ecse.rpi.edu/Homepages/wrf/Research/Short_Notes/pnpoly.html">ecse.rpi.edu</a>
     * See: <a href="https://stackoverflow.com/questions/8721406/how-to-determine-if-a-point-is-inside-a-2d-convex-polygon">stackoverflow.com</a>
     * @param xCoords x coordinates of the polygon
     * @param yCoords y coordinates of the polygon
     * @param x x coordinate of the point to test
     * @param y y coordinate of the point to test
     * @return true if the point is inside the boundary, false otherwise
     *
     */
    public static boolean contains(int[] xCoords, int[] yCoords, int x, int y) {
        if (xCoords.length != yCoords.length) throw new IllegalArgumentException("Coordinates must be in pairs: " + Arrays.toString(xCoords) + ", " + Arrays.toString(yCoords));
        boolean result = false;
        for (int i = 0, j = xCoords.length - 1; i < xCoords.length; j = i++) {
            if ((yCoords[i] > y) != (yCoords[j] > y) &&
                    (x < (xCoords[j] - xCoords[i]) * (y - yCoords[i]) / (yCoords[j] - yCoords[i]) + xCoords[i])) {
                result = !result;
            }
        }
        return result;
    }
    public static boolean contains(double[] xCoords, double[] yCoords, double x, double y) {
        if (xCoords.length != yCoords.length) throw new IllegalArgumentException("Coordinates must be in pairs: " + Arrays.toString(xCoords) + ", " + Arrays.toString(yCoords));
        boolean result = false;
        for (int i = 0, j = xCoords.length - 1; i < xCoords.length; j = i++) {
            if ((yCoords[i] > y) != (yCoords[j] > y) &&
                    (x < (xCoords[j] - xCoords[i]) * (y - yCoords[i]) / (yCoords[j] - yCoords[i]) + xCoords[i])) {
                result = !result;
            }
        }
        return result;
    }

}
