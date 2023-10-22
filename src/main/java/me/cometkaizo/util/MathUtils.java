package me.cometkaizo.util;

import java.util.function.Function;
import java.util.function.Supplier;

public class MathUtils {

    public static double clamp(double num, double low, double high) {
        return num < low ? low : Math.min(num, high);
    }

    public static float clamp(float num, float low, float high) {
        return num < low ? low : Math.min(num, high);
    }

    public static int clamp(int num, int low, int high) {
        return num < low ? low : Math.min(num, high);
    }

    public static double lerp(double percent, double start, double end) {
        return start + percent * (end - start);
    }
    public static float lerp(float percent, float start, float end) {
        return start + percent * (end - start);
    }

    public static double roundToMultipleOf(double num, double mul) {
        return mul*(Math.round(num/mul));
    }

    public static float roundToMultipleOf(float num, float mul) {
        return mul*(Math.round(num/mul));
    }

    public static boolean almostEquals(double a, double b, double error) {
        return a >= b - error && a <= b + error;
    }


    @SafeVarargs
    public static Supplier<Double> sum(Supplier<Double>... suppliers) {
        if (suppliers.length == 0) return () -> 0D;
        if (suppliers.length == 1) return suppliers[0];
        return () -> {
            double result = 0;
            for (var s : suppliers) result += s.get();
            return result;
        };
    }
    @SafeVarargs
    public static Supplier<Double> subtract(Supplier<Double> amtSup, Supplier<Double>... suppliers) {
        if (suppliers.length == 0) return amtSup;
        if (suppliers.length == 1) return () -> amtSup.get() - suppliers[0].get();
        return () -> {
            double result = amtSup.get();
            for (var s : suppliers) result -= s.get();
            return result;
        };
    }
    @SafeVarargs
    public static Supplier<Double> multiply(Supplier<Double> amtSup, Supplier<Double>... suppliers) {
        if (suppliers.length == 0) return () -> 1D;
        if (suppliers.length == 1) return () -> amtSup.get() * suppliers[0].get();
        return () -> {
            double result = amtSup.get();
            for (var s : suppliers) result *= s.get();
            return result;
        };
    }
    @SafeVarargs
    public static Supplier<Double> divide(Supplier<Double> amtSup, Supplier<Double>... suppliers) {
        if (suppliers.length == 0) return () -> 1D;
        if (suppliers.length == 1) return () -> amtSup.get() / suppliers[0].get();
        return () -> {
            double result = amtSup.get();
            for (var s : suppliers) result /= s.get();
            return result;
        };
    }


    private MathUtils() {
        throw new AssertionError("No MathUtils instances for you!");
    }

    public static <T, C extends Comparable<C>> T max(T a, T b, Function<T, C> valueGetter) {
        if (a == null) return b;
        if (b == null) return a;
        return valueGetter.apply(a).compareTo(valueGetter.apply(b)) > 0 ? a : b;
    }
    public static <T, C extends Comparable<C>> T min(T a, T b, Function<T, C> valueGetter) {
        if (a == null || b == null) return null;
        return valueGetter.apply(a).compareTo(valueGetter.apply(b)) < 0 ? a : b;
    }
}
