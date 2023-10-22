package me.cometkaizo.util;

import java.awt.*;

import static java.lang.Math.round;

public class ColorUtils {

    /**
     * Credit: <a href="http://www.java2s.com/Code/Java/2D-Graphics-GUI/Blendtwocolors.htm">java2s.com</a>
     */
    public static Color blend(Color c0, Color c1) {
        double totalAlpha = c0.getAlpha() + c1.getAlpha();
        double weight0 = c0.getAlpha() / totalAlpha;
        double weight1 = c1.getAlpha() / totalAlpha;

        double r = weight0 * c0.getRed() + weight1 * c1.getRed();
        double g = weight0 * c0.getGreen() + weight1 * c1.getGreen();
        double b = weight0 * c0.getBlue() + weight1 * c1.getBlue();
        double a = Math.max(c0.getAlpha(), c1.getAlpha());

        return new Color((int) r, (int) g, (int) b, (int) a);
    }

    /**
     * Overlays the foreground color onto the background color. Ignores the opacity of the background color
     * @param background color underneath
     * @param foreground color on top
     * @return the appropriate blend of the two colors
     */
    public static Color overlay(Color foreground, Color background) {
        if (foreground == null) return background;
        if (background == null) return foreground;
        if (foreground.getAlpha() == 255) return foreground;
        if (foreground.getAlpha() == 0) return new Color(background.getRGB());

        int fR = foreground.getRed(), fG = foreground.getGreen(), fB = foreground.getBlue();
        int bR = background.getRed(), bG = background.getGreen(), bB = background.getBlue();
        float fA = (float)foreground.getAlpha() / 255F, bA = 1 - fA;

        int r = round(fR * fA + bR * bA),
                g = round(fG * fA + bG * bA),
                b = round(fB * fA + bB * bA);
        return new Color(r, g, b);
    }

    public static Color withAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    public static Color gradient(int x, Color c0, Color c1, int x0, int x1) {
        double weight1 = MathUtils.clamp((double)(x - x0) / Math.abs(x1 - x0), 0, 1);
        double weight0 = 1 - weight1;

        double r = weight0 * c0.getRed() + weight1 * c1.getRed();
        double g = weight0 * c0.getGreen() + weight1 * c1.getGreen();
        double b = weight0 * c0.getBlue() + weight1 * c1.getBlue();
        double a = weight0 * c0.getAlpha() + weight1 * c1.getAlpha();

        return new Color((int) r, (int) g, (int) b, (int) a);
    }
}
