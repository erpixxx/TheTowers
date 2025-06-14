package dev.erpix.thetowers.util;

/**
 * Utility class containing color constants used in the plugin.
 */
public final class Colors {

    private Colors() { }

    public static final String PRIMARY = "31EE92";
    public static final String SECONDARY = "7CE9D1";

    public static String format(String color) {
        return "<color:#" + color + ">";
    }

}
