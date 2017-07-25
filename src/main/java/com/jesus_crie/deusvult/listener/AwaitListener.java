package com.jesus_crie.deusvult.listener;

import com.jesus_crie.deusvult.DeusVult;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.function.Predicate;

public class AwaitListener<T extends Event> extends ListenerAdapter {

    private final Class<T> clazz;
    private Predicate<T> onTrigger;

    private boolean active = true;

    public AwaitListener(Class<T> clazz) {
        onTrigger = (e) -> false;
        this.clazz = clazz;
    }

    public void setOnTrigger(Predicate<T> onTrigger) {
        this.onTrigger = onTrigger;
    }

    public void disable() {
        active = false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onGenericEvent(Event event) {
        if (!active)
            return;

        if (event.getClass().getName().equals(clazz.getName())) {
            if (onTrigger.test((T) event))
                DeusVult.instance().getJda().removeEventListener(this);
        }
    }
}
