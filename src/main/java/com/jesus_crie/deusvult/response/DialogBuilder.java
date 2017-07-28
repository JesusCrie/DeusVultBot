package com.jesus_crie.deusvult.response;

import com.jesus_crie.deusvult.utils.StringUtils;
import com.jesus_crie.deusvult.utils.Waiter;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;

import java.awt.Color;

public class DialogBuilder {

    private final EmbedBuilder builder = new EmbedBuilder();
    private final User target;

    public DialogBuilder(User target) {
        this.target = target;
        builder.setAuthor("Confirmation", null, StringUtils.ICON_HELP);
        builder.setColor(Color.BLUE);
        builder.setFooter(StringUtils.stringifyUser(target), target.getEffectiveAvatarUrl());
    }

    public DialogBuilder setContent(String content) {
        builder.setDescription(content);
        return this;
    }

    public boolean send(MessageChannel channel) {
        Message m = channel.sendMessage(builder.build()).complete();
        m.addReaction(StringUtils.EMOTE_CONFIRM).complete();
        m.addReaction(StringUtils.EMOTE_DENY).complete();

        MessageReactionAddEvent event = Waiter.getNextEvent(MessageReactionAddEvent.class,
                e -> e.getMessageIdLong() == m.getIdLong()
                        && e.getUser().equals(target)
                        && (e.getReactionEmote().getName().equals(StringUtils.EMOTE_CONFIRM) || e.getReactionEmote().getName().equals(StringUtils.EMOTE_DENY)),
                () -> m.clearReactions().complete(),
                -1);
        try {
            m.clearReactions().queue();
        } catch (Exception ignore) {}

        return event != null && !event.getReactionEmote().getName().equals(StringUtils.EMOTE_DENY) && event.getReactionEmote().getName().equals(StringUtils.EMOTE_CONFIRM);
    }
}
