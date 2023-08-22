package dev.menace.utils.render;

import java.awt.*;

public class ColorUtils {

    public static int getRainbow(float seconds, float saturation, float brightness) {
        float hue = (System.currentTimeMillis() % (int)(seconds * 1000)) / (float)(seconds * 1000);
        return Color.HSBtoRGB(hue, saturation, brightness);
    }

    public static Color cleanRainbow(float speed, float off) {
        double time = (double) System.currentTimeMillis() / speed;
        time += off;
        time %= 255.0f;
        return Color.getHSBColor((float) (time / 255.0f), 1.0f, 1.0f);
    }

    public static Color fade(Color color, Color color2, int off) {
        double time = (double) (Math.abs((System.currentTimeMillis()) / 10) + off) / 140;
        time %= 1.0; // Normalize time to the range [0, 1]

        // Interpolate between color1 and color2 based on time
        int red = (int) (color.getRed() * (1 - time) + color2.getRed() * time);
        int green = (int) (color.getGreen() * (1 - time) + color2.getGreen() * time);
        int blue = (int) (color.getBlue() * (1 - time) + color2.getBlue() * time);

        return new Color(red, green, blue);
    }

    public static Color setAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    public static Color setAlpha(Color color, float alpha) {
        return new Color((float) color.getRed() / 255, (float) color.getGreen() / 255, (float) color.getBlue() / 255, alpha);
    }

}
