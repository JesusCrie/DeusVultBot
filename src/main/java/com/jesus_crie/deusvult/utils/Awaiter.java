package com.jesus_crie.deusvult.utils;

import com.jesus_crie.deusvult.DeusVult;
import com.jesus_crie.deusvult.listener.AwaitListener;
import com.jesus_crie.deusvult.logger.Logger;
import com.jesus_crie.deusvult.manager.ThreadManager;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
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

    public static void awaitReactNotification(Message target, User user, Consumer<MessageReactionAddEvent> onSuccess) {
        awaitEvent(MessageReactionAddEvent.class,
                e -> e.getMessageIdLong() == target.getIdLong() && e.getUser().equals(user),
                onSuccess,
                null,
                true,
                -1);
    }

    public static <T extends Event> void awaitEvent(Class<T> clazz, Predicate<T> checker, Consumer<T> onSuccess, Runnable onTimeout, boolean singleTrigger, long timeout) {
        final AwaitListener<T> listener = new AwaitListener<>(clazz);

        Runnable task = () -> {
            listener.cancel(true);
            if (onTimeout != null)
                onTimeout.run();
        };
        final ScheduledFuture future = ThreadManager.getTimerPool().schedule(task, timeout, TimeUnit.MILLISECONDS);

        listener.setOnTrigger(e -> {
            if (checker.test(e)) {
                onSuccess.accept(e);

                if (singleTrigger) {
                    future.cancel(true);
                    return true;
                }
            }
            return false;
        });

        DeusVult.instance().getJda().addEventListener(listener);

        if (timeout > 0)
            future.cancel(true);
    }

    public static <T extends Event> T getNextEvent(Class<T> clazz, Predicate<T> checker, Consumer<T> onSuccess, Runnable onTimeout, long timeout) {
        final AwaitListener<T> listener = new AwaitListener<>(clazz);

        Runnable timeoutTask = () -> {
            listener.cancel(true);
            if (onTimeout != null)
                onTimeout.run();
        };
        final ScheduledFuture timeoutFuture = ThreadManager.getTimerPool().schedule(timeoutTask, timeout, TimeUnit.MILLISECONDS);

        listener.setOnTrigger(e -> {
            if (checker.test(e)) {
                onSuccess.accept(e);
                timeoutFuture.cancel(true);
                ThreadManager.getTimerPool().execute(timeoutTask);

                return true;
            }
            return false;
        });

        DeusVult.instance().getJda().addEventListener(listener);

        try {
            return listener.get();
        } catch (Exception e) {
            Logger.WAITER.get().trace(e);
            return null;
        }
    }
}
