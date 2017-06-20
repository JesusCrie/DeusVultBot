package com.jesus_crie.deusvult.listener;

import com.jesus_crie.deusvult.DeusVult;
import com.jesus_crie.deusvult.commands.Command;
import com.jesus_crie.deusvult.logger.Logger;
import com.jesus_crie.deusvult.manager.CommandManager;
import com.jesus_crie.deusvult.utils.F;
import com.jesus_crie.deusvult.utils.StringUtils;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.PermissionException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.Arrays;

public class CommandListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().getId() == DeusVult.instance().getJda().getSelfUser().getId())
            return;

        if (!event.getMessage().getRawContent().startsWith(StringUtils.PREFIX))
            return;

        String[] args = event.getMessage().getRawContent().substring(StringUtils.PREFIX.length()).split(" ");
        Command c = CommandManager.getCommand(args[0]);

        // Multiple checks (channel type & guild only)
        if (c != null && c.isAllowedHere(event.getChannelType())) {
            if (c.isGuildOnly() && !(event.getChannelType() == ChannelType.TEXT && c.getGuildOnly().contains(event.getGuild().getId())))
                return;

            // Final check (permission)
            boolean haveAccess;
            switch (c.getAccessLevel()) {
                case CREATOR:
                    haveAccess = event.getAuthor().getId().equals(StringUtils.USER_CREATOR);
                    break;
                case SERVER_OWNER:
                    haveAccess = event.getAuthor().getId().equals(event.getGuild().getOwner().getUser().getId());
                    break;
                case SERVER_OWNER_CREATOR:
                    haveAccess = (event.getAuthor().getId().equals(StringUtils.USER_CREATOR)
                            || event.getAuthor().getId().equals(event.getGuild().getOwner().getUser().getId()));
                    break;
                case EVERYONE:
                    haveAccess = true;
                    break;
                default:
                    haveAccess = false;
                    break;
            }

            if (!haveAccess)
                return;

            Logger.info("[Command] " + StringUtils.stringifyUser(event.getAuthor()) + " issued " + F.code(event.getMessage().getRawContent())
                + " from " + (event.getChannelType() == ChannelType.TEXT ? event.getGuild().getName() : "Private Channel"));
            try {
                event.getMessage().delete().complete();
            } catch (IllegalStateException ignore) {}

            new Thread(() -> {
                try {
                    c.execute(event, Arrays.copyOfRange(args, 1, args.length));
                } catch (PermissionException e) {
                    Logger.error("[Command] Missing permision !", e);
                    event.getChannel().sendMessage(StringUtils.getErrorMessage(event.getAuthor(), "Missing permission: " + e.getPermission().getName())).queue();
                }
            }).start();
        }
    }
}
