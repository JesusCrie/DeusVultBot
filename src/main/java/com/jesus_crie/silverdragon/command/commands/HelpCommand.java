package com.jesus_crie.silverdragon.command.commands;

import com.jesus_crie.silverdragon.command.Command;
import com.jesus_crie.silverdragon.command.CommandPattern;
import com.jesus_crie.silverdragon.manager.CommandManager;
import com.jesus_crie.silverdragon.response.ResponsePage;
import com.jesus_crie.silverdragon.response.ResponsePaginable;
import com.jesus_crie.silverdragon.response.ResponseUtils;
import com.jesus_crie.silverdragon.utils.F;
import com.jesus_crie.silverdragon.utils.StringUtils;
import com.jesus_crie.silverdragon.utils.T;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.jesus_crie.silverdragon.utils.S.f;

public class HelpCommand extends Command {

    public HelpCommand() {
        super("help",
                "Affiche l'aide des commandes.",
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
        ResponsePaginable help = ResponsePaginable.create(m, "Aide")
                .setIcon(StringUtils.ICON_HELP)
                .setTimeout(T.calc(1, TimeUnit.MINUTES));

        ResponsePage index = new ResponsePage("List des commandes");
        List<String> cmds = new ArrayList<>();
        cmds.add("**1.** Index");
        for (int i = 0; i < CommandManager.getCommands().size(); i++)
            cmds.add(F.bold(i + 2 + ".") + " " + StringUtils.capitalize(CommandManager.getCommands().get(i).getName()));
        index.addFields(ResponseUtils.createList("", false, cmds));

        help.addPage(index);

        CommandManager.getCommands().forEach(c ->
            help.addPage(new ResponsePage(f("Commande %s", c.getName()))
                    .setDescription(c.getDescription())
                    .addField("Rang requis", c.getAccessLevel().name(), true)
                    .addField("Contexte requis", StringUtils.stringifyContext(c.getContext()), true)
                    .addField("Usage", F.codeBlock("yaml", String.join("\n", c.collectNotices())), false))
        );

        return help;
    }
}
