package com.jesus_crie.deusvult.commands;

import com.jesus_crie.deusvult.DeusVult;
import com.jesus_crie.deusvult.builder.EmbedMessageBuilder;
import com.jesus_crie.deusvult.utils.StringUtils;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.PermissionException;

public class StopCommand extends Command {

    public StopCommand() {
        super("stop",
                "stop",
                AccessLevel.CREATOR,
                ChannelType.PRIVATE);
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) throws PermissionException {
        EmbedMessageBuilder builder = new EmbedMessageBuilder(event.getAuthor());
        builder.setAuthor("Shuting down...", null, StringUtils.ICON_BED);
        event.getChannel().sendMessage(builder.build()).complete();

        DeusVult.instance().shutdown();
    }
}
