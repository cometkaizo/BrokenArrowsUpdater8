package me.cometkaizo.command.nodes;

import me.cometkaizo.util.StringUtils;

class EmptyCommandNode extends SoftCommandNode {

    protected EmptyCommandNode(EmptyCommandNodeBuilder builder) {
        super(builder);
    }

    @Override
    protected boolean accepts() {
        return true;
    }

    @Override
    public String toString() {
        return StringUtils.format("""
                EmptyCommandNode{
                    level: {}
                }""", level);
    }
}
