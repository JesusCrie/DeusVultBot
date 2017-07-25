package com.jesus_crie.deusvult.manager;

import com.jesus_crie.deusvult.logger.Logger;
import com.jesus_crie.deusvult.utils.StringUtils;

import java.util.concurrent.*;

public class ThreadManager {

    private static final ExecutorService commandPool;
    private static final ExecutorService generalPool;
    private static final ScheduledExecutorService timerPool;

    static {
        commandPool = Executors.newCachedThreadPool(new NamedThreadFactory("Command",
                (t, e) -> Logger.COMMAND.get().log(new Logger.Log(Logger.Level.FATAL, t, StringUtils.collectStackTrace(e)))));
        generalPool = Executors.newCachedThreadPool(new NamedThreadFactory("General",
                (t, e) -> Logger.UNKNOWN.get().log(new Logger.Log(Logger.Level.FATAL, t, StringUtils.collectStackTrace(e)))));
        timerPool = Executors.newScheduledThreadPool(10, new NamedThreadFactory("Timer",
                (t, e) -> Logger.UNKNOWN.get().log(new Logger.Log(Logger.Level.FATAL, t, StringUtils.collectStackTrace(e)))));
    }


    public static void cleanUp() {
        try {
            commandPool.awaitTermination(1, TimeUnit.SECONDS);
            generalPool.awaitTermination(1, TimeUnit.SECONDS);
            timerPool.shutdownNow();
        } catch (InterruptedException ignore) {}
    }

    public static ExecutorService getCommandPool() {
        return commandPool;
    }

    public static ExecutorService getGeneralPool() {
        return generalPool;
    }

    public static ScheduledExecutorService getTimerPool() {
        return timerPool;
    }

    private static class NamedThreadFactory implements ThreadFactory {

        private final String name;
        private final Thread.UncaughtExceptionHandler handler;

        public NamedThreadFactory(String name, Thread.UncaughtExceptionHandler handler) {
            this.name = name;
            this.handler = handler;
        }

        @Override
        public Thread newThread(Runnable r) {
            final Thread t = new Thread(r);
            t.setName("DeusVult-" + name + "#" + t.getId());
            t.setUncaughtExceptionHandler(handler);
            return t;
        }
    }
}
