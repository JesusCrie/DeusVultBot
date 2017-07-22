package com.jesus_crie.deusvult.manager;

import com.jesus_crie.deusvult.command.Command;

import java.util.Arrays;
import java.util.List;

public class CommandManager {

    private static List<Command> commands;

    public static void registerCommands(Command... cmds) {
        commands = Arrays.asList(cmds);
    }

    public static Command getCommand(String name) {
        return commands.stream()
                .filter(c -> c.getName().equals(name))
                .findAny()
                .orElse(null);
    }
}
