package me.cometkaizo.brokenarrows.jar;

import java.lang.reflect.Method;
import java.util.*;

public interface MethodSupplier {

    Method get(Map<String, Class<?>> classes) throws ClassNotFoundException, NoSuchMethodException;

    static MethodSupplier of(Method method) {
        Objects.requireNonNull(method);
        return classes -> method;
    }
    static MethodSupplier of(ClassSupplier classSup, String name) {
        return of(classSup, name, new Class[0]);
    }
    static MethodSupplier of(ClassSupplier classSup, String name, Class<?>... argTypes) {
        return classes -> classSup.get(classes).getMethod(name, argTypes);
    }
    static MethodSupplier of(ClassSupplier classSup, String name, ClassSupplier... argTypes) {
        return classes -> {
            List<Class<?>> list = new ArrayList<>(argTypes.length);
            for (ClassSupplier s : argTypes) list.add(s.get(classes));
            return classSup.get(classes).getMethod(name, list.toArray(Class[]::new));
        };
    }

}
