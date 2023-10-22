package me.cometkaizo.brokenarrows.command;

import me.cometkaizo.command.nodes.Command;

import java.util.List;

public class ExitCommand extends Command {
    public ExitCommand() {
        rootNode.executes(this::exit);
    }

    private void exit() {
        System.exit(130);
    }

    @Override
    public List<String> getNames() {
        return List.of("exit");
    }
}
