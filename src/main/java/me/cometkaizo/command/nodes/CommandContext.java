package me.cometkaizo.command.nodes;

import me.cometkaizo.brokenarrows.Diagnostic;
import me.cometkaizo.util.Triterator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandContext {
    public final Triterator<String> args;
    public final Map<String, Object> parsedArgs = new HashMap<>(1);
    public final List<Diagnostic> problems = new ArrayList<>(0);

    public CommandContext(String[] args) {
        this.args = Triterator.of(args);
    }
    public Object arg(String key) {
        return parsedArgs.get(key);
    }
}
