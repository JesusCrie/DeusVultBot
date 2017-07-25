package com.jesus_crie.deusvult.utils;

import com.jesus_crie.deusvult.DeusVult;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

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
        return TimeUnit.SECONDS.toMillis(second);
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

    public static long calc(long i, TimeUnit unit) {
        return unit.toMillis(i);
    }

    public static String getUptime() {
        String[] data = OffsetDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis() - DeusVult.instance().getStart()), ZoneId.of("+0"))
                .format(DateTimeFormatter.ofPattern("DD/HH/mm")).split("/");
        data[0] = String.valueOf(Integer.parseInt(data[0]) - 1);
        return S.GENERAL_UPTIME_PATTERN.format((Object[]) data);
    }
}
