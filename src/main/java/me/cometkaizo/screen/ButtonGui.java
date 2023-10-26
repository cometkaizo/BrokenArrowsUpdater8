package me.cometkaizo.screen;

import me.cometkaizo.brokenarrows.BrokenArrowsApp;
import me.cometkaizo.screen.color.ColorSource;
import me.cometkaizo.util.ShapeUtils;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.RectangularShape;
import java.util.function.Consumer;

public class ButtonGui extends Gui {
    public GuiText text, hoverText, pressText;
    public Consumer<? super ButtonGui> action;
    public RectangularShape shape;
    public GuiBackground background, hoverBackground, pressBackground;
    public Border border, hoverBorder, pressBorder;
    protected boolean hovered, pressed;

    public ButtonGui(double x, double y,
                     double width, double height,
                     GuiText text, GuiText hoverText, GuiText pressText,
                     RectangularShape shape,
                     GuiBackground background, GuiBackground hoverBackground, GuiBackground pressBackground,
                     Border border, Border hoverBorder, Border pressBorder,
                     Consumer<? super ButtonGui> action,
                     BrokenArrowsApp app) {
        this(Coordinate.relative(x, y), Coordinate.relative(width, height), text, hoverText, pressText, shape, background, hoverBackground, pressBackground, border, hoverBorder, pressBorder, action, app);
    }
    public ButtonGui(int x, int y,
                     int width, int height,
                     GuiText text, GuiText hoverText, GuiText pressText,
                     RectangularShape shape,
                     GuiBackground background, GuiBackground hoverBackground, GuiBackground pressBackground,
                     Border border, Border hoverBorder, Border pressBorder,
                     Consumer<? super ButtonGui> action,
                     BrokenArrowsApp app) {
        this(Coordinate.abs(x, y), Coordinate.abs(width, height), text, hoverText, pressText, shape, background, hoverBackground, pressBackground, border, hoverBorder, pressBorder, action, app);
    }
    public ButtonGui(Coordinate position, Coordinate size,
                     GuiText text, GuiText hoverText, GuiText pressText,
                     RectangularShape shape,
                     GuiBackground background, GuiBackground hoverBackground, GuiBackground pressBackground,
                     Border border, Border hoverBorder, Border pressBorder,
                     Consumer<? super ButtonGui> action,
                     BrokenArrowsApp app) {
        super(position, size, app);
        this.text = text;
        this.hoverText = hoverText;
        this.pressText = pressText;
        this.shape = shape;
        this.background = background;
        this.hoverBackground = hoverBackground;
        this.pressBackground = pressBackground;
        this.border = border;
        this.hoverBorder = hoverBorder;
        this.pressBorder = pressBorder;
        this.action = action;
    }

    @Override
    public void mouseReleased(int button, double x, double y) {
        super.mouseReleased(button, x, y);
        pressed = false;
        if (contains(x, y)) onPressed();
    }

    @Override
    public void mousePressed(int button, double x, double y) {
        super.mousePressed(button, x, y);
        if (contains(x, y)) pressed = true;
    }

    @Override
    public void mouseMoved(double x, double y) {
        super.mouseMoved(x, y);
        hovered = contains(x, y);
        if (!hovered) pressed = false;
    }

    @Override
    public void mouseDragged(double x, double y) {
        super.mouseDragged(x, y);
        pressed = hovered = contains(x, y);
    }

    public void onPressed() {
        action.accept(this);
    }

    @Override
    public void render(FullResRenderer r, Graphics2D g) {
        super.render(r, g);
        renderBorder(r, g);
        renderBackground(r, g);
        renderText(r, g);
    }

    public void renderBorder(FullResRenderer r, Graphics2D g) {
        Border currentBorder;

        if (pressed) currentBorder = pressBorder;
        else if (hovered) currentBorder = hoverBorder;
        else currentBorder = border;

        if (currentBorder != null) currentBorder.render(r, g, shape, x(), y(), width(), height(), app);
    }

    public void renderBackground(FullResRenderer r, Graphics2D g) {
        if (pressed) renderPressBackground(r, g);
        else if (hovered) renderHoverBackground(r, g);
        else renderRegularBackground(r, g);
    }

    private void renderRegularBackground(FullResRenderer r, Graphics2D g) {
        boolean subtractBorder = border != null && border.mode != BorderMode.OUTER;
        int borderWidth = subtractBorder ? border.getWidth(app) : -1;
        int actualX = subtractBorder ? r.toScreenX(x()) + borderWidth : r.toScreenX(x());
        int actualY = subtractBorder ? r.toScreenY(y()) + borderWidth : r.toScreenY(y());
        int actualWidth = subtractBorder ? r.toScreenWidth(width()) - borderWidth * 2 : r.toScreenWidth(width());
        int actualHeight = subtractBorder ? r.toScreenHeight(height()) - borderWidth * 2 : r.toScreenHeight(height());
        background.render(r, g, shape, actualX, actualY, actualWidth, actualHeight);
    }

    private void renderHoverBackground(FullResRenderer r, Graphics2D g) {
        boolean subtractBorder = hoverBorder != null && hoverBorder.mode != BorderMode.OUTER;
        int hoverBorderWidth = subtractBorder ? hoverBorder.getWidth(app) : -1;
        int actualX = subtractBorder ? r.toScreenX(x()) + hoverBorderWidth : r.toScreenX(x());
        int actualY = subtractBorder ? r.toScreenY(y()) + hoverBorderWidth : r.toScreenY(y());
        int actualWidth = subtractBorder ? r.toScreenWidth(width()) - hoverBorderWidth * 2 : r.toScreenWidth(width());
        int actualHeight = subtractBorder ? r.toScreenHeight(height()) - hoverBorderWidth * 2 : r.toScreenHeight(height());
        hoverBackground.render(r, g, shape, actualX, actualY, actualWidth, actualHeight);
    }

    private void renderPressBackground(FullResRenderer r, Graphics2D g) {
        boolean subtractBorder = pressBorder != null && pressBorder.mode != BorderMode.OUTER;
        int hoverBorderWidth = subtractBorder ? pressBorder.getWidth(app) : -1;
        int actualX = subtractBorder ? r.toScreenX(x()) + hoverBorderWidth : r.toScreenX(x());
        int actualY = subtractBorder ? r.toScreenY(y()) + hoverBorderWidth : r.toScreenY(y());
        int actualWidth = subtractBorder ? r.toScreenWidth(width()) - hoverBorderWidth * 2 : r.toScreenWidth(width());
        int actualHeight = subtractBorder ? r.toScreenHeight(height()) - hoverBorderWidth * 2 : r.toScreenHeight(height());
        pressBackground.render(r, g, shape, actualX, actualY, actualWidth, actualHeight);
    }

    public void renderText(FullResRenderer r, Graphics2D g) {
        double centerX = x() + width() / 2;
        double centerY = y() + height() / 2;
        if (pressed) pressText.render(r, g, centerX, centerY, -0.5, 0.25);
        else if (hovered) hoverText.render(r, g, centerX, centerY, -0.5, 0.25);
        else text.render(r, g, centerX, centerY, -0.5, 0.25);
    }

    public GuiBackground getCurrentBackground() {
        return pressed ? pressBackground : hovered ? hoverBackground : background;
    }

    public void setAllText(GuiText text) {
        this.text = this.hoverText = this.pressText = text;
    }
    public void setAllText(String text) {
        setText(text);
        setHoverText(text);
        setPressText(text);
    }
    public void setText(String text) {
        this.text.text = text;
    }
    public void setHoverText(String text) {
        this.hoverText.text = text;
    }
    public void setPressText(String text) {
        this.pressText.text = text;
    }

    public static class Border {
        protected Length width;
        protected Color color;
        protected BorderMode mode;

        public Border(Length width, Color color, BorderMode mode) {
            this.width = width;
            this.color = color;
            this.mode = mode;
        }

        public void render(FullResRenderer r, Graphics2D g, RectangularShape shape, double x, double y, double width, double height, BrokenArrowsApp app) {
            if (this.width == null || getWidth(app) == 0) return;
            g.setColor(color);
            Area area;
            r.setShape(shape, x, y, width, height);
            if (mode == BorderMode.OUTER) {
                area = ShapeUtils.grow(shape, getWidth(app));
            } else area = new Area(shape);
            subtractBackground(r, area, shape, x, y, width, height, app);
            g.fill(area);
        }

        protected int getWidth(BrokenArrowsApp app) {
            return width.abs(Math.max(app.panel().getWidth(), app.panel().getHeight()), Math.max(app.settings().defaultWidth, app.settings().defaultHeight));
        }

        public void subtractBackground(FullResRenderer r, Area area, RectangularShape shape, double x, double y, double width, double height, BrokenArrowsApp app) {
            int borderWidth = getWidth(app);
            int actualX = mode == BorderMode.OUTER ? r.toScreenX(x) : r.toScreenX(x) + borderWidth;
            int actualY = mode == BorderMode.OUTER ? r.toScreenY(y) : r.toScreenY(y) + borderWidth;
            int actualWidth = mode == BorderMode.OUTER ? r.toScreenWidth(width) : r.toScreenWidth(width) - borderWidth * 2;
            int actualHeight = mode == BorderMode.OUTER ? r.toScreenHeight(height) : r.toScreenHeight(height) - borderWidth * 2;

            RectangularShape backgroundShape = (RectangularShape) shape.clone();
            r.setShape(backgroundShape, actualX, actualY, actualWidth, actualHeight);
            area.subtract(new Area(backgroundShape));
        }


        public Length width() {
            return width;
        }
        public Color color() {
            return color;
        }
        public BorderMode mode() {
            return mode;
        }

        public void setWidth(Length width) {
            this.width = width;
        }

        public void setColor(Color color) {
            this.color = color;
        }

        public void setMode(BorderMode mode) {
            this.mode = mode;
        }
    }

    public enum BorderMode {
        OUTER, INNER
    }


    public static class Builder /*implements ObjectBuilder<ButtonGui>*/ {
        public final BrokenArrowsApp app;
        public Coordinate position, size;
        public GuiText text, hoverText, pressText;
        public GuiBackground background, hoverBackground, pressBackground;
        public Border border, hoverBorder, pressBorder;
        public Consumer<? super ButtonGui> action;
        public RectangularShape shape;

        public Builder(BrokenArrowsApp app) {
            this.app = app;
        }

        public Builder setAllText(GuiText text) {
            this.text = this.hoverText = this.pressText = text;
            return this;
        }
        public Builder setAllTextSize(int size) {
            return setTextSize(size).setHoverTextSize(size).setPressTextSize(size);
        }
        public Builder setAllText(String text) {
            return setText(text).setHoverText(text).setPressText(text);
        }
        public Builder setAllBackgrounds(GuiBackground background) {
            this.background = this.hoverBackground = this.pressBackground = background;
            return this;
        }
        public Builder setAllBorders(Border border) {
            this.border = this.hoverBorder = this.pressBorder = border;
            return this;
        }

        public ButtonGui.Builder setPos(Coordinate pos) {
            this.position = pos;
            return this;
        }
        public ButtonGui.Builder setSize(Coordinate size) {
            this.size = size;
            return this;
        }
        public ButtonGui.Builder setText(GuiText text) {
            this.text = text;
            return this;
        }
        public ButtonGui.Builder setHoverText(GuiText text) {
            this.hoverText = text;
            return this;
        }
        public ButtonGui.Builder setPressText(GuiText text) {
            this.pressText = text;
            return this;
        }
        public ButtonGui.Builder setText(String text) {
            this.text.text = text;
            return this;
        }
        public ButtonGui.Builder setHoverText(String text) {
            this.hoverText.text = text;
            return this;
        }
        public ButtonGui.Builder setPressText(String text) {
            this.pressText.text = text;
            return this;
        }
        public ButtonGui.Builder setTextSize(int size) {
            this.text.setSize(size);
            return this;
        }
        public ButtonGui.Builder setHoverTextSize(int size) {
            this.hoverText.setSize(size);
            return this;
        }
        public ButtonGui.Builder setPressTextSize(int size) {
            this.pressText.setSize(size);
            return this;
        }
        public ButtonGui.Builder setHoverTextStyle(Font font, ColorSource color) {
            if (hoverText == null || hoverText == text) hoverText = new GuiText(text.text(), text.font(), text.color(), text.deltaX(), text.deltaY(), text.deltaXFactor(), text.deltaYFactor());
            if (font != null) hoverText.setFont(font);
            if (color != null) hoverText.setColor(color);
            return this;
        }
        public ButtonGui.Builder setPressTextStyle(Font font, ColorSource color) {
            if (pressText == null || pressText == text) pressText = new GuiText(text.text(), text.font(), text.color(), text.deltaX(), text.deltaY(), text.deltaXFactor(), text.deltaYFactor());
            if (font != null) pressText.setFont(font);
            if (color != null) pressText.setColor(color);
            return this;
        }
        public ButtonGui.Builder setShape(RectangularShape shape) {
            this.shape = shape;
            return this;
        }
        public ButtonGui.Builder setBackground(GuiBackground background) {
            this.background = background;
            return this;
        }
        public ButtonGui.Builder setHoverBackground(GuiBackground background) {
            this.hoverBackground = background;
            return this;
        }
        public ButtonGui.Builder setPressBackground(GuiBackground background) {
            this.pressBackground = background;
            return this;
        }
        public ButtonGui.Builder setBorder(ButtonGui.Border border) {
            this.border = border;
            return this;
        }
        public ButtonGui.Builder setHoverBorder(ButtonGui.Border border) {
            this.hoverBorder = border;
            return this;
        }
        public ButtonGui.Builder setPressBorder(ButtonGui.Border border) {
            this.pressBorder = border;
            return this;
        }
        public ButtonGui.Builder setAction(Consumer<? super ButtonGui> action) {
            this.action = action;
            return this;
        }

        /*@Override*/
        public ButtonGui build() {
            return new ButtonGui(position, size, text, hoverText, pressText, shape, background, hoverBackground, pressBackground, border, hoverBorder, pressBorder, action, app);
        }

        protected ButtonGui buildOrDefault(Builder builder) {
            final Coordinate size = this.size == null ? builder.size : this.size;
            final GuiText text = this.text == null ? builder.text : this.text,
                    hoverText = this.hoverText == null ? builder.hoverText : this.hoverText,
                    pressText = this.pressText == null ? builder.pressText : this.pressText;
            final RectangularShape shape = this.shape == null ? builder.shape : this.shape;
            final GuiBackground background = this.background == null ? builder.background : this.background,
                    hoverBackground = this.hoverBackground == null ? builder.hoverBackground : this.hoverBackground,
                    pressBackground = this.pressBackground == null ? builder.pressBackground : this.pressBackground;
            final Border border = this.border == null ? builder.border : this.border,
                    hoverBorder = this.hoverBorder == null ? builder.hoverBorder : this.hoverBorder,
                    pressBorder = this.pressBorder == null ? builder.pressBorder : this.pressBorder;
            final Consumer<? super ButtonGui> action = this.action == null ? builder.action : this.action;
            return new ButtonGui(Coordinate.zero(), size, text, hoverText, pressText, shape, background, hoverBackground, pressBackground, border, hoverBorder, pressBorder, action, app);
        }
    }

}
