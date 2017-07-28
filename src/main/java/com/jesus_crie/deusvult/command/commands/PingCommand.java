package com.jesus_crie.deusvult.command.commands;

import com.jesus_crie.deusvult.command.Command;
import com.jesus_crie.deusvult.command.CommandPattern;
import com.jesus_crie.deusvult.response.ResponseBuilder;
import com.jesus_crie.deusvult.utils.StringUtils;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import static com.jesus_crie.deusvult.utils.S.*;

public class PingCommand extends Command {

    public PingCommand() {
        super("ping",
                "Calcul le ping du bot entre l'envoi de la commande et l'envoi de la réponse.",
                null,
                AccessLevel.EVERYONE,
                Context.calculate(Context.EVERYWHERE));

        registerPatterns(
                new CommandPattern(null, (e, a) -> onCommand(e), "")
        );
    }

    private boolean onCommand(MessageReceivedEvent event) {
        ResponseBuilder.create(event.getMessage())
                .setTitle("Calcul en cours...")
                .setIcon(StringUtils.ICON_TERMINAL)
                .send(event.getChannel())
                .queue(m -> {
                    long ping = m.getCreationTime().toInstant().toEpochMilli() - event.getMessage().getCreationTime().toInstant().toEpochMilli();
                    m.editMessage(
                            ResponseBuilder.create(event.getMessage())
                            .setTitle(f("Ping: %s ms", ping))
                            .setIcon(StringUtils.ICON_TERMINAL)
                            .build()
                    ).queue();
                });

        return true;
    }
}
