package dev.menace.event.events;

import net.minecraft.network.play.client.C03PacketPlayer;

public class EventTeleport extends Event {

    C03PacketPlayer.C06PacketPlayerPosLook response;
    double x, y, z;
    float yaw, pitch;

    public EventTeleport(C03PacketPlayer.C06PacketPlayerPosLook response, double x, double y, double z, float yaw, float pitch) {
        this.response = response;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public C03PacketPlayer.C06PacketPlayerPosLook getResponse() {
        return response;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

}
