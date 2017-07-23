package com.jesus_crie.deusvult.utils;

import com.jesus_crie.deusvult.DeusVult;
import com.jesus_crie.deusvult.listener.AwaitListener;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

public class Awaiter {

    public static void awaitMessageFromUser(MessageChannel channel, User user, Consumer<MessageReceivedEvent> success, Runnable fail, long timeout) {
        Timer timer = new Timer();

        AwaitListener<MessageReceivedEvent> listener = new AwaitListener<>(e -> {
            if (e.getChannel().equals(channel) && e.getAuthor().equals(user)) {
                timer.cancel();
                success.accept(e);
                return true;
            }
            return false;
        }, MessageReceivedEvent.class);

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                DeusVult.instance().getJda().removeEventListener(listener);
                fail.run();
            }
        };

        timer.schedule(task, timeout);

        DeusVult.instance().getJda().addEventListener(listener);
    }
}
