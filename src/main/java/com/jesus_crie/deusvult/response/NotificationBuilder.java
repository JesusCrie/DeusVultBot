package com.jesus_crie.deusvult.response;

import com.jesus_crie.deusvult.utils.Awaiter;
import com.jesus_crie.deusvult.utils.S;
import com.jesus_crie.deusvult.utils.StringUtils;
import com.jesus_crie.deusvult.utils.T;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

public class NotificationBuilder {

    private User toSendTo;
    private long timeout = T.calc(0, 10);
    private EmbedBuilder builder = new EmbedBuilder();

    public static NotificationBuilder create(User u) {
        return new NotificationBuilder(u);
    }

    private NotificationBuilder(User u) {
        toSendTo = u;
        builder.setAuthor(S.RESPONSE_NOTIFICATION_TITLE.get(), null, StringUtils.ICON_BELL);
        builder.setFooter(S.RESPONSE_NOTIFICATION_FOOTER.get(), null);
    }

    public NotificationBuilder setContent(String content) {
        builder.setDescription(content);
        return this;
    }

    public NotificationBuilder setTimeout(long timeout) {
        this.timeout = timeout;
        return this;
    }

    public void send() {
        Message m = toSendTo.openPrivateChannel().complete().sendMessage(builder.build()).complete();
        m.addReaction(StringUtils.EMOJI_CACTUS).complete();

        Awaiter.awaitReactNotification(m, toSendTo, e -> {
            if (!e.getReactionEmote().getName().equals(StringUtils.EMOJI_CACTUS))
                return;
            m.delete().queue();
        });
    }
}
