package me.cometkaizo.screen;

import me.cometkaizo.brokenarrows.BrokenArrowsApp;

import java.util.Objects;
import java.util.function.Supplier;

public class Coordinate {
    protected Length x;
    protected Length y;

    private Coordinate(int x, int y) {
        this(Length.abs(x), Length.abs(y));
    }
    private Coordinate(double x, double y, boolean abs) {
        this(abs ? Length.abs(x) : Length.relative(x), abs ? Length.abs(y) : Length.relative(y));
    }
    private Coordinate(Length x, Length y) {
        Objects.requireNonNull(x);
        Objects.requireNonNull(y);
        this.x = x;
        this.y = y;
    }

    public static Coordinate abs(int x, int y) {
        return new Coordinate(x, y);
    }
    public static Coordinate abs(Length x, int y) {
        return new Coordinate(x, Length.abs(y));
    }
    public static Coordinate abs(int x, Length y) {
        return new Coordinate(Length.abs(x), y);
    }
    public static Coordinate abs(double x, double y) {
        return new Coordinate(x, y, true);
    }
    public static Coordinate abs(Length x, double y) {
        return new Coordinate(x, Length.abs(y));
    }
    public static Coordinate abs(double x, Length y) {
        return new Coordinate(Length.abs(x), y);
    }
    public static Coordinate abs(Supplier<Double> xSup, Supplier<Double> ySup) {
        return new Coordinate(Length.abs(xSup), Length.abs(ySup));
    }
    public static Coordinate relative(double x, double y) {
        return new Coordinate(x, y, false);
    }
    public static Coordinate of(Length x, Length y) {
        return new Coordinate(x, y);
    }
    public static Coordinate direct(Supplier<Double> xSup, Supplier<Double> ySup) {
        return new Coordinate(Length.direct(xSup), Length.direct(ySup));
    }
    public static Coordinate relative(Length x, double y) {
        return new Coordinate(x, Length.relative(y));
    }
    public static Coordinate relative(double x, Length y) {
        return new Coordinate(Length.relative(x), y);
    }

    public static Coordinate relative(Supplier<Double> xSup, Supplier<Double> ySup) {
        return new Coordinate(Length.relative(xSup), Length.relative(ySup));
    }

    public static Coordinate zero() {
        return relative(0D, 0D);
    }

    public static Coordinate one() {
        return relative(1D, 1D);
    }

    public Length x() {
        return x;
    }
    public Length y() {
        return y;
    }

    public double xPer(int reference, int defaultReference) {
        return x.per(reference, defaultReference);
    }

    public double xPer(BrokenArrowsApp app) {
        return xPer(app.panel().getWidth(), app.settings().defaultWidth);
    }

    public double yPer(int reference, int defaultReference) {
        return y.per(reference, defaultReference);
    }

    public double yPer(BrokenArrowsApp app) {
        return yPer(app.panel().getHeight(), app.settings().defaultHeight);
    }

    public double xAbs(int reference, int defaultReference) {
        return x.abs(reference, defaultReference);
    }

    public double xAbs(BrokenArrowsApp app) {
        return xAbs(app.panel().getWidth(), app.settings().defaultWidth);
    }

    public double yAbs(int reference, int defaultReference) {
        return y.abs(reference, defaultReference);
    }

    public double yAbs(BrokenArrowsApp app) {
        return yAbs(app.panel().getHeight(), app.settings().defaultHeight);
    }

    public void setX(double x) {
        this.x.set(x);
    }

    public void setX(int x) {
        this.x.set(x);
    }

    public void setX(Length x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y.set(y);
    }

    public void setY(int y) {
        this.y.set(y);
    }

    public void setY(Length y) {
        this.y = y;
    }

    public boolean isXAbsolute() {
        return x.isAbsolute();
    }
    public boolean isYAbsolute() {
        return y.isAbsolute();
    }
}
