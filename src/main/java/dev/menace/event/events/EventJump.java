package dev.menace.event.events;

public class EventJump extends Event {

    double motionFactor;
    float yaw;
    boolean boosting;

    public EventJump(double motionFactor, float yaw, boolean boosting) {
        this.motionFactor = motionFactor;
        this.yaw = yaw;
        this.boosting = boosting;
    }

    public double getMotionFactor() {
        return motionFactor;
    }

    public void setMotionFactor(double motionFactor) {
        this.motionFactor = motionFactor;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public boolean isBoosting() {
        return boosting;
    }

    public void setBoosting(boolean boosting) {
        this.boosting = boosting;
    }

}
