package me.cometkaizo.brokenarrows.jar;

import java.lang.reflect.Field;
import java.util.Map;

public interface FieldSupplier {

    Field get(Map<String, Class<?>> classes) throws ClassNotFoundException, NoSuchFieldException;

    static FieldSupplier of(Field field) {
        return classes -> field;
    }
    static FieldSupplier of(ClassSupplier classSup, String name) {
        return classes -> classSup.get(classes).getField(name);
    }

}
