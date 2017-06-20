package com.jesus_crie.deusvult.logger;

import java.text.SimpleDateFormat;

public class Logger {

    public static void info(String message) {
        System.out.println(getDate() + " [Info] " + message);
        DiscordLog.info(message, System.currentTimeMillis());
    }

    public static void warning(String message) {
        System.out.println(getDate() + " [Warning] " + message);
        DiscordLog.warning(message, System.currentTimeMillis());
    }

    public static void error(String message, Exception e) {
        System.err.println(getDate() + " [ERROR] " + message);
        e.printStackTrace();
        DiscordLog.error(message, e, System.currentTimeMillis());
    }

    private static String getDate() {
        return new SimpleDateFormat("[HH:mm:ss]").format(System.currentTimeMillis());
    }
}
