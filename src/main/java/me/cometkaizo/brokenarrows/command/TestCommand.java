package me.cometkaizo.brokenarrows.command;

import me.cometkaizo.command.nodes.CommandContext;
import me.cometkaizo.command.nodes.CommandNode;

public class TestCommand {
    public static final CommandNode COMMAND = CommandNode.build(n -> n
            .literal("test")
            .argStr("string")
            .executes(TestCommand::test));

    private static void test(CommandContext context) {
        System.out.println(context.arg("string"));
    }
}
