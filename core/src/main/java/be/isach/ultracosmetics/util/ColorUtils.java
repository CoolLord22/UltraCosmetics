package be.isach.ultracosmetics.util;

import java.awt.*;

public class ColorUtils {
    /**
     * Generates a color in the rainbow sequence based on the current time. Use this method to get the smoothest
     * transition between all colors of the rainbow.
     *
     * @return a Color representing the color for the current time.
     */
    public static Color getRainbowColor() {
        return getRainbowColor(2);
    }

    /**
     * Generates a color in the rainbow sequence based on the current time.
     *
     * @param secondsPerPhase number of seconds that each rainbow phase will last. There are a total of 6 phases. So,
     *                        this number multiplied by 6 will be the total amount of time until the colors start
     *                        repeating themselves.
     *
     * @return a Color representing the color for the current time.
     */
    public static Color getRainbowColor(double secondsPerPhase) {
        long currentEpochMilliseconds = System.currentTimeMillis();
        long currentEpochSeconds = currentEpochMilliseconds / 1000L;

        int phase = (int) ((currentEpochSeconds % (6 * secondsPerPhase)) / secondsPerPhase);
        double millisecondsToAdjustBy = currentEpochMilliseconds % (secondsPerPhase * 1000);

        // There's a change of a specific number of rgb values in a single tick. This calculation is basically the
        // current tick (or milliseconds) multiplied by 255 to get the rgb value.
        int currentRotatingRgbValue = (int) (millisecondsToAdjustBy * 0.255 / secondsPerPhase);

        int red = 0;
        int green = 0;
        int blue = 0;

        // There are six phases in a rainbow when adjusting rgb values. This if-else encapsulates those six phases.
        if (phase == 0) { // Red is at 255, Green goes to 255
            red = 255;
            green = currentRotatingRgbValue;
        } else if (phase == 1) { // Red goes to 0
            red = 255 - currentRotatingRgbValue;
            green = 255;
        } else if (phase == 2) { // Blue goes to 255
            green = 255;
            blue = currentRotatingRgbValue;
        } else if (phase == 3) { // Green goes to 0
            green = 255 - currentRotatingRgbValue;
            blue = 255;
        } else if (phase == 4) { // Red goes to 255
            red = currentRotatingRgbValue;
            blue = 255;
        } else if (phase == 5) { // Blue goes to 0
            red = 255;
            blue = 255 - currentRotatingRgbValue;
        }

        return new Color(red, green, blue);
    }
}
