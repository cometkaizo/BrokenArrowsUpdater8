package me.cometkaizo.screen;

import me.cometkaizo.brokenarrows.BrokenArrowsApp;

import java.util.ArrayList;
import java.util.function.Consumer;

public class MenuGui extends RepeaterGui {
    protected ButtonGui.Builder defaultButton;

    public MenuGui(Coordinate position, Coordinate size,
                   Axis axis,
                   SpacingMode spacingMode, Length primarySpacing, Length secondarySpacing,
                   ButtonGui.Builder defaultButton,
                   BrokenArrowsApp app) {
        super(position, size, new ArrayList<>(0), axis, spacingMode, primarySpacing, secondarySpacing, app);
        this.defaultButton = defaultButton;
    }

    public ButtonGui addButton(ButtonGui.Builder builder) {
        ButtonGui button = build(builder);
        addNestedComponent(button);
        return button;
    }

    protected ButtonGui build(ButtonGui.Builder builder) {
        return builder.buildOrDefault(defaultButton);
    }

    public static class Builder {
        public Coordinate position, size;
        public Axis axis;
        public SpacingMode spacingMode;
        public Length primarySpacing, secondarySpacing;
        public BrokenArrowsApp app;
        public ButtonGui.Builder buttonBuilder;

        public Builder(Coordinate position, Coordinate size,
                       Axis axis,
                       SpacingMode spacingMode, Length primarySpacing, Length secondarySpacing,
                       BrokenArrowsApp app) {
            this.position = position;
            this.size = size;
            this.axis = axis;
            this.spacingMode = spacingMode;
            this.primarySpacing = primarySpacing;
            this.secondarySpacing = secondarySpacing;
            this.app = app;
            buttonBuilder = new ButtonGui.Builder(app);
        }

        public Builder setButtonBuilder(ButtonGui.Builder builder) {
            this.buttonBuilder = builder;
            return this;
        }

        public Builder buttonBuilder(Consumer<ButtonGui.Builder> action) {
            action.accept(buttonBuilder);
            return this;
        }


        public MenuGui build() {
            return new MenuGui(position, size,
                    axis,
                    spacingMode,
                    primarySpacing, secondarySpacing,
                    buttonBuilder,
                    app);
        }

    }

}
