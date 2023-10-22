package me.cometkaizo.screen;

import me.cometkaizo.brokenarrows.BrokenArrowsApp;
import me.cometkaizo.screen.color.ColorSource;

import java.awt.*;
import java.awt.geom.RectangularShape;
import java.util.ArrayList;
import java.util.function.Consumer;

public class MenuGui extends RepeaterGui {
    protected Coordinate buttonSize;
    protected GuiText buttonText, buttonHoverText, buttonPressText;
    protected RectangularShape buttonShape;
    protected GuiBackground buttonBackground, buttonHoverBackground, buttonPressBackground;
    protected ButtonGui.Border buttonBorder, buttonHoverBorder, buttonPressBorder;
    protected Consumer<? super ButtonGui> buttonAction;

    public MenuGui(Coordinate position, Coordinate size,
                   Axis axis,
                   SpacingMode spacingMode, Length primarySpacing, Length secondarySpacing,
                   Coordinate buttonSize,
                   GuiText buttonText, GuiText buttonHoverText, GuiText buttonPressText,
                   RectangularShape buttonShape,
                   GuiBackground buttonBackground, GuiBackground buttonHoverBackground, GuiBackground buttonPressBackground,
                   ButtonGui.Border buttonBorder, ButtonGui.Border buttonHoverBorder, ButtonGui.Border buttonPressBorder,
                   Consumer<? super ButtonGui> buttonAction,
                   BrokenArrowsApp app) {
        super(position, size, new ArrayList<>(0), axis, spacingMode, primarySpacing, secondarySpacing, app);
        this.buttonSize = buttonSize;
        this.buttonText = buttonText;
        this.buttonHoverText = buttonHoverText;
        this.buttonPressText = buttonPressText;
        this.buttonShape = buttonShape;
        this.buttonBackground = buttonBackground;
        this.buttonHoverBackground = buttonHoverBackground;
        this.buttonPressBackground = buttonPressBackground;
        this.buttonBorder = buttonBorder;
        this.buttonHoverBorder = buttonHoverBorder;
        this.buttonPressBorder = buttonPressBorder;
        this.buttonAction = buttonAction;
    }

    public ButtonGui addButton(ButtonBuilder builder) {
        ButtonGui button = build(builder);
        addNestedComponent(button);
        return button;
    }

    protected ButtonGui build(ButtonBuilder builder) {
        final Coordinate size = builder.size == null ? buttonSize : builder.size;
        final GuiText text = builder.text == null ? buttonText : builder.text,
                hoverText = builder.hoverText == null ? buttonHoverText : builder.hoverText,
                pressText = builder.pressText == null ? buttonHoverText : builder.pressText;
        final RectangularShape shape = builder.shape == null ? buttonShape : builder.shape;
        final GuiBackground background = builder.background == null ? buttonBackground : builder.background,
                hoverBackground = builder.hoverBackground == null ? buttonHoverBackground : builder.hoverBackground,
                pressBackground = builder.pressBackground == null ? buttonPressBackground : builder.pressBackground;
        final ButtonGui.Border border = builder.border == null ? buttonBorder : builder.border,
                hoverBorder = builder.hoverBorder == null ? buttonHoverBorder : builder.hoverBorder,
                pressBorder = builder.pressBorder == null ? buttonPressBorder : builder.pressBorder;
        final Consumer<? super ButtonGui> action = builder.action == null ? buttonAction : builder.action;
        return new ButtonGui(Coordinate.zero(), size, text, hoverText, pressText, shape, background, hoverBackground, pressBackground, border, hoverBorder, pressBorder, action, app);
    }

    @SuppressWarnings("unused")
    public static class ButtonBuilder {
        protected Coordinate size;
        protected GuiText text, hoverText, pressText;
        protected RectangularShape shape;
        protected GuiBackground background, hoverBackground, pressBackground;
        protected ButtonGui.Border border, hoverBorder, pressBorder;
        protected Consumer<? super ButtonGui> action = button -> {};

        public ButtonBuilder setSize(Coordinate size) {
            this.size = size;
            return this;
        }
        public ButtonBuilder setAllText(GuiText text) {
            return setText(text).setHoverText(text).setPressText(text);
        }
        public ButtonBuilder setText(GuiText text) {
            this.text = text;
            return this;
        }
        public ButtonBuilder setHoverText(GuiText text) {
            this.hoverText = text;
            return this;
        }
        public ButtonBuilder setPressText(GuiText text) {
            this.pressText = text;
            return this;
        }
        public ButtonBuilder setHoverTextStyle(Font font, ColorSource color) {
            if (hoverText == null) hoverText = new GuiText(text.text(), text.font(), text.color(), text.deltaX(), text.deltaY(), text.deltaXFactor(), text.deltaYFactor());
            if (font != null) hoverText.setFont(font);
            if (color != null) hoverText.setColor(color);
            return this;
        }
        public ButtonBuilder setPressTextStyle(Font font, ColorSource color) {
            if (pressText == null) pressText = new GuiText(text.text(), text.font(), text.color(), text.deltaX(), text.deltaY(), text.deltaXFactor(), text.deltaYFactor());
            if (font != null) pressText.setFont(font);
            if (color != null) pressText.setColor(color);
            return this;
        }
        public ButtonBuilder setShape(RectangularShape shape) {
            this.shape = shape;
            return this;
        }
        public ButtonBuilder setBackground(GuiBackground background) {
            this.background = background;
            return this;
        }
        public ButtonBuilder setHoverBackground(GuiBackground background) {
            this.hoverBackground = background;
            return this;
        }
        public ButtonBuilder setPressBackground(GuiBackground background) {
            this.pressBackground = background;
            return this;
        }
        public ButtonBuilder setBorder(ButtonGui.Border border) {
            this.border = border;
            return this;
        }
        public ButtonBuilder setHoverBorder(ButtonGui.Border border) {
            this.hoverBorder = border;
            return this;
        }
        public ButtonBuilder setPressBorder(ButtonGui.Border border) {
            this.pressBorder = border;
            return this;
        }
        public ButtonBuilder setAction(Consumer<? super ButtonGui> action) {
            this.action = action;
            return this;
        }

    }

    @SuppressWarnings("unused")
    public static class Builder {
        protected Coordinate position, size;
        protected Axis axis;
        protected SpacingMode spacingMode;
        protected Length primarySpacing, secondarySpacing;
        protected BrokenArrowsApp app;
        protected Coordinate buttonSize;
        protected GuiText buttonText, buttonHoverText, buttonPressText;
        protected RectangularShape buttonShape;
        protected GuiBackground buttonBackground, buttonHoverBackground, buttonPressBackground;
        protected ButtonGui.Border buttonBorder, buttonHoverBorder, buttonPressBorder;
        protected Consumer<? super ButtonGui> buttonAction;

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
        }

        public Builder setButtonSize(Coordinate size) {
            this.buttonSize = size;
            return this;
        }
        public Builder setButtonText(GuiText text) {
            this.buttonText = text;
            return this;
        }
        public Builder setButtonHoverText(GuiText text) {
            this.buttonHoverText = text;
            return this;
        }
        public Builder setButtonPressText(GuiText text) {
            this.buttonPressText = text;
            return this;
        }
        public Builder setButtonShape(RectangularShape shape) {
            this.buttonShape = shape;
            return this;
        }
        public Builder setAllButtonBackgrounds(GuiBackground background) {
            return setButtonBackground(background).setButtonHoverBackground(background).setButtonPressBackground(background);
        }
        public Builder setButtonBackground(GuiBackground background) {
            this.buttonBackground = background;
            return this;
        }
        public Builder setButtonHoverBackground(GuiBackground background) {
            this.buttonHoverBackground = background;
            return this;
        }
        public Builder setButtonPressBackground(GuiBackground background) {
            this.buttonPressBackground = background;
            return this;
        }
        public Builder setButtonBorder(ButtonGui.Border border) {
            this.buttonBorder = border;
            return this;
        }
        public Builder setButtonHoverBorder(ButtonGui.Border border) {
            this.buttonHoverBorder = border;
            return this;
        }
        public Builder setButtonPressBorder(ButtonGui.Border border) {
            this.buttonPressBorder = border;
            return this;
        }
        public Builder setButtonAction(Consumer<? super ButtonGui> action) {
            this.buttonAction = action;
            return this;
        }


        public MenuGui build() {
            return new MenuGui(position, size,
                    axis,
                    spacingMode,
                    primarySpacing, secondarySpacing,
                    buttonSize,
                    buttonText, buttonHoverText, buttonPressText,
                    buttonShape,
                    buttonBackground, buttonHoverBackground, buttonPressBackground,
                    buttonBorder, buttonHoverBorder, buttonPressBorder,
                    buttonAction,
                    app);
        }

    }

}
