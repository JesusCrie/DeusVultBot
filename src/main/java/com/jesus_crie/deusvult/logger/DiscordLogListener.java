package com.jesus_crie.deusvult.logger;

import com.jesus_crie.deusvult.DeusVult;
import com.jesus_crie.deusvult.response.ResponseBuilder;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;

import java.awt.*;
import java.util.Date;

public class DiscordLogListener implements Logger.Listener {

    private static final String FORMAT = "**[%level%] [%thread%] [%name%]** %content%";

    private final TextChannel channel;
    public DiscordLogListener(TextChannel c) {
        channel = c;
    }

    @Override
    public void onLog(Logger.Log log, Logger.SimpleLogger logger) {
        if (!DeusVult.instance().isReady())
            return;

        final EmbedBuilder builder = new EmbedBuilder();
        builder.setFooter(ResponseBuilder.TIME.format(new Date()), null);

        switch (log.getLevel()) {
            case INFO:
                builder.setColor(Color.GREEN);
                break;
            case DEBUG:
                builder.setColor(Color.GRAY);
                break;
            case WARNING:
                builder.setColor(Color.ORANGE);
                break;
            case FATAL:
            case UNKNOW:
            default:
                builder.setColor(Color.RED);
                break;
        }

        String out = FORMAT.replace("%level%", log.getLevel().toString())
                .replace("%thread%", log.getThreadName())
                .replace("%name%", logger.getName())
                .replace("%content%", log.getContent().toString()
                        .replace("\"", "```yaml\n"));
        builder.setDescription(out);

        channel.sendMessage(builder.build()).queue();
    }
}
