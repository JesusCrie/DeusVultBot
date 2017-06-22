package com.jesus_crie.deusvult.listener;

import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.function.Consumer;

public class ReactionListener extends ListenerAdapter {

    private long msgId;
    private Consumer<MessageReactionAddEvent> onReaction;

    public ReactionListener(long msgId, Consumer<MessageReactionAddEvent> onReaction) {
        this.msgId = msgId;
        this.onReaction = onReaction;
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (event.getReaction().isSelf())
            return;
        if (event.getMessageIdLong() != event.getMessageIdLong())
            return;

        onReaction.accept(event);
    }
}
