package dev.menace.module.modules.world;

import dev.menace.event.Listener;
import dev.menace.event.annotations.EventLink;
import dev.menace.event.events.EventUpdate;
import dev.menace.module.Category;
import dev.menace.module.Module;

public class FastPlaceModule extends Module {
    public FastPlaceModule() {
        super("FastPlace", "Blocks go brrrrrrrrrr", Category.WORLD);
    }

    @EventLink
    public Listener<EventUpdate> onUpdate = event -> mc.rightClickDelayTimer = 0;

}
