package com.jesus_crie.deusvult.command.commands;

import com.jesus_crie.deusvult.command.Command;
import com.jesus_crie.deusvult.command.CommandPattern;
import com.jesus_crie.deusvult.manager.CommandManager;
import com.jesus_crie.deusvult.response.ResponsePage;
import com.jesus_crie.deusvult.response.ResponsePaginable;
import com.jesus_crie.deusvult.response.ResponseUtils;
import com.jesus_crie.deusvult.utils.F;
import com.jesus_crie.deusvult.utils.S;
import com.jesus_crie.deusvult.utils.StringUtils;
import com.jesus_crie.deusvult.utils.T;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

public class HelpCommand extends Command {

    public HelpCommand() {
        super("help",
                S.COMMAND_HELP_HELP.get(),
                null,
                AccessLevel.EVERYONE,
                Context.calculate(Context.EVERYWHERE));

        registerPatterns(
                new CommandPattern(new CommandPattern.Argument[] {
                        CommandPattern.Argument.INTEGER
                }, this::onCommandPage, "<page>"),

                new CommandPattern(new CommandPattern.Argument[] {
                        CommandPattern.Argument.WORD_ONLY
                }, this::onCommandSpecific, "<commande>"),

                new CommandPattern(null, (e, a) -> onCommandGeneric(e), "")
        );
    }

    private boolean onCommandGeneric(MessageReceivedEvent event) {
        getHelp(event.getMessage()).send(event.getChannel(), event.getAuthor());
        return true;
    }

    private boolean onCommandSpecific(MessageReceivedEvent event, List<Object> args) {
        String query = (String) args.get(0);
        List<Command> commands = CommandManager.getCommands();
        int index = 0;
        for (int i = 0; i < commands.size(); i++) {
            if (commands.get(i).getName().equalsIgnoreCase(query)) {
                index = i;
                break;
            }
        }

        getHelp(event.getMessage()).setPage(index).send(event.getChannel(), event.getAuthor());
        return true;
    }

    private boolean onCommandPage(MessageReceivedEvent event, List<Object> args) {
        getHelp(event.getMessage()).setPage(((int) args.get(0) - 1)).send(event.getChannel(), event.getAuthor());
        return true;
    }

    private ResponsePaginable getHelp(Message m) {
        ResponsePaginable help = ResponsePaginable.create(m, S.COMMAND_HELP_TITLE.get())
                .setIcon(StringUtils.ICON_HELP)
                .setTimeout(T.calc(0, 1));

        ResponsePage index = new ResponsePage(S.COMMAND_HELP_COMMAND_LIST.get());
        List<String> cmds = new ArrayList<>();
        cmds.add("**1.** Index");
        for (int i = 0; i < CommandManager.getCommands().size(); i++)
            cmds.add(F.bold(i + 2 + ".") + " " + StringUtils.capitalize(CommandManager.getCommands().get(i).getName()));
        index.addFields(ResponseUtils.createList("", false, cmds));

        help.addPage(index);

        CommandManager.getCommands().forEach(c ->
            help.addPage(new ResponsePage(S.COMMAND_HELP_COMMAND_TITLE.format(c.getName()))
                    .setDescription(c.getDescription())
                    .addField(S.COMMAND_HELP_ACCESS_LEVEL.get(), c.getAccessLevel().name(), true)
                    .addField(S.COMMAND_HELP_CONTEXT.get(), StringUtils.stringifyContext(c.getContext()), true)
                    .addField(S.COMMAND_HELP_USAGE.get(), F.codeBlock("yaml", String.join("\n", c.collectNotices())), false))
        );

        return help;
    }
}
