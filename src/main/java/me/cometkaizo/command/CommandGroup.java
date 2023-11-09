package me.cometkaizo.command;

import me.cometkaizo.brokenarrows.Diagnostic;
import me.cometkaizo.command.nodes.CommandContext;
import me.cometkaizo.command.nodes.CommandNode;

import java.util.ArrayList;
import java.util.List;

public class CommandGroup {

    private final List<CommandNode> commands;

    public CommandGroup(CommandNode... commands) {
        this.commands = List.of(commands);
    }

    public List<Diagnostic> execute(String input) {
        List<Diagnostic> allProblems = new ArrayList<>(1);
        validateInput(input, allProblems);
        if (!allProblems.isEmpty()) return allProblems;

        // getting inputted command information
        String[] args = getInputParts(input);

        for (var command : commands) {
            var context = new CommandContext(args);
            boolean success = command.match(context);
            if (success) return context.problems;
            else allProblems.addAll(context.problems);
        }
        return allProblems;
    }

    private static String[] getInputParts(String input) {
        return input.trim().split(" ");
    }

    private static void validateInput(String input, List<Diagnostic> problems) {
        if (input == null) problems.add(new Diagnostic.Error(null, "Command cannot be null"));
        else if (input.isBlank()) problems.add(new Diagnostic.Error(null, "Command cannot be blank"));
    }

}
