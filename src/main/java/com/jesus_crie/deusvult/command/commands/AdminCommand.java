package com.jesus_crie.deusvult.command.commands;

import com.jesus_crie.deusvult.command.Command;
import com.jesus_crie.deusvult.command.CommandPattern;
import com.jesus_crie.deusvult.response.ResponseBuilder;
import com.jesus_crie.deusvult.utils.S;
import com.jesus_crie.deusvult.utils.StringUtils;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.List;

public class AdminCommand extends Command {

    public AdminCommand() {
        super("admin",
                S.COMMAND_ADMIN_HELP.get(),
                null,
                AccessLevel.ADMIN,
                Context.calculate(Context.EVERYWHERE));

        registerPatterns(
                new CommandPattern(new CommandPattern.Argument[] {
                        CommandPattern.Argument.forString("blankrole"),
                        CommandPattern.Argument.STRING
                }, this::onCommand, "blankrole <nom du role>")
        );
    }

    private boolean onCommand(MessageReceivedEvent event, List<Object> args) {
        Role r = event.getGuild().getController().createRole()
                .setName((String) args.get(1))
                .setPermissions(0)
                .setHoisted(false)
                .setMentionable(false)
                .complete();

        ResponseBuilder.create(event.getMessage())
                .setTitle("Role created ")
                .setDescription(r.getAsMention())
                .setIcon(StringUtils.ICON_TERMINAL)
                .send(event.getChannel()).queue();

        return true;
    }
}
