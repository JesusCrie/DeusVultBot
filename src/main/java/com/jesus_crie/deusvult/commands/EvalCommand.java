package com.jesus_crie.deusvult.commands;

import com.jesus_crie.deusvult.DeusVult;
import com.jesus_crie.deusvult.builder.EmbedMessageBuilder;
import com.jesus_crie.deusvult.utils.StringUtils;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.PermissionException;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class EvalCommand extends Command {

    public EvalCommand() {
        super("eval",
                "eval <code>",
                AccessLevel.CREATOR,
                ChannelType.TEXT, ChannelType.PRIVATE, ChannelType.GROUP);
        hidden = true;
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) throws PermissionException {
        if (args.length < 1) {
            event.getChannel().sendMessage(StringUtils.getErrorMessage(event.getAuthor(), "You need to provide some code !")).queue();
            return;
        }

        ScriptEngine engine = new ScriptEngineManager().getEngineByExtension("js");
        engine.put("bot", DeusVult.instance());
        engine.put("event", event);
        engine.put("jda", event.getJDA());

        String code = String.join(" ", args);

        EmbedMessageBuilder builder = new EmbedMessageBuilder(event.getAuthor());
        builder.setAuthor("Evaluation", null, StringUtils.ICON_TERMINAL);
        builder.addField("To evaluate", "```js\n" + code + "```", false);

        try {
            builder.addField("Result", "```" + engine.eval(code) + "```", false);
        } catch (ScriptException e) {
            builder.addField("Result", "```" + e + "```", false);
        }

        event.getChannel().sendMessage(builder.build()).queue();
    }
}
