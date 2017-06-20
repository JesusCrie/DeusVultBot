package com.jesus_crie.deusvult.logger;

import com.jesus_crie.deusvult.DeusVult;
import com.jesus_crie.deusvult.utils.StringUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;

import java.awt.*;
import java.time.Instant;
import java.util.Arrays;

public class DiscordLog {

    private static TextChannel logChannel;

    public static void init(TextChannel channel) {
        logChannel = channel;
    }

    public static void info(String message, long timestamp) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setAuthor("Info", null, StringUtils.ICON_CHECK);
        builder.setColor(Color.GREEN);
        builder.setTimestamp(Instant.ofEpochMilli(timestamp));
        builder.setTitle(message);

        send(builder.build());
    }

    public static void warning(String message, long timestamp) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setAuthor("Warning", null, StringUtils.ICON_ERROR);
        builder.setColor(Color.ORANGE);
        builder.setTimestamp(Instant.ofEpochMilli(timestamp));
        builder.setTitle(message);

        send(builder.build());
    }

    public static void error(String message, Exception e, long timestamp) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setAuthor("ERROR", null, StringUtils.ICON_ERROR);
        builder.setColor(Color.RED);
        builder.setTimestamp(Instant.ofEpochMilli(timestamp));
        builder.setTitle(message);
        builder.setDescription("\n" + String.join("\n", Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).toArray(String[]::new)));

        send(builder.build());
    }

    private static void send(MessageEmbed embed) {
        if (logChannel != null && DeusVult.instance().isReady())
            logChannel.sendMessage(embed).queue();
    }
}
