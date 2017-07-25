package com.jesus_crie.deusvult.command.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jesus_crie.deusvult.command.Command;
import com.jesus_crie.deusvult.command.CommandPattern;
import com.jesus_crie.deusvult.exception.CommandException;
import com.jesus_crie.deusvult.logger.Logger;
import com.jesus_crie.deusvult.response.ResponseBuilder;
import com.jesus_crie.deusvult.response.ResponseUtils;
import com.jesus_crie.deusvult.utils.S;
import com.jesus_crie.deusvult.utils.StringUtils;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Random;

public class GifCommand extends Command {

    public GifCommand() {
        super("gif",
                S.COMMAND_GIF_HELP.get(),
                null,
                AccessLevel.EVERYONE,
                Context.calculate(Context.EVERYWHERE));

        registerPatterns(
                new CommandPattern(new CommandPattern.Argument[] {
                        CommandPattern.Argument.STRING.clone().setRepeatable(true)
                }, this::onCommandSearch, "<recherche>"),

                new CommandPattern(null, (e, a) -> onCommandRandom(e), "")
        );
    }

    private boolean onCommandRandom(MessageReceivedEvent event) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode node = mapper.readValue(new URL(StringUtils.GIPHY_RANDOM + "?api_key=" + StringUtils.GIPHY_KEY), JsonNode.class);
            String gif = node.get("data").get("image_original_url").asText();

            ResponseBuilder.create(event.getMessage())
                    .setTitle(S.COMMAND_GIF_RANDOM.get())
                    .setIcon(StringUtils.ICON_GIPHY)
                    .setImage(gif)
                    .send(event.getChannel()).queue();
        } catch (IOException e) {
            ResponseUtils.errorMessage(event.getMessage(), new CommandException(S.COMMAND_GIF_FAIL.get()))
                    .send(event.getChannel()).queue();
            Logger.COMMAND.get().trace(e);
            return true;
        }

        return true;
    }

    private boolean onCommandSearch(MessageReceivedEvent event, List<Object> args) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String search = String.join(" ", args.stream().map(Object::toString).toArray(String[]::new));
            String url = StringUtils.GIPHY_SEARCH + "?api_key=" + StringUtils.GIPHY_KEY + "&q=" + search.replace(" ", "+");
            JsonNode node = mapper.readValue(new URL(url), JsonNode.class).get("data");

            String gif = node.get(new Random().nextInt(node.size())).get("images").get("original").get("url").asText();

            ResponseBuilder.create(event.getMessage())
                    .setTitle(S.COMMAND_GIF_SEARCH.format(search))
                    .setIcon(StringUtils.ICON_GIPHY)
                    .setImage(gif)
                    .send(event.getChannel()).queue();
        } catch (IOException e) {
            ResponseUtils.errorMessage(event.getMessage(), new CommandException(S.COMMAND_GIF_FAIL.get()))
                    .send(event.getChannel()).queue();
            Logger.COMMAND.get().trace(e);
            return true;
        }

        return true;
    }
}
