package com.jesus_crie.silverdragon.listener;

import com.jesus_crie.silverdragon.SilverDragon;
import com.jesus_crie.silverdragon.command.Command;
import com.jesus_crie.silverdragon.exception.CommandException;
import com.jesus_crie.silverdragon.logger.Logger;
import com.jesus_crie.silverdragon.manager.CommandManager;
import com.jesus_crie.silverdragon.manager.ThreadManager;
import com.jesus_crie.silverdragon.response.ResponseUtils;
import com.jesus_crie.silverdragon.utils.StringUtils;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.PermissionException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.Arrays;

import static com.jesus_crie.silverdragon.utils.S.f;

public class CommandListener extends ListenerAdapter {

    public static final String CMD_PRIVATE = "[%user%] Executing: ¤¤%content%```";
    public static final String CMD_GUILD = "[%guild%] %user% triggered: ¤¤%content%```";

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        // Check if is a command
        if (!event.getMessage().getRawContent().startsWith(StringUtils.PREFIX))
            return;

        // Check if is self
        if (event.getAuthor().getIdLong() == SilverDragon.instance().getJDA().getSelfUser().getIdLong())
            return;

        String[] fullCmd = event.getMessage().getRawContent().trim().replace("\n", " ").substring(StringUtils.PREFIX.length()).split(" ");
        Command command = CommandManager.getCommand(fullCmd[0]);

        // Check if command exist
        if (command == null)
            return;

        // Check if context is allowed
        if ((command.getContext() & Command.Context.fromChannel(event.getChannel())) == 0) {
            ResponseUtils.errorMessage(event.getMessage(), new CommandException("Cette commande n'est pas autorisée dans ce contexte."))
                    .send(event.getChannel()).queue();
            return;
        }

        // If in a guild
        if (event.getGuild() != null) {
            // Check context main guild
            if (!((command.getContext() & Command.Context.ALL_GUILD.b) == Command.Context.ALL_GUILD.b)
                    && event.getGuild().getIdLong() != SilverDragon.instance().getMainGuild().getIdLong()) {
                ResponseUtils.errorMessage(event.getMessage(), new CommandException("Cette commande n'est pas disponible sur ce serveur."))
                        .send(event.getChannel()).queue();
            }

            // Check access level
            if (!command.getAccessLevel().superiorOrEqual(Command.AccessLevel.fromMember(event.getMember()))) {
                ResponseUtils.errorMessage(event.getMessage(), new CommandException("Vous n'avez pas les permissions requises pour cette commande."))
                        .send(event.getChannel()).queue();
                return;
            }

            // Check guild only
            if (!command.isGuildAuthorized(event.getGuild())) {
                ResponseUtils.errorMessage(event.getMessage(), new CommandException("Cette commande n'est pas disponible sur ce serveur."))
                        .send(event.getChannel()).queue();
                return;
            }
        }

        ThreadManager.getCommandPool().execute(() -> {
            if (event.getGuild() == null)
                Logger.COMMAND.get().info(CMD_PRIVATE.replace("%user%", StringUtils.stringifyUser(event.getAuthor()))
                        .replace("%content%", event.getMessage().getRawContent()));
            else
                Logger.COMMAND.get().info(CMD_GUILD.replace("%guild%", event.getGuild().getName())
                        .replace("%user%", StringUtils.stringifyUser(event.getAuthor()))
                        .replace("%content%", event.getMessage().getRawContent()));
            try {
                try {
                    event.getMessage().delete().complete();
                } catch (IllegalStateException ignore) {
                } finally {
                    event.getChannel().sendTyping().complete();
                    command.execute(event, Arrays.copyOfRange(fullCmd, 1, fullCmd.length));
                }
            } catch (PermissionException e) {
                Logger.COMMAND.get().trace(e);
                ResponseUtils.errorMessage(event.getMessage(), new CommandException(f("Erreur: Il manque la permission %s", e.getPermission())))
                        .send(event.getChannel()).queue();
            } catch (Exception e) {
                Logger.COMMAND.get().trace(e);
                ResponseUtils.errorMessage(event.getMessage(), new CommandException(f("FATAL ERROR: %s", e)))
                        .send(event.getChannel()).queue();
            }
        });
    }
}
