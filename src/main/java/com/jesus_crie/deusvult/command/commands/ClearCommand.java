package com.jesus_crie.deusvult.command.commands;

import com.jesus_crie.deusvult.command.Command;
import com.jesus_crie.deusvult.command.CommandPattern;
import com.jesus_crie.deusvult.exception.CommandException;
import com.jesus_crie.deusvult.response.ResponseBuilder;
import com.jesus_crie.deusvult.response.ResponseUtils;
import com.jesus_crie.deusvult.utils.S;
import com.jesus_crie.deusvult.utils.StringUtils;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.List;

public class ClearCommand extends Command {

    public ClearCommand() {
        super("clear",
                S.COMMAND_CLEAR_HELP.get(),
                null,
                AccessLevel.CREATOR,
                Context.calculate(Context.ALL_GUILD));

        registerPatterns(
                new CommandPattern(new CommandPattern.Argument[] {
                        CommandPattern.Argument.INTEGER
                }, this::onCommandGlobal),

                new CommandPattern(new CommandPattern.Argument[] {
                        CommandPattern.Argument.USER,
                        CommandPattern.Argument.INTEGER
                }, this::onCommandUser),

                new CommandPattern(new CommandPattern.Argument[] {
                        CommandPattern.Argument.forString("old"),
                        CommandPattern.Argument.INTEGER
                }, this::onCommandOld)
        );
    }

    private boolean onCommandGlobal(MessageReceivedEvent event, List<Object> args) {
        if ((int) args.get(0) <= 1) {
            ResponseUtils.errorMessage(event.getMessage(), new CommandException(S.COMMAND_CLEAR_ERROR_NOT_ENOUGH.format(args.get(0))))
                    .send(event.getChannel()).queue();
            return true;
        }

        for (int howMany = (int) args.get(0); howMany > 0; howMany -= 100) {
            List<Message> toDelete = event.getTextChannel().getHistory().retrievePast(howMany > 100 ? 100 : howMany).complete();
            event.getTextChannel().deleteMessages(toDelete).complete();
        }

        ResponseBuilder.create(event.getMessage())
                .setTitle(S.COMMAND_CLEAR_TITLE.format((int) args.get(0)))
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
                .setTitle(S.COMMAND_CLEAR_TITLE_USER.format((int) args.get(1), user.getName()))
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
                .setTitle(S.COMMAND_CLEAR_TITLE.format((int) args.get(1)))
                .setIcon(StringUtils.ICON_INFO)
                .send(event.getChannel()).queue();

        return true;
    }
}
