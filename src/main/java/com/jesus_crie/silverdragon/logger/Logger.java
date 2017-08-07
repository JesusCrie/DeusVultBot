package com.jesus_crie.silverdragon.logger;

import com.jesus_crie.silverdragon.utils.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;

public enum Logger {

    START(SimpleLogger.getLog("Start")),
    COMMAND(SimpleLogger.getLog("Command")),
    MUSIC(SimpleLogger.getLog("Music")),
    CONFIG(SimpleLogger.getLog("Config")),
    LOBBY(SimpleLogger.getLog("Lobby")),
    UNKNOWN(SimpleLogger.getLog("UNKNOWN")),
    DEV(SimpleLogger.getLog("DEV")),
    WAITER(SimpleLogger.getLog("Waiter"));

    private final SimpleLogger logger;
    Logger(SimpleLogger logger) {
        this.logger = logger;
    }

    public static boolean loggerRegistered(String name) {
        for (Logger l : values()) {
            if (l.get().name.equalsIgnoreCase(name))
                return true;
        }
        return false;
    }

    public SimpleLogger get() {
        return logger;
    }

    public static class SimpleLogger {

        private final String name;
        private final String FORMAT = "[%time%] [%level%] [%thread%] [%name%] %content%";
        private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

        private static final Map<String, SimpleLogger> loggers = new HashMap<>();
        private static final List<Listener> listeners = new ArrayList<>();

        public static void addListener(Listener l) {
            listeners.add(l);
        }

        public static void removeListener(Listener l) {
            listeners.remove(l);
        }

        public static SimpleLogger getLog(String name) {
            if (loggers.keySet().contains(name))
                return loggers.get(name);
            return new SimpleLogger(name);
        }

        private SimpleLogger(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void info(Object message) {
            log(new Log(Level.INFO, Thread.currentThread(), message));
        }

        public void debug(Object message) {
            log(new Log(Level.DEBUG, Thread.currentThread(), message));
        }

        public void warning(Object message) {
            log(new Log(Level.WARNING, Thread.currentThread(), message));
        }

        public void fatal(Object message) {
            log(new Log(Level.FATAL, Thread.currentThread(), message));
        }

        public void trace(Throwable e) {
            log(new Log(Level.FATAL, Thread.currentThread(), StringUtils.collectStackTrace(e)));
        }

        public void log(Log log) {
            String output = FORMAT.replace("%time%", dateFormat.format(new Date()))
                    .replace("%level%", log.level.name)
                    .replace("%thread%", log.threadName)
                    .replace("%name%", name)
                    .replace("%content%", log.content.toString());

            listeners.forEach(l -> l.onLog(log, this));

            if (log.level.isError)
                System.err.println(output);
            else
                System.out.println(output);
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof SimpleLogger && ((SimpleLogger) obj).name.equals(name);
        }
    }

    public static class Log {

        private final String threadName;
        private final Level level;
        private final Object content;

        public Log(Level l, Thread t, Object o) {
            threadName = t.getName();
            level = l;
            content = o;
        }

        public String getThreadName() {
            return threadName;
        }

        public Level getLevel() {
            return level;
        }

        public Object getContent() {
            return content;
        }
    }

    public interface Listener {
        void onLog(Log log, SimpleLogger logger);
    }

    public enum Level {
        INFO("Info", 0, false),
        DEBUG("Debug", 1, false),
        WARNING("Warning", 2, false),
        FATAL("Fatal", 3, true),
        UNKNOW("UNKNOW", 4, true);

        private final String name;
        private final int priority;
        private final boolean isError;
        Level(String name, int priority, boolean isError) {
            this.name = name;
            this.priority = priority;
            this.isError = isError;
        }

        public int getPriority() {
            return priority;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
