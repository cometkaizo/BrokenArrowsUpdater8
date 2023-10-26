package me.cometkaizo.screen.color;

import java.awt.*;

public class AbstractPalette implements Palette {
    public Color darker, dark, medium, light, lighter;
    public Color textDark, textMedium, textLight;
    public Color accent1Dark, accent1Medium, accent1Light;
    public Color accent2Dark, accent2Medium, accent2Light;

    @Override
    public Color darker() {
        return darker;
    }

    @Override
    public Color dark() {
        return dark;
    }

    @Override
    public Color medium() {
        return medium;
    }

    @Override
    public Color light() {
        return light;
    }

    @Override
    public Color lighter() {
        return lighter;
    }

    @Override
    public Color textDark() {
        return textDark;
    }

    @Override
    public Color textMedium() {
        return textMedium;
    }

    @Override
    public Color textLight() {
        return textLight;
    }

    @Override
    public Color accent1Dark() {
        return accent1Dark;
    }

    @Override
    public Color accent1Medium() {
        return accent1Medium;
    }

    @Override
    public Color accent1Light() {
        return accent1Light;
    }

    @Override
    public Color accent2Dark() {
        return accent2Dark;
    }

    @Override
    public Color accent2Medium() {
        return accent2Medium;
    }

    @Override
    public Color accent2Light() {
        return accent2Light;
    }
}
