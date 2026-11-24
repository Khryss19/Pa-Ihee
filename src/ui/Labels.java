package ui;

import model.ComfortRoom;
import model.IssueReport;
import model.MaintenanceRecord;
import util.TextUtil;
import util.TimeUtil;

import java.time.Duration;
import java.time.LocalDateTime;

/** Builds the short pieces of text the screens show, so every screen words them the same way. */
public final class Labels {

    private Labels() {
    }

    /** "2nd Floor" */
    public static String floor(int floor) {
        return TextUtil.ordinal(floor) + " Floor";
    }

    /** "2nd Floor - Men's CR" (with an en dash) */
    public static String room(ComfortRoom room) {
        return floor(room.getFloor()) + " \u2013 " + room.getName();
    }

    /** "Men's" (the room name without " CR"; used where space is tight) */
    public static String roomShort(ComfortRoom room) {
        String name = room.getName();
        return name.endsWith(" CR") ? name.substring(0, name.length() - 3) : name;
    }

    /** "2nd Floor - Men's CR * Stall 1" */
    public static String location(IssueReport report) {
        return room(report.getRoom()) + " " + Theme.BULLET + " " + report.getFixtureName();
    }

    /** "5 mins ago" for recent times, otherwise the program's shared date format ("Oct 7, 2026 4:30 PM"). */
    public static String when(LocalDateTime time) {
        long days = Duration.between(time, LocalDateTime.now()).toDays();
        return days < 7 ? TimeUtil.ago(time) : MaintenanceRecord.format(time);
    }
}
