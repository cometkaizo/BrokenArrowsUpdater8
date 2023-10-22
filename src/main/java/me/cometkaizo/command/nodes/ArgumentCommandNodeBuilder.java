package me.cometkaizo.command.nodes;

import me.cometkaizo.command.arguments.Argument;
import me.cometkaizo.util.StringUtils;

public class ArgumentCommandNodeBuilder extends CommandNodeBuilder {

    protected final Argument argument;

    public ArgumentCommandNodeBuilder(Argument argument) {
        this.argument = argument;
    }

    @Override
    protected ArgumentCommandNode build() {
        return new ArgumentCommandNode(this);
    }

    @Override
    public String toString() {
        return StringUtils.format("""
                ArgumentCommandNodeBuilder{
                    argument: {},
                    level: {}
                }""", argument, level);
    }
}
