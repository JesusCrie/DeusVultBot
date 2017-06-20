package com.jesus_crie.deusvult.commands;

import com.jesus_crie.deusvult.builder.EmbedMessageBuilder;
import com.jesus_crie.deusvult.utils.StringUtils;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.PermissionException;

/**
 * Exemple command, will not be registered.
 */
public class ExempleCommand extends Command { // Need to extend Command.

    /**
     * Default constructor
     */
    public ExempleCommand() {
        super("example,,ex", // Name and aliases of the command separated with ,,
                "example <name>", // Some exemple on how to use the command (for the help command) separated with ,,
                AccessLevel.SERVER_OWNER_CREATOR, // Who can use this command ? Here the creator of the bot (Jesus_Crie) and the owner of the server
                ChannelType.TEXT, ChannelType.GROUP, ChannelType.PRIVATE); // Where can we use this command ? (text = guild, group/private = mp)

        // Registering sub command (useless we're not gonna use this but it's fun)
        registerSubCommands(
                new ASubCommand()
        );
    }

    /**
     * The method that will be executed when the command is triggered.
     *
     * @param event - The event that as triggered the command.
     * @param args - An array of strings that contains every arguments given after the command (ex: `>test lol 123` will return ["lol", "123"])
     * @throws PermissionException - Don't worry about permissions
     */
    @Override
    public void execute(MessageReceivedEvent event, String[] args) throws PermissionException {
        if (args.length < 1) { // If there is no args
            event.getChannel() // Get the channel
                    .sendMessage( // Send a message
                            StringUtils.getErrorMessage( // The StringUtils class contains some usefull method like this one that create an error message.
                                    event.getAuthor(), // We need to provide the author of the command for the footer.
                                    "I need more args !") // A message that explain our disappointment.
                    ).queue(); // DON'T FORGET THIS or the message while never be sent.
            return; // Because this fool has no arguments.
        }

        EmbedMessageBuilder builder = new EmbedMessageBuilder(event.getAuthor()); // Create an embed builder with the author for the footer.

        // Set a text with an image
        builder.setAuthor("Hey " + event.getAuthor().getName() + " !", // Set the text
                null, // We don't care about him
                StringUtils.ICON_CHECK); // The icon url, StringUtils is awesome

        event.getChannel().sendMessage(builder.build()).queue(); // Now we send this shit
    }

    /**
     * A sub command that will never be used, just for fun
     */
    private class ASubCommand extends Command {

        public ASubCommand() {
            super("sub,,s",
                    "example sub",
                    AccessLevel.SERVER_OWNER_CREATOR,
                    ChannelType.TEXT, ChannelType.GROUP, ChannelType.PRIVATE);
        }

        /**
         * See {@link com.jesus_crie.deusvult.commands.Command#execute(MessageReceivedEvent, String[])}
         */
        @Override
        public void execute(MessageReceivedEvent event, String[] args) throws PermissionException {
            event.getChannel().sendMessage("You're ugly.").queue();
        }
    }
}
