package me.cometkaizo.brokenarrows;

import me.cometkaizo.screen.ButtonGui;
import me.cometkaizo.screen.GuiBackground;
import me.cometkaizo.screen.GuiText;
import me.cometkaizo.screen.color.ColorSource;
import me.cometkaizo.screen.color.Palette;

import java.awt.*;
import java.awt.geom.RectangularShape;

public class ButtonStyle {
    protected final BrokenArrowsApp app;
    protected GuiBackground lighterBackground, lightBackground, mediumBackground, darkBackground, darkerBackground;
    protected ColorSource textColor;
    protected Font font = new Font(Font.DIALOG, Font.BOLD, 24);
    protected GuiText text;
    protected RectangularShape shape;

    public ButtonStyle(BrokenArrowsApp app) {
        this.app = app;
        lighterBackground = new GuiBackground(new ColorSource(app, Palette::lighter));
        lightBackground = new GuiBackground(new ColorSource(app, Palette::light));
        mediumBackground = new GuiBackground(new ColorSource(app, Palette::medium));
        darkBackground = new GuiBackground(new ColorSource(app, Palette::dark));
        darkerBackground = new GuiBackground(new ColorSource(app, Palette::darker));
        textColor = new ColorSource(app, Palette::textLight);
        text = new GuiText("Sample Text", font, textColor);
        shape = new Rectangle();
    }

    public ButtonGui.Builder medium() {
        ButtonGui.Builder builder = new ButtonGui.Builder(app);
        builder.background = mediumBackground;
        builder.hoverBackground = lightBackground;
        builder.pressBackground = darkBackground;
        builder.setAllText(text.copy());
        builder.shape = shape;
        return builder;
    }
    public ButtonGui.Builder light() {
        ButtonGui.Builder builder = new ButtonGui.Builder(app);
        builder.background = lightBackground;
        builder.hoverBackground = lighterBackground;
        builder.pressBackground = mediumBackground;
        builder.setAllText(text.copy());
        builder.shape = shape;
        return builder;
    }

}
