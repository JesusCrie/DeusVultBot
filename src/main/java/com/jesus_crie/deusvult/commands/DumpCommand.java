package com.jesus_crie.deusvult.commands;

import com.jesus_crie.deusvult.builder.EmbedMessageBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.PermissionException;

import java.util.List;
import java.util.stream.Collectors;

public class DumpCommand extends Command {

    public DumpCommand() {
        super("dump",
                "dump",
                AccessLevel.CREATOR,
                ChannelType.TEXT);
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) throws PermissionException {
        EmbedMessageBuilder builder = new EmbedMessageBuilder(event.getAuthor());
        builder.addMainList("Main infos", event.getGuild().getIconUrl(),
                "Guild Name: " + event.getGuild().getName(),
                "Guild ID: `" + event.getGuild().getId() + "`");

        List<String> roles = event.getGuild().getRoles().stream()
                .map(r -> r.getName() + ": `" + r.getId() + "`")
                .collect(Collectors.toList());
        builder.addFieldList("Roles", roles);

        event.getChannel().sendMessage(builder.build()).queue();
    }
}
