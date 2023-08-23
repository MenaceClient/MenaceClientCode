package dev.menace.utils.math;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

public class MathUtils {

    private static final int MIN_DELAY_MS = 100; // Minimum delay between clicks in milliseconds
    private static final int MAX_DELAY_MS = 300; // Maximum delay between clicks in milliseconds

    private static final Random random = new Random();

    /**
     * Checks if mouse is over rectangle.
     *
     * @param x
     * @param y
     * @param width RELATIVE to x.
     * @param height RELATIVE to y.
     * @param mouseX
     * @param mouseY
     * @return true if rectangle is hovered
     */
    public static boolean isMouseHovered(double x, double y, int width, int height, int mouseX, int mouseY) {
        return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
    }

    /**
     * Checks if mouse is over rectangle.
     *
     * @param x
     * @param y
     * @param x1
     * @param y1
     * @param mouseX
     * @param mouseY
     * @return true if rectangle is hovered
     */
    public static boolean isMouseHovered2(double x, double y, int x1, int y1, int mouseX, int mouseY) {
        return mouseX >= x && mouseY >= y && mouseX < x1 && mouseY < y1;
    }

    public static int randInt(int i, int length) {
        return (random.nextInt() * (length - i)) + i;
    }

    public static double roundToPlace(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }


    public static double snapToStep(double value, double valueStep) {
        if (valueStep > 0.0F)
            value = valueStep * (double) Math.round(value / valueStep);

        return value;
    }

    public static double getDistanceBetweenAngles(double angle1, double angle2) {
        double angle = Math.abs(angle1 - angle2) % 360.0D;
        if (angle > 180.0D) {
            angle = 360.0D - angle;
        }

        return angle;
    }

    public static int calculateDelay(int aps) {
        double baseDelay = 1000.0 / aps;
        double randomFactor = random.nextDouble(); // Random value between 0 and 1
        double delayWithVariation = baseDelay + (baseDelay * randomFactor);

        return (int) Math.min(Math.max(delayWithVariation, MIN_DELAY_MS), MAX_DELAY_MS);
    }

}
