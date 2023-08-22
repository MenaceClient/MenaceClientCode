package dev.menace.event.events;

public class EventJump extends Event {

    float motionFactor;

    public EventJump(float motionFactor) {
        this.motionFactor = motionFactor;
    }

    public float getMotionFactor() {
        return motionFactor;
    }

    public void setMotionFactor(float motionFactor) {
        this.motionFactor = motionFactor;
    }

}
