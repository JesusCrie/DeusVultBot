package com.jesus_crie.deusvult.logger;

import com.jesus_crie.deusvult.DeusVult;
import com.jesus_crie.deusvult.utils.F;
import com.jesus_crie.deusvult.utils.StringUtils;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.utils.SimpleLog;

import java.awt.*;
import java.util.Date;

public class DiscordLogListener implements SimpleLog.LogListener {

    private TextChannel channel;
    public DiscordLogListener(TextChannel c) {
        channel = c;
    }

    @Override
    public void onLog(SimpleLog log, SimpleLog.Level logLevel, Object message) {
        logToDiscord(F.bold("[" + log.name + "] ") + message, logLevel);
    }

    @Override
    public void onError(SimpleLog log, Throwable err) {
        logToDiscord(F.bold("[" + log.name + "] ") + err, SimpleLog.Level.FATAL);
    }

    public void logToDiscord(String message, SimpleLog.Level level) {
        if (!DeusVult.instance().isReady())
            return;

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTimestamp(new Date().toInstant());

        switch (level) {
            case FATAL:
                builder.setColor(Color.RED);
                builder.setAuthor("FATAL", null, StringUtils.ICON_ERROR);
                break;
            case WARNING:
                builder.setColor(Color.ORANGE);
                builder.setAuthor("WARNING", null, StringUtils.ICON_ERROR);
                break;
            case DEBUG:
                builder.setColor(Color.GRAY);
                builder.setAuthor("Debug", null, StringUtils.ICON_TERMINAL);
                break;
            case TRACE:
                builder.setColor(Color.RED);
                break;
            case INFO:
            default:
                builder.setColor(Color.GREEN);
                builder.setAuthor(level.name(), null, StringUtils.ICON_CHECK);
                break;
        }

        builder.setDescription(message);
        channel.sendMessage(builder.build()).queue();
    }
}
