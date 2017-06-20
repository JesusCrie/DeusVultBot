package com.jesus_crie.deusvult.manager;

import com.jesus_crie.deusvult.commands.Command;

import java.util.ArrayList;
import java.util.List;

public class CommandManager {

    private static List<Command> commands = new ArrayList<>();

    public static void registerCommands(Command... cmds) {
        for (Command c : cmds)
            commands.add(c);
    }

    public static Command getCommand(String alias) {
        alias.toLowerCase();
        for (Command c : commands)
            if (c.getAliases().contains(alias))
                return c;
        return null;
    }
}
