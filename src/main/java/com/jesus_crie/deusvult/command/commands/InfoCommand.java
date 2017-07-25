package com.jesus_crie.deusvult.command.commands;

import com.jesus_crie.deusvult.command.Command;
import com.jesus_crie.deusvult.command.CommandPattern;
import com.jesus_crie.deusvult.response.ResponseBuilder;
import com.jesus_crie.deusvult.response.ResponseUtils;
import com.jesus_crie.deusvult.utils.S;
import com.jesus_crie.deusvult.utils.StringUtils;
import com.jesus_crie.deusvult.utils.T;
import net.dv8tion.jda.core.JDAInfo;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class InfoCommand extends Command {

    public InfoCommand() {
        super("info",
                S.COMMAND_INFO_HELP.get(),
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
                .addField(S.COMMAND_INFO_CREATED_BY.get(), event.getJDA().getUserById(StringUtils.USER_CREATOR).getAsMention(), true)
                .addField(S.COMMAND_INFO_VERSION.get(), StringUtils.VERSION, true)
                .addField(S.COMMAND_INFO_UPTIME.get(), T.getUptime(), true)
                .addField(ResponseUtils.createList(S.COMMAND_INFO_LIBS.get(), false,
                        "[JDA " + JDAInfo.VERSION + "](" + JDAInfo.GITHUB + ")",
                        "[LavaPlayer 1.2.42](https://github.com/sedmelluq/lavaplayer)",
                        "[Jaskson Databind 2.8.8](https://github.com/FasterXML/jackson-databind)"))
                .send(event.getChannel()).queue();

        return true;
    }
}
