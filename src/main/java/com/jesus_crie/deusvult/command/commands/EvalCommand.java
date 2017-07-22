package com.jesus_crie.deusvult.command.commands;

import com.jesus_crie.deusvult.command.Command;
import com.jesus_crie.deusvult.command.CommandPattern;
import com.jesus_crie.deusvult.response.ResponseBuilder;
import com.jesus_crie.deusvult.utils.F;
import com.jesus_crie.deusvult.utils.S;
import com.jesus_crie.deusvult.utils.StringUtils;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.List;

public class EvalCommand extends Command {

    private ScriptEngine engine;
    private String imports = "var imports = new JavaImporter(" +
            "java.util," +
            "java.lang," +
            "com.jesus_crie.deusvult," +
            "com.jesus_crie.deusvult.utils," +
            "com.jesus_crie.deusvult.manager," +
            "com.jesus_crie.deusvult.config," +
            "com.jesus_crie.deusvult.response);";

    public EvalCommand() {
        super("eval",
                S.COMMAND_EVAL_HELP.get(),
                null,
                AccessLevel.ADMIN,
                Context.calculate(Context.EVERYWHERE));

        registerPatterns(new CommandPattern(new CommandPattern.Argument[] {
                CommandPattern.Argument.STRING.clone().setRepeatable(true)
        }, this::onCommand));

        engine = new ScriptEngineManager().getEngineByExtension("js");
    }

    private boolean onCommand(MessageReceivedEvent e, List<Object> args) {
        String code = String.join(" ", args.stream()
                .map(Object::toString)
                .toArray(String[]::new));

        engine.put("event", e);
        engine.put("message", e.getMessage());
        engine.put("jda", e.getJDA());

        Object result;

        try {
            result = engine.eval(imports + "with (imports) {\n" + code + "\n}");
        } catch (ScriptException ee) {
            result = ee;
        }

        ResponseBuilder.create(e.getMessage())
                .setTitle(S.COMMAND_EVAL_TITLE.get())
                .setIcon(StringUtils.ICON_TERMINAL)
                .addField(S.COMMAND_EVAL_TO_EVALUATE.get(), F.codeBlock("js", code), false)
                .addField(S.COMMAND_EVAL_RESULT.get(), F.codeBlock("bash", result == null ? "null" : result.toString()), false)
                .send(e.getChannel()).queue();

        return true;
    }
}
