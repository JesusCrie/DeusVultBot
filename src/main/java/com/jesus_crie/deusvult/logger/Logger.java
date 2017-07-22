package com.jesus_crie.deusvult.logger;

import net.dv8tion.jda.core.utils.SimpleLog;

public enum Logger {

    START(SimpleLog.getLog("Start")),
    COMMAND(SimpleLog.getLog("Command")),
    MUSIC(SimpleLog.getLog("Music"));

    private SimpleLog logger;
    Logger(SimpleLog logger) {
        this.logger = logger;
    }

    public SimpleLog get() {
        return logger;
    }
}
