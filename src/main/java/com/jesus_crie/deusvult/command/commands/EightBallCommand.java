package com.jesus_crie.deusvult.command.commands;

import com.jesus_crie.deusvult.command.Command;
import com.jesus_crie.deusvult.command.CommandPattern;
import com.jesus_crie.deusvult.response.ResponseBuilder;
import com.jesus_crie.deusvult.utils.StringUtils;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class EightBallCommand extends Command {

    private final Random random = new Random();
    private final List<String> answers = Arrays.asList("Oui.//Je pense que oui.//Ca me parait evident.//Bien sur.//Effectivement.//A mon avis, oui.//A l'évidence oui.//" +
            "Je suis mitigé.//J'hésite.//Tu m'en demande beaucoup tu sais.//Je me tate encore.//Pas la moindre idée !//J'ai même pas envie de répondre.//Un peu oui mais un peu non.//" +
            "Non.//Vraiment ? Non.//Tu plaisante j'espère ?//HEHO, redescend sur terre !//42.//Sans doute pas.//Mdr nope.".split("//"));

    public EightBallCommand() {
        super("8ball",
                "Pose une question et recois une réponse claire et précise.",
                null,
                AccessLevel.EVERYONE,
                Context.calculate(Context.EVERYWHERE));

        registerPatterns(
                new CommandPattern(new CommandPattern.Argument[] {
                        CommandPattern.Argument.STRING.clone().setRepeatable(true)
                }, this::onCommand, "<question>")
        );
    }

    private boolean onCommand(MessageReceivedEvent event, List<Object> args) {
        String question = event.getMessage().getStrippedContent().substring(StringUtils.PREFIX.length()).substring("8ball ".length());
        String answer = answers.get(random.nextInt(answers.size()));

        ResponseBuilder.create(event.getMessage())
                .addField(StringUtils.EMOTE_EXCLAMATION + " " + question, StringUtils.EMOTE_8BALL + " " + answer, false)
                .send(event.getChannel()).queue();

        return true;
    }
}
