package me.cometkaizo.command.nodes;

import me.cometkaizo.util.StringUtils;

import java.util.function.Supplier;

class ConditionalCommandNode extends SoftCommandNode {

    protected final Supplier<Boolean> condition;
    protected final String name;

    public ConditionalCommandNode(ConditionalCommandNodeBuilder builder) {
        super(builder);
        this.condition = builder.condition;
        this.name = builder.name;
    }

    @Override
    protected boolean accepts() {
        return condition.get();
    }

    @Override
    public String toString() {
        return StringUtils.format("""
                ConditionalCommandNode{
                    condition: {},
                    level: {}
                }""", condition, level);
    }

    @Override
    public String toPrettyString() {
        return name;
    }
}
