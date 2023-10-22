package me.cometkaizo.command.arguments;

import java.util.function.Predicate;

public class StringArgument extends Argument {


    public StringArgument(String name) {
        super(name);
    }
    public StringArgument(String name, Predicate<Object> requirement) {
        super(name, requirement);
    }

    @Override
    public boolean accepts(String string) {
        return requirement.test(string);
    }

    @Override
    public String translate(String string) throws IllegalArgumentException {
        if (!accepts(string)) throw new IllegalArgumentException();
        return string;
    }
}
