package com.jesus_crie.deusvult.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class TimerManager {

    private static List<Timer> timers = new ArrayList<>();

    public static Timer create() {
        Timer t = new Timer();
        timers.add(t);
        return t;
    }

    public static void cleanUp() {
        timers.forEach(Timer::cancel);
    }
}
