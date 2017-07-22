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

public class MathCommand extends Command {

    private ScriptEngine engine = new ScriptEngineManager().getEngineByExtension("js");
    private String math = "var Math = Java.type(java.lang.Math);";

    public MathCommand() {
        super("math",
                S.COMMAND_MATH_HELP.get(),
                null,
                AccessLevel.EVERYONE,
                Context.calculate(Context.EVERYWHERE));

        registerPatterns(
                new CommandPattern(new CommandPattern.Argument[] {
                        CommandPattern.Argument.STRING.clone().setRepeatable(true)
                }, this::onCommand)
        );
    }

    private boolean onCommand(MessageReceivedEvent event, List<Object> args) {
        String expression = String.join(" ", args.stream()
                .map(Object::toString)
                .toArray(String[]::new));

        Object result;
        try {
            result = engine.eval(expression.replace("sqrt", "Math.sqrt")
                                            .replace("pow", "Math.pow")
                                            .replace("abs", "Math.abs")
                                            .replace("sin", "Math.sin")
                                            .replace("cos", "Math.cos")
                                            .replace("tan", "Math.ran")
                                            .replace("exp", "Math.exp")
                                            .replace("random", "Math.random"));
        } catch (ScriptException e) {
            result = e;
        }

        ResponseBuilder.create(event.getMessage())
                .setTitle(S.COMMAND_MATH_TITLE.get())
                .setIcon(StringUtils.ICON_TERMINAL)
                .addField(S.COMMAND_MATH_EXPRESSION.get(), F.codeBlock("js", expression), false)
                .addField(S.COMMAND_MATH_RESULT.get(), F.codeBlock(result.toString()), false)
                .send(event.getChannel()).queue();

        return true;
    }
}
