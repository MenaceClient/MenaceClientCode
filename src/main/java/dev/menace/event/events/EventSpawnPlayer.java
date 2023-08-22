package dev.menace.event.events;

import net.minecraft.entity.player.EntityPlayer;

public class EventSpawnPlayer extends Event {

    EntityPlayer player;

    public EventSpawnPlayer(EntityPlayer player) {
        this.player = player;
    }

    public EntityPlayer getPlayer() {
        return player;
    }

}
