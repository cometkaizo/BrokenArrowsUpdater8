package me.cometkaizo.screen;

import me.cometkaizo.util.TriConsumer;
import me.cometkaizo.util.TriPredicate;

import java.awt.*;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public interface PixelModifier extends BiConsumer<Color[][], TriConsumer<Color[][], Integer, Integer>> {
    static PixelModifier ofColor(Predicate<Color> filter) {
        return (screen, task) -> {
            for (int x = 0; x < screen.length; x++) {
                for (int y = 0; y < screen[x].length; y++) {
                    if (filter.test(screen[x][y])) task.accept(screen, x, y);
                }
            }
        };
    }
    static PixelModifier ofPos(BiPredicate<Integer, Integer> filter) {
        return (screen, task) -> {
            for (int x = 0; x < screen.length; x++) {
                for (int y = 0; y < screen[x].length; y++) {
                    if (filter.test(x, y)) task.accept(screen, x, y);
                }
            }
        };
    }
    static PixelModifier ofPixel(TriPredicate<Color, Integer, Integer> filter) {
        return (screen, task) -> {
            for (int x = 0; x < screen.length; x++) {
                for (int y = 0; y < screen[x].length; y++) {
                    if (filter.test(screen[x][y], x, y)) task.accept(screen, x, y);
                }
            }
        };
    }

    static PixelModifier ofRange(int startX, int startY, int endX, int endY) {
        return (screen, task) -> {
            for (int x = Math.max(startX, 0); x < Math.min(endX, screen.length); x++) {
                for (int y = Math.max(startY, 0); y < Math.min(endY, screen[x].length); y++) {
                    task.accept(screen, x, y);
                }
            }
        };
    }
}
