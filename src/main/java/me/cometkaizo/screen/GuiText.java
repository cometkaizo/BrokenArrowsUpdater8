package me.cometkaizo.screen;

import me.cometkaizo.screen.color.ColorSource;

import java.awt.*;

public class GuiText {
    protected String text;
    protected Font defaultFont;
    protected Font font;
    protected ColorSource color;
    protected double deltaX;
    protected double deltaY;
    protected double deltaXFactor;
    protected double deltaYFactor;

    public GuiText(String text, Font font, ColorSource color, double deltaX, double deltaY, double deltaXFactor, double deltaYFactor) {
        this.text = text;
        this.font = font;
        this.defaultFont = font;
        this.color = color;
        this.deltaX = deltaX;
        this.deltaY = deltaY;
        this.deltaXFactor = deltaXFactor;
        this.deltaYFactor = deltaYFactor;
    }
    public GuiText(String text, Font font, ColorSource color, double deltaXFactor, double deltaYFactor) {
        this(text, font, color, 0, 0, deltaXFactor, deltaYFactor);
    }
    public GuiText(String text, Font font, ColorSource color) {
        this(text, font, color, 0, 0, 0, 0);
    }


    public void render(FullResRenderer r, Graphics2D g, double x, double y, double deltaXFactor, double deltaYFactor) {
        if (!shouldRender()) return;
        g.setColor(color().color());
        if (font() != null) g.setFont(font());
        renderRaw(r, g, x, y, deltaXFactor, deltaYFactor);
    }

    public void renderRaw(FullResRenderer r, Graphics2D g, double x, double y, double deltaXFactor, double deltaYFactor) {
        r.renderString(g, text(), x + deltaX(), y + deltaY(), deltaXFactor() + deltaXFactor, deltaYFactor() + deltaYFactor);
    }

    private boolean shouldRender() {
        return text() != null && !text().isBlank() && color != null && color.isVisible();
    }


    public String text() {
        return text;
    }
    public Font font() {
        return font;
    }
    public ColorSource color() {
        return color;
    }
    public double deltaX() {
        return deltaX;
    }
    public double deltaY() {
        return deltaY;
    }
    public double deltaXFactor() {
        return deltaXFactor;
    }
    public double deltaYFactor() {
        return deltaYFactor;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setDefaultFont(Font defaultFont) {
        this.defaultFont = defaultFont;
    }

    public void setFont(Font font) {
        this.font = font != null ? font : defaultFont;
    }

    public void setColor(ColorSource color) {
        this.color = color;
    }
}
