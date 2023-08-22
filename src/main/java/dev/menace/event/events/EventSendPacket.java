package dev.menace.event.events;

import net.minecraft.network.Packet;

public class EventSendPacket extends Event {

    Packet<?> packet;

    public EventSendPacket(Packet<?> packet) {
        this.packet = packet;
    }

    public Packet<?> getPacket() {
        return packet;
    }

}
