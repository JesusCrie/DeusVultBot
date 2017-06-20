package com.jesus_crie.deusvult.commands;

import com.jesus_crie.deusvult.builder.EmbedMessageBuilder;
import com.jesus_crie.deusvult.utils.F;
import com.jesus_crie.deusvult.utils.StringUtils;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.PermissionException;

public class TestCommand extends Command {

    public TestCommand() {
        super("test,,t",
                "test <dev|lol>",
                AccessLevel.CREATOR,
                ChannelType.TEXT, ChannelType.GROUP, ChannelType.PRIVATE);
        setShortDescription("Test command.");

        registerSubCommands(
                new Dev(),
                new Lol()
        );
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) throws PermissionException {
        if (args.length < 1) {
            event.getChannel().sendMessage(StringUtils.getErrorMessage(event.getAuthor(), "Not enough args.")).queue();
            return;
        }

        if (!proceedSubCommand(event, args)) {
            event.getChannel().sendMessage(StringUtils.getErrorMessage(event.getAuthor(), "Unknown subcommand !")).queue();
        }
    }

    private class Dev extends Command {

        public Dev() {
            super("dev,,d",
                    "test dev",
                    AccessLevel.CREATOR,
                    ChannelType.TEXT, ChannelType.GROUP, ChannelType.PRIVATE);
        }

        @Override
        public void execute(MessageReceivedEvent event, String[] args) throws PermissionException {
            EmbedMessageBuilder builder = new EmbedMessageBuilder(event.getAuthor());
            builder.addMainList("Some infos on this channel.", StringUtils.ICON_INFO,
                    "Name: " + F.bold(event.getChannel().getName()),
                    "ID: " + F.bold(event.getChannel().getId()));

            Message lastMsg = event.getChannel().getMessageById(event.getChannel().getLatestMessageId()).complete();
            builder.addFieldList("Last Message",
                    "Author: " + lastMsg.getAuthor().getAsMention(),
                    "ID: " + F.bold(lastMsg.getId()),
                    "Content: " + F.codeBlock("http", lastMsg.getRawContent()));

            event.getChannel().sendMessage(builder.build()).queue();
        }
    }

    private class Lol extends Command {

        public Lol() {
            super("lol",
                    "test lol",
                    AccessLevel.CREATOR,
                    ChannelType.TEXT, ChannelType.GROUP, ChannelType.PRIVATE);
        }

        @Override
        public void execute(MessageReceivedEvent event, String[] args) throws PermissionException {
            event.getChannel().sendMessage("Bite mdr").queue();
        }
    }
}
