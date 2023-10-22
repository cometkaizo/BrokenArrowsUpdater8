package me.cometkaizo.brokenarrows.command;

import me.cometkaizo.command.arguments.StringArgument;
import me.cometkaizo.command.nodes.ArgumentCommandNodeBuilder;
import me.cometkaizo.command.nodes.Command;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class TestCommand extends Command {
    public TestCommand() {
        rootNode.then(new ArgumentCommandNodeBuilder(new StringArgument("path"))).executes(this::test);
    }

    private void test() {
        String path = (String) parsedArgs.get("path");
        System.out.println(path);
        File file = new File(path);
        System.out.println(file.exists());
        System.out.println(file.getAbsolutePath());
        System.out.println(Arrays.toString(file.listFiles()));
    }

    @Override
    public List<String> getNames() {
        return List.of("test");
    }
}
