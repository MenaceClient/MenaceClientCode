package dev.menace.event.events;

public class EventSlowdown extends Event {

    float forwardMultiplier, strafeMultiplier;

    public EventSlowdown(float forwardMultiplier, float strafeMultiplier) {
        this.forwardMultiplier = forwardMultiplier;
        this.strafeMultiplier = strafeMultiplier;
    }

    public float getForwardMultiplier() {
        return forwardMultiplier;
    }

    public void setForwardMultiplier(float forwardMultiplier) {
        this.forwardMultiplier = forwardMultiplier;
    }

    public float getStrafeMultiplier() {
        return strafeMultiplier;
    }

    public void setStrafeMultiplier(float strafeMultiplier) {
        this.strafeMultiplier = strafeMultiplier;
    }

}
