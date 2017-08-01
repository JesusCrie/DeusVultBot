package com.jesus_crie.silverdragon.command.commands;

import com.jesus_crie.silverdragon.command.Command;
import com.jesus_crie.silverdragon.command.CommandPattern;
import com.jesus_crie.silverdragon.response.ResponseBuilder;
import com.jesus_crie.silverdragon.response.ResponseUtils;
import com.jesus_crie.silverdragon.utils.StringUtils;
import com.jesus_crie.silverdragon.utils.T;
import net.dv8tion.jda.core.JDAInfo;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class InfoCommand extends Command {

    public InfoCommand() {
        super("info",
                "Affiche des infos sur le bot.",
                null,
                AccessLevel.EVERYONE,
                Context.calculate(Context.EVERYWHERE));

        registerPatterns(
                new CommandPattern(null, (e, a) -> onCommand(e), "")
        );
    }

    private boolean onCommand(MessageReceivedEvent event) {
        ResponseBuilder.create(event.getMessage())
                .setTitle(event.getJDA().getSelfUser().getName())
                .setIcon(event.getJDA().getSelfUser().getEffectiveAvatarUrl())
                .setThumbnail(event.getJDA().getSelfUser().getEffectiveAvatarUrl())
                .addField("Créé par", event.getJDA().getUserById(StringUtils.USER_CREATOR).getAsMention(), true)
                .addField("Version", StringUtils.VERSION, true)
                .addField("Uptime", T.getUptime(), true)
                .addField(ResponseUtils.createList("Library utilisés", false,
                        "[JDA " + JDAInfo.VERSION + "](" + JDAInfo.GITHUB + ")",
                        "[LavaPlayer 1.2.42](https://github.com/sedmelluq/lavaplayer)",
                        "[Jaskson Databind 2.8.8](https://github.com/FasterXML/jackson-databind)"))
                .send(event.getChannel()).queue();

        return true;
    }
}
