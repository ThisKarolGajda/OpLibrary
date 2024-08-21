package me.opkarol.oplibrary.tools;

import me.opkarol.oplibrary.injection.IgnoreInject;

@IgnoreInject
public class TimeUtils {
    public static long getCurrent() {
        return System.currentTimeMillis();
    }

    public static long addToTimestamp(long timestamp, long value, TimeUnit unit) {
        return timestamp + (value * unit.toMilliseconds());
    }

    public static long subtractFromTimestamp(long timestamp, long value, TimeUnit unit) {
        return timestamp - (value * unit.toMilliseconds());
    }

    public static long subtractFromCurrent(long timestamp) {
        return timestamp - System.currentTimeMillis();
    }

    public static boolean hasTimePassed(long milliseconds) {
        return System.currentTimeMillis() > milliseconds;
    }

    public static long addToCurrent(long value, TimeUnit unit) {
        return addToTimestamp(System.currentTimeMillis(), value, unit);
    }

    public static boolean willTimePass(long timestamp, long value, TimeUnit unit) {
        long timeToPass = value * unit.toMilliseconds();
        long currentTimeMillis = System.currentTimeMillis();
        return currentTimeMillis > timestamp - timeToPass;
    }

    public enum TimeUnit {
        SECOND(1000),
        MINUTE(60000),
        HOUR(3600000),
        DAY(86400000),
        WEEK(604800000);

        private final long milliseconds;

        TimeUnit(long milliseconds) {
            this.milliseconds = milliseconds;
        }

        public long toMilliseconds() {
            return milliseconds;
        }

        public long toSeconds() {
            return milliseconds / 1000;
        }
    }
}
