package com.jesus_crie.deusvult.command.commands;

import com.jesus_crie.deusvult.command.Command;
import com.jesus_crie.deusvult.command.CommandPattern;
import com.jesus_crie.deusvult.response.ResponseBuilder;
import com.jesus_crie.deusvult.response.ResponsePage;
import com.jesus_crie.deusvult.response.ResponsePaginable;
import com.jesus_crie.deusvult.utils.S;
import com.jesus_crie.deusvult.utils.StringUtils;
import com.jesus_crie.deusvult.utils.T;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.List;

public class TestCommand extends Command {

    public TestCommand() {
        super("test",
                S.COMMAND_TEST_HELP.get(),
                null,
                AccessLevel.ADMIN,
                Context.calculate(Context.EVERYWHERE));

        registerPatterns(
                new CommandPattern(new CommandPattern.Argument[] {
                        CommandPattern.Argument.forString("embed"),
                        CommandPattern.Argument.STRING,
                        CommandPattern.Argument.URL_AS_STRING,
                        CommandPattern.Argument.STRING.clone().setRepeatable(true)
                }, this::onCommandEmbed, "embed <titre> <url icone> <contenu>"),

                new CommandPattern(null, (e, a) -> onCommandTest(e), "")
        );
    }

    private boolean onCommandEmbed(MessageReceivedEvent event, List<Object> args) {
        ResponseBuilder.create(event.getMessage())
                .setTitle((String) args.get(1))
                .setIcon((String) args.get(2))
                .setDescription(
                        String.join(" ", args.subList(3, args.size()).stream()
                            .map(Object::toString)
                            .toArray(String[]::new)))
                .send(event.getChannel()).queue();

        return true;
    }

    private boolean onCommandTest(MessageReceivedEvent event) {
        ResponsePaginable.create(event.getMessage(), "Test Paginable")
                .setTimeout(T.calc(30))
                .setIcon(StringUtils.ICON_CUP)
                .addPages(
                        new ResponsePage("Page 1").setDescription("Je suis une peche"),
                        new ResponsePage("Page 2").setDescription("Témoch"))
                .send(event.getChannel(), event.getAuthor());

        return true;
    }
}
