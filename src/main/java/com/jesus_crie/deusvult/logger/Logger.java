package com.jesus_crie.deusvult.logger;

import net.dv8tion.jda.core.utils.SimpleLog;

public enum Logger {

    START(SimpleLog.getLog("Start")),
    COMMAND(SimpleLog.getLog("Command")),
    MUSIC(SimpleLog.getLog("Music")),
    CONFIG(SimpleLog.getLog("Config")),
    TEAM(SimpleLog.getLog("Team"));

    private SimpleLog logger;
    Logger(SimpleLog logger) {
        this.logger = logger;
    }

    public static boolean loggerRegistered(String name) {
        for (Logger l : values()) {
            if (l.get().name.equalsIgnoreCase(name))
                return true;
        }
        return false;
    }

    public SimpleLog get() {
        return logger;
    }
}
