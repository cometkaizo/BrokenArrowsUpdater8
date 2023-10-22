package me.cometkaizo.screen.color;

import me.cometkaizo.brokenarrows.BrokenArrowsApp;

import java.awt.*;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public class ColorSource {
    protected Color color;
    protected Supplier<Palette> paletteSup;
    protected Supplier<Color> colorSup;
    protected BrokenArrowsApp defaultAppInstance;

    public ColorSource(Color color) {
        this.color = color;
    }

    public ColorSource(Supplier<Color> colorSup) {
        this.colorSup = colorSup;
    }

    public ColorSource(Supplier<Palette> paletteSup, Function<Palette, Color> colorFunc) {
        this(() -> colorFunc.apply(paletteSup.get()));
        this.paletteSup = paletteSup;
    }

    public ColorSource(BrokenArrowsApp app, Function<Palette, Color> colorFunc) {
        this.defaultAppInstance = app;
        setColor(colorFunc);
    }

    public Color color() {
        return color != null ? color : colorSup != null ? colorSup.get() : null;
    }

    public void setColor(Color color) {
        this.color = color;
        this.colorSup = null;
    }

    public void setColor(Supplier<Color> colorSup) {
        this.colorSup = colorSup;
        this.color = null;
    }

    public void setColor(Function<Palette, Color> colorFunc) {
        setColor(() -> {
            Palette palette;
            if (paletteSup != null) palette = paletteSup.get();
            else if (defaultAppInstance != null) palette = defaultAppInstance.palette();
            else return null;
            if (palette == null) return null;
            return colorFunc.apply(palette);
        });
    }

    public void setColor(BrokenArrowsApp app, Function<Palette, Color> colorFunc) {
        setColor(() -> {
            if (app == null) return null;
            Palette palette = app.palette();
            if (palette == null) return null;
            return colorFunc.apply(palette);
        });
    }

    public void setColor(Supplier<Palette> paletteSup, Function<Palette, Color> colorFunc) {
        setColor(() -> {
            if (paletteSup == null) return null;
            Palette palette = paletteSup.get();
            if (palette == null) return null;
            return colorFunc.apply(palette);
        });
    }


    public boolean isVisible() {
        Color c = color();
        return c != null && c.getAlpha() > 0;
    }

    public boolean isOpaque() {
        Color c = color();
        return c != null && c.getAlpha() == 255;
    }
    public boolean hasTransparency() {
        Color c = color();
        return c == null || c.getAlpha() < 255;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ColorSource that = (ColorSource) o;
        return Objects.equals(color, that.color) && Objects.equals(paletteSup, that.paletteSup) && Objects.equals(colorSup, that.colorSup) && Objects.equals(defaultAppInstance, that.defaultAppInstance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, paletteSup, colorSup, defaultAppInstance);
    }

    @Override
    public String toString() {
        return "ColorSource{" +
                "color=" + color +
                ", paletteSup=" + paletteSup +
                ", colorSup=" + colorSup +
                ", defaultAppInstance=" + defaultAppInstance +
                '}';
    }
}
