package dev.menace.event.events;

public class EventStrafe extends Event {

    private float forward, strafe;
    private float friction, attributeSpeed;
    private float yaw;

    public EventStrafe(float forward, float strafe, float friction, float attributeSpeed, float yaw) {
        this.forward = forward;
        this.strafe = strafe;
        this.friction = friction;
        this.attributeSpeed = attributeSpeed;
        this.yaw = yaw;
    }

    public float getForward() {
        return forward;
    }

    public void setForward(float forward) {
        this.forward = forward;
    }

    public float getStrafe() {
        return strafe;
    }

    public void setStrafe(float strafe) {
        this.strafe = strafe;
    }

    public float getFriction() {
        return friction;
    }

    public void setFriction(float friction) {
        this.friction = friction;
    }

    public float getAttributeSpeed() {
        return attributeSpeed;
    }

    public void setAttributeSpeed(float attributeSpeed) {
        this.attributeSpeed = attributeSpeed;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

}
