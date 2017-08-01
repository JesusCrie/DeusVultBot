package com.jesus_crie.silverdragon.command.commands;

import com.jesus_crie.silverdragon.command.Command;
import com.jesus_crie.silverdragon.command.CommandPattern;
import com.jesus_crie.silverdragon.response.ResponseBuilder;
import com.jesus_crie.silverdragon.utils.StringUtils;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import static com.jesus_crie.silverdragon.utils.S.f;

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
