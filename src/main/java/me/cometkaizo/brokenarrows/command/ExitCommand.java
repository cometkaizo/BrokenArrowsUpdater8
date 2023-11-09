package me.cometkaizo.brokenarrows.command;

import me.cometkaizo.command.nodes.CommandNode;

public class ExitCommand {
    public static final CommandNode COMMAND = CommandNode.build(n -> n.literal("exit").executes(c -> exit()));

    private static void exit() {
        System.exit(130);
    }
}
