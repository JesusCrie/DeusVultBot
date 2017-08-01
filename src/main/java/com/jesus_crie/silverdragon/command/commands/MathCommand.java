package com.jesus_crie.silverdragon.command.commands;

import com.jesus_crie.silverdragon.command.Command;
import com.jesus_crie.silverdragon.command.CommandPattern;
import com.jesus_crie.silverdragon.response.ResponseBuilder;
import com.jesus_crie.silverdragon.utils.F;
import com.jesus_crie.silverdragon.utils.StringUtils;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.List;

public class MathCommand extends Command {

    private final ScriptEngine engine = new ScriptEngineManager().getEngineByExtension("js"); // TODO exclude classes
    private String math = "var Math = Java.type(java.lang.Math);";

    public MathCommand() {
        super("math",
                "Calcule le resultat d'un calcul donné.",
                null,
                AccessLevel.ADMIN,
                Context.calculate(Context.EVERYWHERE));

        registerPatterns(
                new CommandPattern(new CommandPattern.Argument[] {
                        CommandPattern.Argument.STRING.clone().setRepeatable(true)
                }, this::onCommand, "<expression>")
        );
    }

    private boolean onCommand(MessageReceivedEvent event, List<Object> args) { //TODO restrict usable packages
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
                .setTitle("Math")
                .setIcon(StringUtils.ICON_TERMINAL)
                .addField("Expression (math)", F.codeBlock("js", expression), false)
                .addField("Résultat", F.codeBlock(result.toString()), false)
                .send(event.getChannel()).queue();

        return true;
    }
}
