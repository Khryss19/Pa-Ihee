package util;

/** Small helpers for building the text shown on screen. */
public final class TextUtil {

    private TextUtil() {
    }

    /** 1 -> "1st", 2 -> "2nd", 3 -> "3rd", 4 -> "4th", 11 -> "11th" */
    public static String ordinal(int n) {
        int mod100 = n % 100;
        if (mod100 >= 11 && mod100 <= 13) {
            return n + "th";
        }
        switch (n % 10) {
            case 1:
                return n + "st";
            case 2:
                return n + "nd";
            case 3:
                return n + "rd";
            default:
                return n + "th";
        }
    }

    /** countOf(1, "Issue") -> "1 Issue", countOf(34, "Issue") -> "34 Issues" */
    public static String countOf(long count, String singular) {
        return count + " " + singular + (count == 1 ? "" : "s");
    }
}
