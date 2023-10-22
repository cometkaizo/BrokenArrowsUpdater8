package me.cometkaizo.screen.color;

import java.awt.*;

public class BluePalette extends AbstractPalette {
    public BluePalette() {
        dark = new Color(8, 23, 23);
        medium = new Color(26, 32, 52);
        light = new Color(57, 70, 73);
        textDark = new Color(100, 100, 100);
        textMedium = new Color(129, 129, 119);
        textLight = new Color(201, 198, 187);
        accent1Dark = accent2Dark = new Color(16, 58, 40);
        accent1Medium = accent2Medium = new Color(24, 86, 64);
        accent1Light = accent2Light = new Color(29, 124, 128);
    }
}
