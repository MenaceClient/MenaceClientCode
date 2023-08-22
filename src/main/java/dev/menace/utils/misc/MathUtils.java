package dev.menace.utils.misc;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MathUtils {

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
        return (int) (Math.random() * (length - i)) + i;
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
}
