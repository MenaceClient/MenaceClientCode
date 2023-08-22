package dev.menace.event.events;

public class EventRender2D extends Event {

    int scaledWidth, scaledHeight;
    float partialTicks;

    public EventRender2D(int scaledWidth, int scaledHeight, float partialTicks) {
        this.scaledWidth = scaledWidth;
        this.scaledHeight = scaledHeight;
        this.partialTicks = partialTicks;
    }

    public int getScaledWidth() {
        return scaledWidth;
    }

    public int getScaledHeight() {
        return scaledHeight;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

}
