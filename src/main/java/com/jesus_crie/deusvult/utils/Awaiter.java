package com.jesus_crie.deusvult.utils;

import com.jesus_crie.deusvult.DeusVult;
import com.jesus_crie.deusvult.listener.AwaitListener;
import com.jesus_crie.deusvult.manager.TimerManager;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;

import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Awaiter {

    public static void awaitMessageFromUser(MessageChannel channel, User user, Consumer<MessageReceivedEvent> onSuccess, Runnable onTimeout, long timeout) {
        awaitEvent(MessageReceivedEvent.class,
                e -> e.getChannel().equals(channel) && e.getAuthor().equals(user),
                onSuccess,
                onTimeout,
                true,
                timeout);
    }

    public static void awaitReactionFromUser(Message targetMessage, User user, Consumer<MessageReactionAddEvent> onSuccess, Runnable onTimeout, long timeout) {
        awaitEvent(MessageReactionAddEvent.class,
                e -> e.getMessageIdLong() == targetMessage.getIdLong() && e.getUser().equals(user),
                onSuccess,
                onTimeout,
                false,
                timeout);
    }

    public static <T extends Event> void awaitEvent(Class<T> clazz, Predicate<T> checker, Consumer<T> onSuccess, Runnable onTimeout, boolean singleTrigger, long timeout) {
        Timer timer = TimerManager.create();

        AwaitListener<T> listener = new AwaitListener<>(e -> {
            if (checker.test(e)) {
                onSuccess.accept(e);

                if (singleTrigger) {
                    timer.cancel();
                    return true;
                }
            }
            return false;
        }, clazz);

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                DeusVult.instance().getJda().removeEventListener(listener);
                onTimeout.run();
            }
        };
        DeusVult.instance().getJda().addEventListener(listener);

        if (timeout > 0)
            timer.schedule(task, timeout);
    }
}
