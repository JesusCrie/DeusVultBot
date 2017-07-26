package com.jesus_crie.deusvult.response;

import com.jesus_crie.deusvult.utils.StringUtils;
import com.jesus_crie.deusvult.utils.Waiter;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

public class NotificationBuilder {

    private final User toSendTo;
    private final EmbedBuilder builder = new EmbedBuilder();

    public static NotificationBuilder create(User u) {
        return new NotificationBuilder(u);
    }

    private NotificationBuilder(User u) {
        toSendTo = u;
        builder.setAuthor("Notification", null, StringUtils.ICON_BELL);
        builder.setFooter("Clique sur " + StringUtils.EMOTE_CACTUS + " pour effacer", null);
    }

    public NotificationBuilder setContent(String content) {
        builder.setDescription(content);
        return this;
    }

    public void send() {
        Message m = toSendTo.openPrivateChannel().complete().sendMessage(builder.build()).complete();
        m.addReaction(StringUtils.EMOTE_CACTUS).complete();

        Waiter.awaitReactionFromUser(m, toSendTo,
                StringUtils.EMOTE_CACTUS,
                e -> m.delete().queue());
    }
}
