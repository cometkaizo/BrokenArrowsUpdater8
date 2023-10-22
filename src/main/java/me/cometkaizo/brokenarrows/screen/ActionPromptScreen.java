package me.cometkaizo.brokenarrows.screen;

import me.cometkaizo.brokenarrows.BrokenArrowsApp;
import me.cometkaizo.screen.*;
import me.cometkaizo.screen.color.ColorSource;
import me.cometkaizo.screen.color.Palette;

import java.awt.*;
import java.util.function.Supplier;

public class ActionPromptScreen extends InfoScreen {
    protected ButtonGui button;
    protected Length buttonWidth = Length.abs(200), buttonHeight = Length.abs(80);
    protected GuiBackground buttonBackground = new GuiBackground(new ColorSource(app, Palette::light));
    protected GuiText buttonText;
    protected ButtonGui.Border buttonBorder = null;
    protected final Supplier<Boolean> buttonAction;
    public ActionPromptScreen(String title, String message, String buttonText, Supplier<Boolean> buttonAction, BrokenArrowsApp app) {
        super(title, message, app);
        this.buttonAction = buttonAction;
        this.buttonText = new GuiText(buttonText, new Font(Font.DIALOG, Font.PLAIN, 24), new ColorSource(this.app, Palette::textMedium));
    }

    @Override
    public void init() {
        super.init();
        button = new ButtonGui(Coordinate.abs(Length.direct(() -> panel.messagePanel.right() - app.resolveX(MARGIN_SMALL_LEN) - button.width()), panel.messagePanel.top() + app.resolveY(MARGIN_SMALL_LEN)),
                Coordinate.of(buttonWidth, buttonHeight),
                buttonText, buttonText, buttonText,
                new Rectangle(),
                buttonBackground, buttonBackground, buttonBackground,
                buttonBorder, buttonBorder, buttonBorder,
                b -> onButtonPress(), app);

        addNestedComponent(button);
    }

    private void onButtonPress() {
        if (buttonAction.get()) app.panel().removeScreen(this);
    }
}
