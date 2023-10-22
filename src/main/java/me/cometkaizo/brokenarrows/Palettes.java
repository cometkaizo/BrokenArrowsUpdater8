package me.cometkaizo.brokenarrows;

import me.cometkaizo.registry.Registry;
import me.cometkaizo.screen.color.BluePalette;
import me.cometkaizo.screen.color.DarkPalette;
import me.cometkaizo.screen.color.Palette;

import java.util.function.Supplier;

public class Palettes {

    public static final Registry<Object, Palette> PALETTES = new Registry<>();

    public static final Supplier<DarkPalette> DARK = PALETTES.register("dark", o -> new DarkPalette());
    public static final Supplier<BluePalette> BLUE = PALETTES.register("blue", o -> new BluePalette());

}
