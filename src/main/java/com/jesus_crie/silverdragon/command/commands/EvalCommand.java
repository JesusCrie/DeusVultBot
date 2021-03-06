package com.jesus_crie.silverdragon.command.commands;

import com.jesus_crie.silverdragon.command.Command;
import com.jesus_crie.silverdragon.command.CommandPattern;
import com.jesus_crie.silverdragon.response.ResponseBuilder;
import com.jesus_crie.silverdragon.utils.F;
import com.jesus_crie.silverdragon.utils.StringUtils;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.List;

public class EvalCommand extends Command {

    private final ScriptEngine engine;

    public EvalCommand() {
        super("eval",
                "Execute du code en JS. Uniquement utilisable par le créateur.",
                null,
                AccessLevel.CREATOR,
                Context.calculate(Context.EVERYWHERE));

        registerPatterns(
                new CommandPattern(new CommandPattern.Argument[] {
                    CommandPattern.Argument.STRING.clone().setRepeatable(true)
                }, this::onCommand, "<code>")
        );

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
            String imports = "var imports = new JavaImporter(" +
                    "java.util," +
                    "java.lang," +
                    "com.jesus_crie.silverdragon," +
                    "com.jesus_crie.silverdragon.utils," +
                    "com.jesus_crie.silverdragon.manager," +
                    "com.jesus_crie.silverdragon.config," +
                    "com.jesus_crie.silverdragon.response);";
            result = engine.eval(imports + "with (imports) {\n" + code + "\n}");
        } catch (Exception ee) {
            result = ee;
        }

        ResponseBuilder.create(e.getMessage())
                .setTitle("Evaluation (JS / Nashorn)")
                .setIcon(StringUtils.ICON_TERMINAL)
                .addField("Code à exécuter", F.codeBlock("js", code), false)
                .addField("Resultat", F.codeBlock("bash", result == null ? "null" : result.toString()), false)
                .send(e.getChannel()).queue();

        return true;
    }
}
