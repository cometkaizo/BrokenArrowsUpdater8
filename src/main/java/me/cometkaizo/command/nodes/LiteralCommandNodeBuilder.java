package me.cometkaizo.command.nodes;

import me.cometkaizo.util.StringUtils;

public class LiteralCommandNodeBuilder extends CommandNodeBuilder {

    protected final String literal;

    public LiteralCommandNodeBuilder(String literal) {
        this.literal = literal;
    }

    @Override
    protected LiteralCommandNode build() {
        return new LiteralCommandNode(this);
    }

    @Override
    public String toString() {
        return StringUtils.format("""
                LiteralCommandNodeBuilder{
                    literal: {}
                }""", literal);
    }
}
