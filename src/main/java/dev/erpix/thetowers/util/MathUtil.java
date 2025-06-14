package dev.erpix.thetowers.util;

/**
 * Utility class for some mathematical operations.
 */
public final class MathUtil {

    private MathUtil() { }

    /**
     * Returns the value rounded down to the specified number of decimal places.
     *
     * @param value  the value to round
     * @param places the number of decimal places to round to
     * @return the rounded value
     */
    public static double floor(double value, int places) {
        double scale = scale(places);
        return Math.floor(value * scale) / scale;
    }

    /**
     * Returns the value rounded to the specified number of decimal places.
     *
     * @param value  the value to round
     * @param places the number of decimal places to round to
     * @return the rounded value
     */
    public static double round(double value, int places) {
        double scale = scale(places);
        return Math.round(value * scale) / scale;
    }

    /**
     * Returns the value rounded up to the specified number of decimal places.
     *
     * @param value  the value to round
     * @param places the number of decimal places to round to
     * @return the rounded value
     */
    public static double ceil(double value, int places) {
        double scale = scale(places);
        return Math.ceil(value * scale) / scale;
    }

    /**
     * Calculates the scale factor based on the number of decimal places.
     *
     * @param places the number of decimal places
     * @return the scale factor
     */
    public static double scale(int places) {
        return Math.pow(10, places);
    }

    /**
     * Generates a random double value between the specified minimum and maximum values.
     *
     * @param min Minimum value
     * @param max Maximum value
     * @return A random double value between min and max
     */
    public static double randomDouble(double min, double max) {
        return min + (Math.random() * (max - min));
    }

}
