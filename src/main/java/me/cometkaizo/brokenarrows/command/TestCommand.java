package me.cometkaizo.brokenarrows.command;

import me.cometkaizo.command.nodes.Command;

import java.util.List;
import java.util.Optional;

public class TestCommand extends Command {
    public TestCommand() {
        rootNode.executes(this::test);
    }

    private void test() {
        ProcessHandle.allProcesses()
                .forEach(process -> System.out.println(processDetails(process)));
    }

    private static String processDetails(ProcessHandle process) {
        return String.format("%8d %8s %10s %26s %-40s",
                process.pid(),
                text(process.parent().map(ProcessHandle::pid)),
                text(process.info().user()),
                text(process.info().startInstant()),
                text(process.info().commandLine()));
    }

    private static String text(Optional<?> optional) {
        return optional.map(Object::toString).orElse("-");
    }
    @Override
    public List<String> getNames() {
        return List.of("test");
    }
}
