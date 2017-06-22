package com.jesus_crie.deusvult.builder;

import com.jesus_crie.deusvult.listener.ReactionListener;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public abstract class ReactableMessageBuilder extends EmbedMessageBuilder {

    protected ReactionListener listener;
    protected Consumer<MessageReactionAddEvent> action;
    protected List<String> emotes;

    public ReactableMessageBuilder(User u) {
        super(u);
    }

    public void setReactions(String... emotes) {
        this.emotes = Arrays.asList(emotes);
    }

    public abstract void send(MessageChannel channel);
}
