package com.jesus_crie.silverdragon.command.commands;

import com.jesus_crie.silverdragon.SilverDragon;
import com.jesus_crie.silverdragon.command.Command;
import com.jesus_crie.silverdragon.command.CommandPattern;
import com.jesus_crie.silverdragon.response.ResponseBuilder;
import com.jesus_crie.silverdragon.utils.StringUtils;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class StopCommand extends Command {

    public StopCommand() {
        super("stop",
                "Stoppe le bot, utilisable uniquement par le créateur.",
                null,
                AccessLevel.CREATOR,
                Context.calculate(Context.PRIVATE, Context.MAIN_GUILD));

        registerPatterns(
                new CommandPattern(null, (e, a) -> onCommand(e), "")
        );
    }

    private boolean onCommand(MessageReceivedEvent e) {
        ResponseBuilder.create(e.getMessage())
                .setTitle("Shutting down...")
                .setIcon(StringUtils.ICON_BED)
                .send(e.getChannel()).complete();

        SilverDragon.instance().shutdown();
        return true;
    }
}
