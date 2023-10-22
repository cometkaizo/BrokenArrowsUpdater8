package me.cometkaizo.brokenarrows.jar;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public interface ConstructorSupplier {

    Constructor<?> get(Map<String, Class<?>> classes) throws ClassNotFoundException, NoSuchMethodException;

    static ConstructorSupplier of(Constructor<?> constructor) {
        Objects.requireNonNull(constructor);
        return classes -> constructor;
    }

    static ConstructorSupplier of(ClassSupplier classSup, Class<?>... argTypes) {
        return classes -> classSup.get(classes).getConstructor(argTypes);
    }
    static ConstructorSupplier of(ClassSupplier classSup, ClassSupplier... argTypes) {
        return classes -> {
            List<Class<?>> list = new ArrayList<>(argTypes.length);
            for (ClassSupplier s : argTypes) list.add(s.get(classes));
            return classSup.get(classes).getConstructor(list.toArray(Class[]::new));
        };
    }

}
