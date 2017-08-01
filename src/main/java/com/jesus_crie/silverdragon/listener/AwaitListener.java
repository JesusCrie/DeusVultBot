package com.jesus_crie.silverdragon.listener;

import com.jesus_crie.silverdragon.SilverDragon;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.hooks.EventListener;

import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

public class AwaitListener<T extends Event> extends CompletableFuture<T> implements EventListener {

    private final Class<T> clazz;
    private Predicate<T> onTrigger;

    public AwaitListener(Class<T> clazz) {
        onTrigger = (e) -> false;
        this.clazz = clazz;
    }

    public void setOnTrigger(Predicate<T> onTrigger) {
        this.onTrigger = onTrigger;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onEvent(Event event) {
        if (event.getClass().getName().equals(clazz.getName())) {
            if (onTrigger.test((T) event)) {
                event.getJDA().removeEventListener(this);
                complete((T) event);
            }
        }
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        SilverDragon.instance().getJDA().removeEventListener(this);
        return true;
    }
}
