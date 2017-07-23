package com.jesus_crie.deusvult.command.commands;

import com.jesus_crie.deusvult.DeusVult;
import com.jesus_crie.deusvult.command.Command;
import com.jesus_crie.deusvult.command.CommandPattern;
import com.jesus_crie.deusvult.response.ResponseBuilder;
import com.jesus_crie.deusvult.utils.S;
import com.jesus_crie.deusvult.utils.StringUtils;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class StopCommand extends Command {

    public StopCommand() {
        super("stop",
                S.COMMAND_STOP_HELP.get(),
                null,
                AccessLevel.CREATOR,
                Context.calculate(Context.PRIVATE, Context.MAIN_GUILD));

        registerPatterns(
                new CommandPattern(null, (e, a) -> onCommand(e))
        );
    }

    private boolean onCommand(MessageReceivedEvent e) {
        ResponseBuilder.create(e.getMessage())
                .setTitle(S.COMMAND_STOP_SHUTTING_DOWN.get())
                .setIcon(StringUtils.ICON_BED)
                .send(e.getChannel()).complete();

        DeusVult.instance().shutdown();
        return true;

    }
}
