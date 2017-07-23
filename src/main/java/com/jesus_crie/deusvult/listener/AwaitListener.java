package com.jesus_crie.deusvult.listener;

import com.jesus_crie.deusvult.DeusVult;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.function.Predicate;

public class AwaitListener<T extends Event> extends ListenerAdapter {

    private Class<T> clazz;
    private Predicate<T> onTrigger;

    private boolean active = true;

    public AwaitListener(Predicate<T> onTrigger, Class<T> clazz) {
        this.onTrigger = onTrigger;
        this.clazz = clazz;
    }

    public void disable() {
        active = false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onGenericEvent(Event event) {
        if (!active)
            return;

        if (event.getClass().getName().equals(clazz.getName()))
            if (onTrigger.test((T) event))
                DeusVult.instance().getJda().removeEventListener(this);
    }
}
