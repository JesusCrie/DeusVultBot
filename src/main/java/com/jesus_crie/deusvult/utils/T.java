package com.jesus_crie.deusvult.utils;

public class T {

    public static final long SECOND = 1000L;
    public static final long MINUTE = SECOND * 60;
    public static final long HOUR = MINUTE * 60;
    public static final long DAY = HOUR * 24;
    public static final long YEAR = DAY * 365;

    public static long calc(long second, long minute, long hour, long day, long year) {
        return SECOND * second
                + MINUTE * minute
                + HOUR * hour
                + DAY * day
                + YEAR * year;
    }

    public static long calc(long second) {
        return calc(second, 0, 0, 0, 0);
    }

    public static long calc(long second, long minute) {
        return calc(second, minute, 0, 0, 0);
    }

    public static long calc(long second, long minute, long hour) {
        return calc(second, minute, hour, 0, 0);
    }

    public static long calc(long second, long minute, long hour, long day) {
        return calc(second, minute, hour, day, 0);
    }
}
