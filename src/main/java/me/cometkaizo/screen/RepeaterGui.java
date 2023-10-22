package me.cometkaizo.screen;

import me.cometkaizo.brokenarrows.BrokenArrowsApp;
import me.cometkaizo.util.MathUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static me.cometkaizo.util.MathUtils.*;

public class RepeaterGui extends ContainerGui {
    protected Axis axis;
    protected SpacingMode spacingMode;
    protected Length primarySpacing;
    protected Length secondarySpacing;

    public RepeaterGui(double x, double y,
                       double width, double height,
                       List<Gui> components,
                       Axis axis,
                       SpacingMode spacingMode, Length primarySpacing, Length secondarySpacing,
                       BrokenArrowsApp app) {
        this(Coordinate.relative(x, y), Coordinate.relative(width, height), components, axis, spacingMode, primarySpacing, secondarySpacing, app);
    }

    public RepeaterGui(int x, int y,
                       int width, int height,
                       List<Gui> components,
                       Axis axis,
                       SpacingMode spacingMode, Length primarySpacing, Length secondarySpacing,
                       BrokenArrowsApp app) {
        this(Coordinate.abs(x, y), Coordinate.abs(width, height), components, axis, spacingMode, primarySpacing, secondarySpacing, app);
    }

    public RepeaterGui(Coordinate position, Coordinate size,
                       List<Gui> components,
                       Axis axis,
                       SpacingMode spacingMode, Length primarySpacing, Length secondarySpacing,
                       BrokenArrowsApp app) {
        super(position, size, app);
        this.axis = axis;
        this.spacingMode = spacingMode;
        this.primarySpacing = primarySpacing;
        this.secondarySpacing = secondarySpacing;
        components.forEach(this::addComponent);
        updateComponentPositions();
    }

    @Override
    public void onScreenResized() {
        super.onScreenResized();
        reupdateComponentPositions();
    }

    @Override
    public void update() {
        super.update();
        updateComponentPositions();
    }

    public void updateComponentPositions() {
        spacingMode.updatePos(components,
                axis,
                this::width, this::height,
                this::getPrimarySpacing,
                this::getSecondarySpacing);
    }

    public void reupdateComponentPositions() {
        spacingMode.reupdatePos(components,
                axis,
                this::width, this::height,
                this::getPrimarySpacing,
                this::getSecondarySpacing);
    }

    protected double getPrimarySpacing() {
        return primarySpacing.per((int) axis.get(app.panel().getWidth(), app.panel().getHeight()),
                (int) axis.get(app.settings().defaultWidth, app.settings().defaultHeight));
    }

    protected double getSecondarySpacing() {
        return secondarySpacing.per((int) axis.opposite().get(app.panel().getWidth(), app.panel().getHeight()),
                (int) axis.opposite().get(app.settings().defaultWidth, app.settings().defaultHeight));
    }

    public enum Axis {
        HORIZONTAL, VERTICAL;

        public double getSize(Gui component) {
            return this == HORIZONTAL ? component.width() : component.height();
        }

        public double get(double x, double y) {
            return this == HORIZONTAL ? x : y;
        }
        public Supplier<Double> get(Supplier<Double> x, Supplier<Double> y) {
            return this == HORIZONTAL ? x : y;
        }

        public double getPos(Gui component) {
            return this == HORIZONTAL ? component.xRaw() : component.yRaw();
        }

        public void setPos(Gui component, Supplier<Double> amt) {
            if (this == HORIZONTAL) component.setX(Length.relative(amt));
            else component.setY(Length.relative(amt));
        }

        public void setPos(Gui component, Supplier<Double> primary, Supplier<Double> secondary) {
            if (this == HORIZONTAL) {
                component.setX(Length.relative(primary));
                component.setY(Length.relative(secondary));
            } else {
                component.setY(Length.relative(primary));
                component.setX(Length.relative(secondary));
            }
        }

        public Axis opposite() {
            return this == HORIZONTAL ? VERTICAL : HORIZONTAL;
        }
    }
    public interface SpacingMode {
        SpacingMode START = (components, axis, width, height, primarySpacing, secondarySpacing) -> {
            if (components.isEmpty()) return;
            Supplier<Double> pSize = axis.get(width, height);
            Supplier<Double> sSize = axis.opposite().get(width, height);
            Supplier<Double> pDeltaAbs = () -> 0D;
            Supplier<Double> pDelta = () -> 0D;
            Supplier<Double> sDelta = () -> 0D;
            Gui tallestElementInRow = null;

            for (var component : components) {
                tallestElementInRow = MathUtils.max(component, tallestElementInRow, c -> axis.opposite().getSize(c));

                if (pSize.get() - pDeltaAbs.get() < axis.getSize(component)) {
                    pDeltaAbs = pDelta = () -> 0D;
                    Gui finalTallestElementInRow = tallestElementInRow;
                    sDelta = sum(sDelta, () -> axis.opposite().getSize(finalTallestElementInRow), secondarySpacing);
                    tallestElementInRow = null;
                }
                if (sSize.get() - sDelta.get() < axis.opposite().getSize(component)) break;
                axis.setPos(component, pDelta, sDelta);

                pDeltaAbs = sum(pDeltaAbs, () -> axis.getSize(component), primarySpacing);
                pDelta = divide(pDeltaAbs, pSize);
            }
        };
        SpacingMode END = (components, axis, width, height, primarySpacing, secondarySpacing) -> {
            Supplier<Double> pSize = axis.get(width, height);
            Supplier<Double> sSize = axis.opposite().get(width, height);
            Supplier<Double> pDeltaAbs = pSize;
            Supplier<Double> pDelta;
            Supplier<Double> sDelta = () -> 0D;
            Gui tallestElementInRow = null;
            for (var component : components) {
                pDeltaAbs = subtract(pDeltaAbs, () -> axis.getSize(component));
                pDelta = divide(pDeltaAbs, pSize);
                tallestElementInRow = MathUtils.max(component, tallestElementInRow, c -> axis.opposite().getSize(c));

                if (pDeltaAbs.get() < axis.getSize(component)) {
                    pDeltaAbs = pSize;
                    pDelta = () -> 1D;
                    Gui finalTallestElementInRow = tallestElementInRow;
                    sDelta = sum(sDelta, () -> axis.opposite().getSize(finalTallestElementInRow), secondarySpacing);
                    tallestElementInRow = null;
                }
                if (sSize.get() - sDelta.get() < axis.opposite().getSize(component)) break;

                axis.setPos(component, pDelta, sDelta);

                pDeltaAbs = subtract(pDeltaAbs, primarySpacing);
            }
        };
        SpacingMode CENTER = new SpacingMode() {
            @Override
            public void updatePos(List<Gui> components, Axis axis, Supplier<Double> width, Supplier<Double> height, Supplier<Double> primarySpacing, Supplier<Double> secondarySpacing) {
                Supplier<Double> pSize = axis.get(width, height);
                Supplier<Double> sSize = axis.opposite().get(width, height);
                Supplier<Double> pDeltaAbs = () -> 0D;
                Supplier<Double> pDelta = () -> 0D;
                Supplier<Double> sDelta = () -> 0D;
                Gui tallestElementInRow = null;
                List<Gui> row = new ArrayList<>(Math.min(10, components.size()));
                for (int index = 0; index < components.size(); index++) {
                    Gui component = components.get(index);
                    tallestElementInRow = MathUtils.max(component, tallestElementInRow, c -> axis.opposite().getSize(c));

                    axis.setPos(component, pDelta, sDelta);

                    pDeltaAbs = sum(pDeltaAbs, () -> axis.getSize(component), primarySpacing);
                    pDelta = divide(pDeltaAbs, pSize);
                    row.add(component);

                    if (pSize.get() - pDeltaAbs.get() < axis.getSize(component) || index == components.size() - 1) {
                        Supplier<Double> finalPDeltaAbs = pDeltaAbs;
                        Supplier<Double> extraDelta = () -> (pSize.get() - finalPDeltaAbs.get()) / 2;
                        for (Gui c : row) {
                            double pos = axis.getPos(c);
                            axis.setPos(c, () -> pos + extraDelta.get());
                        }
                        pDeltaAbs = pDelta = () -> 0D;

                        Gui finalTallestElementInRow = tallestElementInRow;
                        sDelta = sum(sDelta, () -> axis.opposite().getSize(finalTallestElementInRow), secondarySpacing);

                        tallestElementInRow = null;
                        row.clear();
                    }
                    if (sSize.get() - sDelta.get() < axis.opposite().getSize(component)) break;
                }
            }

            @Override
            public void reupdatePos(List<Gui> components, Axis axis, Supplier<Double> width, Supplier<Double> height, Supplier<Double> primarySpacing, Supplier<Double> secondarySpacing) {
                updatePos(components, axis, width, height, primarySpacing, secondarySpacing);
            }
        };
        SpacingMode EQUAL = new SpacingMode() {
            @Override
            public void updatePos(List<Gui> components, Axis axis, Supplier<Double> width, Supplier<Double> height, Supplier<Double> primarySpacing, Supplier<Double> secondarySpacing) {
                if (components.isEmpty()) return;
                Supplier<Double> pSize = axis.get(width, height);
                Supplier<Double> sSize = axis.opposite().get(width, height);
                Supplier<Double> pDeltaAbs = () -> 0D;
                Supplier<Double> sDelta = () -> 0D;
                Gui tallestElementInRow = null;
                List<Gui> row = new ArrayList<>(Math.min(10, components.size()));

                for (int index = 0; index < components.size(); index++) {
                    Gui component = components.get(index);
                    tallestElementInRow = MathUtils.max(component, tallestElementInRow, c -> axis.opposite().getSize(c));

                    if (pSize.get() - pDeltaAbs.get() < axis.getSize(component)) {
                        updateRow(row, axis, pSize, pDeltaAbs, sDelta, primarySpacing, secondarySpacing);
                        pDeltaAbs = () -> 0D;

                        Gui finalTallestElementInRow = tallestElementInRow;
                        sDelta = sum(sDelta, () -> axis.opposite().getSize(finalTallestElementInRow), secondarySpacing);
                        tallestElementInRow = null;
                        row.clear();
                    }
                    if (sSize.get() - sDelta.get() < axis.opposite().getSize(component)) break;

                    pDeltaAbs = sum(pDeltaAbs, () -> axis.getSize(component));
                    row.add(component);

                    if (index == components.size() - 1) {
                        updateRow(row, axis, pSize, pDeltaAbs, sDelta, primarySpacing, secondarySpacing);
                    }

                    pDeltaAbs = sum(pDeltaAbs, primarySpacing);
                }
            }

            private void updateRow(List<Gui> row, Axis axis, Supplier<Double> pSize, Supplier<Double> pDeltaAbs, Supplier<Double> sDelta, Supplier<Double> primarySpacing, Supplier<Double> secondarySpacing) {
                if (row.size() > 1) {
                    Supplier<Double> extraSpace = subtract(pSize, pDeltaAbs);
                    updateRow(row, axis, pSize, sDelta, () -> primarySpacing.get() + extraSpace.get() / (row.size() - 1), secondarySpacing);
                }
            }

            void updateRow(List<Gui> row, Axis axis, Supplier<Double> pSize, Supplier<Double> sDelta, Supplier<Double> primarySpacing, Supplier<Double> secondarySpacing) {
                Supplier<Double> deltaAbs = () -> 0D;
                Supplier<Double> delta = () -> 0D;
                Gui tallestElementInRow = null;

                for (var component : row) {
                    tallestElementInRow = MathUtils.max(component, tallestElementInRow, c -> axis.opposite().getSize(c));

                    axis.setPos(component, delta, sDelta);

                    deltaAbs = sum(deltaAbs, () -> axis.getSize(component), primarySpacing);
                    delta = divide(deltaAbs, pSize);
                }
            }
        };
        void updatePos(List<Gui> components,
                       Axis axis,
                       Supplier<Double> width, Supplier<Double> height,
                       Supplier<Double> primarySpacing, Supplier<Double> secondarySpacing);
        default void reupdatePos(List<Gui> components,
                       Axis axis,
                       Supplier<Double> width, Supplier<Double> height,
                       Supplier<Double> primarySpacing, Supplier<Double> secondarySpacing) {

        }
    }
}
