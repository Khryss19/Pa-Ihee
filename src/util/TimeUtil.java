package util;

import java.time.Duration;
import java.time.LocalDateTime;

/** Turns times into the short phrases used by the UI ("5 mins ago"). */
public final class TimeUtil {

    private TimeUtil() {
    }

    public static String ago(LocalDateTime time) {
        long seconds = Duration.between(time, LocalDateTime.now()).getSeconds();
        if (seconds < 60) {
            return "Just now";
        }
        long minutes = seconds / 60;
        if (minutes < 60) {
            return minutes == 1 ? "1 min ago" : minutes + " mins ago";
        }
        long hours = minutes / 60;
        if (hours < 24) {
            return hours == 1 ? "1 hour ago" : hours + " hours ago";
        }
        long days = hours / 24;
        return days == 1 ? "1 day ago" : days + " days ago";
    }

    /** 39 -> "39 mins", 1 -> "1 min" */
    public static String minutes(long minutes) {
        return minutes == 1 ? "1 min" : minutes + " mins";
    }
}
