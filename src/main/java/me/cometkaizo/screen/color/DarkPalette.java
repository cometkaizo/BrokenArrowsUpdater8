package me.cometkaizo.screen.color;

import java.awt.*;

public class DarkPalette extends AbstractPalette {
    public DarkPalette() {
        dark = new Color(25, 25, 25);
        medium = new Color(50, 50, 50);
        light = new Color(65, 65, 65);
        textDark = new Color(100, 100, 100);
        textMedium = new Color(155, 155, 155);
        textLight = new Color(230, 230, 230);
        accent1Dark = accent2Dark = new Color(17, 58, 16);
        accent1Medium = accent2Medium = new Color(40, 86, 24);
        accent1Light = accent2Light = new Color(74, 122, 32);
    }
}
