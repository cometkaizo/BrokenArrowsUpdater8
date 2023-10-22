package me.cometkaizo.screen;

import java.util.function.Supplier;

public class Length {
    protected Supplier<Double> percentSup;
    protected Supplier<Double> directPercentSup;
    protected Double percent;
    protected Integer pixels;
    protected boolean abs;

    private Length(double length, boolean abs) {
        this.percent = length;
        this.abs = abs;
    }

    private Length(int length) {
        this.pixels = length;
        this.abs = true;
    }

    public Length(Supplier<Double> lengthSup, boolean abs) {
        this.percentSup = lengthSup;
        this.abs = abs;
    }

    public Length(Supplier<Double> lengthSup) {
        this.directPercentSup = lengthSup;
        this.abs = true;
    }

    public static Length abs(int length) {
        return new Length(length);
    }

    public static Length abs(double length) {
        return new Length(length, true);
    }

    public static Length relative(double length) {
        return new Length(length, false);
    }

    public static Length direct(Supplier<Double> lengthSup) {
        return new Length(lengthSup);
    }

    public static Length relative(Supplier<Double> lengthSup) {
        return new Length(lengthSup, false);
    }

    public static Length abs(Supplier<Double> lengthSup) {
        return new Length(lengthSup, true);
    }

    public static Length zero() {
        return Length.abs(0);
    }

    public int abs(int reference, int defaultReference) {
        Double percent = directPercentSup != null ? directPercentSup.get() : percentSup != null ? percentSup.get() : this.percent;
        if (abs) {
            if (pixels != null) return pixels;
            return (int) (percent * defaultReference);
        }
        return (int) (percent * reference);
    }

    public double per(int reference, int defaultReference) {
        if (directPercentSup != null) return directPercentSup.get();
        Double percent = percentSup != null ? percentSup.get() : this.percent;
        if (abs) {
            if (pixels != null) return pixels / (double)reference;
            return percent * defaultReference / reference;
        }
        return percent;
    }

    public boolean isAbsolute() {
        return abs;
    }

    public void set(double length) {
        pixels = null;
        percent = length;
    }
    public void set(int length) {
        percent = null;
        pixels = length;
    }

    public Length multiplied(double factor) {
        return pixels != null ? new Length((int) (pixels * factor)) : new Length((percentSup != null ? percentSup.get() : this.percent) * factor, abs);
    }

    @Override
    public String toString() {
        return "Length{" +
                "percentSup=" + percentSup +
                ", directPercentSup=" + directPercentSup +
                ", percent=" + percent +
                ", pixels=" + pixels +
                ", abs=" + abs +
                '}';
    }
}
