package me.cometkaizo.brokenarrows.jar;

import java.util.Map;
import java.util.Objects;

public interface ClassSupplier {

    Class<?> get(Map<String, Class<?>> classes) throws ClassNotFoundException;

    static ClassSupplier of(Class<?> clazz) {
        Objects.requireNonNull(clazz);
        return classes -> clazz;
    }
    static ClassSupplier of(String name) {
        return classes -> classes.get(name);
    }

}
