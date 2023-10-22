package me.cometkaizo.io.data;

import me.cometkaizo.registry.Registry;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public class DataTypes {

    public static final Registry<Object, Data.Type<?>> DATA_TYPES = new Registry<>();

    public static final Supplier<Data.Type<?>> BOOLEAN = DATA_TYPES.register("boolean", o -> BooleanData.TYPE);
    public static final Supplier<Data.Type<?>> INT = DATA_TYPES.register("int", o -> IntData.TYPE);
    public static final Supplier<Data.Type<?>> DOUBLE = DATA_TYPES.register("double", o -> DoubleData.TYPE);
    public static final Supplier<Data.Type<?>> STRING = DATA_TYPES.register("string", o -> StringData.TYPE);
    public static final Supplier<Data.Type<?>> COMPOUND = DATA_TYPES.register("compound", o -> CompoundData.TYPE);
    public static final Supplier<Data.Type<?>> LIST = DATA_TYPES.register("list", o -> ListData.TYPE);
    public static final Supplier<Data.Type<?>> END = DATA_TYPES.register("end", o -> DataEnd.TYPE);

}
