package dev.menace.event.events;

import net.minecraft.entity.Entity;

public class EventAttack extends Event {

    Entity target;

    public EventAttack(Entity target) {
        this.target = target;
    }

    public Entity getTarget() {
        return target;
    }
}
