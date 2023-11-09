package me.cometkaizo.command.nodes;

import me.cometkaizo.util.StringUtils;

public class LiteralCommandNode extends CommandNode {

    private final String literal;

    @Override
    protected boolean matchImpl(CommandContext context) {
        var arg = context.args.safeNext();
        return arg.isPresent() && literal.equals(arg.get());
    }

    public LiteralCommandNode(Builder builder) {
        super(builder);
        this.literal = builder.literal;
    }

    @Override
    public String toString() {
        return StringUtils.format("""
                LiteralCommandNode{
                    literal: {}
                }""", literal);
    }

    @Override
    public String toPrettyString() {
        return literal;
    }

    public static class Builder extends CommandNode.Builder {

        protected final String literal;

        public Builder(String literal) {
            this.literal = literal;
        }

        @Override
        public LiteralCommandNode build() {
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
}
