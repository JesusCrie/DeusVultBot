package com.jesus_crie.deusvult.command.commands;

import com.jesus_crie.deusvult.command.Command;
import com.jesus_crie.deusvult.command.CommandPattern;
import com.jesus_crie.deusvult.response.ResponseBuilder;
import com.jesus_crie.deusvult.utils.S;
import com.jesus_crie.deusvult.utils.StringUtils;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.List;

public class PingCommand extends Command {

    public PingCommand() {
        super("ping",
                S.COMMAND_PING_HELP.get(),
                null,
                AccessLevel.EVERYONE,
                Context.calculate(Context.EVERYWHERE));

        registerPatterns(
                new CommandPattern(null, this::onCommand)
        );
    }

    private boolean onCommand(MessageReceivedEvent event, List<Object> args) {
        ResponseBuilder.create(event.getMessage())
                .setTitle(S.COMMAND_PING_AWAIT.get())
                .setIcon(StringUtils.ICON_TERMINAL)
                .send(event.getChannel())
                .queue(m -> {
                    long ping = m.getCreationTime().toInstant().toEpochMilli() - event.getMessage().getCreationTime().toInstant().toEpochMilli();
                    m.editMessage(
                            ResponseBuilder.create(event.getMessage())
                            .setTitle(S.COMMAND_PING_PING.format(ping))
                            .setIcon(StringUtils.ICON_TERMINAL)
                            .build()
                    ).queue();
                });

        return true;
    }
}
