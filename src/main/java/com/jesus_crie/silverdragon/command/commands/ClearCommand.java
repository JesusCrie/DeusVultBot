package com.jesus_crie.silverdragon.command.commands;

import com.jesus_crie.silverdragon.command.Command;
import com.jesus_crie.silverdragon.command.CommandPattern;
import com.jesus_crie.silverdragon.exception.CommandException;
import com.jesus_crie.silverdragon.response.ResponseBuilder;
import com.jesus_crie.silverdragon.response.ResponseUtils;
import com.jesus_crie.silverdragon.utils.StringUtils;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.List;

import static com.jesus_crie.silverdragon.utils.S.f;

public class ClearCommand extends Command {

    public ClearCommand() {
        super("clear",
                "Efface un certain nombre de messages dans le channel.",
                null,
                AccessLevel.ADMIN,
                Context.calculate(Context.ALL_GUILD));

        registerPatterns(
                new CommandPattern(new CommandPattern.Argument[] {
                        CommandPattern.Argument.INTEGER
                }, this::onCommandGlobal, "<nombre>"),

                new CommandPattern(new CommandPattern.Argument[] {
                        CommandPattern.Argument.USER,
                        CommandPattern.Argument.INTEGER
                }, this::onCommandUser, "<membre> <nombre>"),

                new CommandPattern(new CommandPattern.Argument[] {
                        CommandPattern.Argument.forString("old"),
                        CommandPattern.Argument.INTEGER
                }, this::onCommandOld, "old <nombre>")
        );
    }

    private boolean onCommandGlobal(MessageReceivedEvent event, List<Object> args) {
        if ((int) args.get(0) <= 1) {
            ResponseUtils.errorMessage(event.getMessage(), new CommandException(f("%s n'est pas un nombre de message correct.", args.get(0))))
                    .send(event.getChannel()).queue();
            return true;
        }

        for (int howMany = (int) args.get(0); howMany > 0; howMany -= 100) {
            List<Message> toDelete = event.getTextChannel().getHistory().retrievePast(howMany > 100 ? 100 : howMany).complete();
            event.getTextChannel().deleteMessages(toDelete).complete();
        }

        ResponseBuilder.create(event.getMessage())
                .setTitle(f("%s messages ont été éffacés.", args.get(0)))
                .setIcon(StringUtils.ICON_INFO)
                .send(event.getChannel()).queue();

        return true;
    }

    private boolean onCommandUser(MessageReceivedEvent event, List<Object> args) {
        User user = (User) args.get(0);
        int count = (int) args.get(1);

        for (Message m : event.getTextChannel().getIterableHistory()) {
            if (count <= 0)
                break;
            if (m.getAuthor().equals(user)) {
                m.delete().queue();
                count--;
            }
        }

        ResponseBuilder.create(event.getMessage())
                .setTitle(f("%s messages de %s sont en train d'être supprimés", args.get(1), user.getName()))
                .setIcon(StringUtils.ICON_INFO)
                .send(event.getChannel()).queue();

        return true;
    }

    private boolean onCommandOld(MessageReceivedEvent event, List<Object> args) {

        for (int howMany = (int) args.get(1); howMany > 0; howMany -= 100) {
            List<Message> toDelete = event.getTextChannel().getHistory().retrievePast(howMany > 100 ? 100 : howMany).complete();
            toDelete.forEach(m -> m.delete().complete());
        }

        ResponseBuilder.create(event.getMessage())
                .setTitle(f("%s messages ont été éffacés.", args.get(1)))
                .setIcon(StringUtils.ICON_INFO)
                .send(event.getChannel()).queue();

        return true;
    }
}
