package me.cometkaizo.command.nodes;

import me.cometkaizo.util.StringUtils;

class LiteralCommandNode extends CommandNode {

    private final String literal;

    protected boolean accepts(String string) {
        return string.equals(literal);
    }

    @Override
    protected void executeFunctionality() {

    }

    public LiteralCommandNode(LiteralCommandNodeBuilder builder) {
        super(builder);
        this.literal = builder.literal;
    }

    @Override
    public String toString() {
        return StringUtils.format("""
                LiteralCommandNode{
                    literal: {},
                    level: {}
                }""", literal, level);
    }

    @Override
    public String toPrettyString() {
        return literal;
    }
}
