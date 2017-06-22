package com.jesus_crie.deusvult.manager;

import java.util.Timer;
import java.util.TimerTask;

public class TimerManager {

    private static Timer timer = new Timer();

    public static void doLater(Runnable action, long delayMillis) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                action.run();
            }
        }, delayMillis);
    }

    public static void doAndRepeatLater(Runnable action, long periodMillis) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                action.run();
            }
        }, 0, periodMillis);
    }

    public static void doLaterAndRepeatLater(Runnable action, long delayMillis, long periodMillis) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                action.run();
            }
        }, delayMillis, periodMillis);
    }

    public static void cleanup() {
        timer.cancel();
        timer.purge();
    }
}
