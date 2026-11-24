package ui;

import java.awt.Color;
import java.awt.Font;

/** Colors, fonts and symbols taken from the Pa-Ihee design. Change them here to restyle the whole app. */
public final class Theme {

    private Theme() {
    }

    public static final int WINDOW_WIDTH = 390;
    public static final int WINDOW_HEIGHT = 780;

    // ----- colors -----
    public static final Color BACKGROUND = new Color(0xD9F3F1);   // light teal page background
    public static final Color DARK = new Color(0x1E3D3A);         // dark green (buttons, banners)
    public static final Color DARK_HOVER = new Color(0x2C5651);
    public static final Color WHITE = Color.WHITE;
    public static final Color TEXT = new Color(0x1A2B29);
    public static final Color MUTED = new Color(0x5F7370);
    public static final Color HINT = new Color(0x9BADAA);
    public static final Color BORDER = new Color(0xC9DEDB);
    public static final Color FIELD_FILL = new Color(0xF5FAF9);
    public static final Color SOFT_TEXT = new Color(0xB9D6D2);    // small text on the dark banner
    public static final Color HIGHLIGHT = new Color(0xE3F4F2);
    public static final Color INFO_BOX = new Color(0xEEF6F5);

    public static final Color GREEN_BG = new Color(0xD7F5E1);
    public static final Color GREEN_FG = new Color(0x1F8A4C);
    public static final Color RED_BG = new Color(0xFCE3E3);
    public static final Color RED_FG = new Color(0xC23B32);
    public static final Color ORANGE_BG = new Color(0xFFEBD3);
    public static final Color ORANGE_FG = new Color(0xB45309);

    // ----- fonts -----
    public static final Font DISPLAY = new Font(Font.SANS_SERIF, Font.BOLD, 24);
    public static final Font TITLE = new Font(Font.SANS_SERIF, Font.BOLD, 18);
    public static final Font HEADING = new Font(Font.SANS_SERIF, Font.BOLD, 15);
    public static final Font BODY = new Font(Font.SANS_SERIF, Font.PLAIN, 13);
    public static final Font BODY_BOLD = new Font(Font.SANS_SERIF, Font.BOLD, 13);
    public static final Font SMALL = new Font(Font.SANS_SERIF, Font.PLAIN, 11);
    public static final Font SMALL_BOLD = new Font(Font.SANS_SERIF, Font.BOLD, 11);
    public static final Font CHIP = new Font(Font.SANS_SERIF, Font.BOLD, 12);
    public static final Font STAT = new Font(Font.SANS_SERIF, Font.BOLD, 22);

    // ----- text -----
    public static final String TAGLINE = "Never stall when nature calls.";

    // Unicode escapes keep the source files plain ASCII, so they compile the same on every computer.
    public static final String BULLET = "\u2022";
    public static final String DASH = "\u2013";
    public static final String BACK = "\u2190";
    public static final String CHEVRON = "\u203A";
}
