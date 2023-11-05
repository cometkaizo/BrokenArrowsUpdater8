package me.cometkaizo.brokenarrows.screen;

import me.cometkaizo.brokenarrows.BrokenArrowsApp;
import me.cometkaizo.screen.ButtonGui;
import me.cometkaizo.screen.Coordinate;
import me.cometkaizo.screen.Length;

import java.util.function.Consumer;

public class ActionPromptScreen extends AlertScreen {
    protected ButtonGui button;
    protected String buttonText;
    protected final Consumer<Runnable> buttonAction;
    public ActionPromptScreen(String title, String message, String buttonText, Consumer<Runnable> buttonAction, BrokenArrowsApp app) {
        super(title, message, app);
        this.buttonAction = buttonAction;
        this.buttonText = buttonText;
    }

    @Override
    public void init() {
        super.init();
        button = app.buttonStyle.light()
                .setAllTextSize(24)
                .setPos(Coordinate.abs(Length.direct(() -> panel.messagePanel.right() - app.resolveX(MARGIN_SMALL_LEN) - button.width()), panel.messagePanel.top() + app.resolveY(MARGIN_SMALL_LEN)))
                .setSize(Coordinate.abs(200, 80))
                .setAllText(buttonText)
                .setAction(b -> onButtonPress())
                .build();

        addNestedComponent(button);
    }

    private void onButtonPress() {
        buttonAction.accept(this::close);
    }
}
