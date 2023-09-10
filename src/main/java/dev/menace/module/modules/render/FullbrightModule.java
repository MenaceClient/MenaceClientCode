package dev.menace.module.modules.render;

import dev.menace.event.Listener;
import dev.menace.event.annotations.EventLink;
import dev.menace.event.events.EventUpdate;
import dev.menace.module.Category;
import dev.menace.module.Module;

public class FullbrightModule extends Module {

    float oldGamma;

    public FullbrightModule() {
        super("Fullbright", "Changes the world light level.", Category.RENDER);
    }

    @Override
    public void onEnable() {
        oldGamma = mc.gameSettings.gammaSetting;
        super.onEnable();
    }

    @EventLink
    Listener<EventUpdate> onUpdate = event -> mc.gameSettings.gammaSetting = 10F;

    @Override
    public void onDisable() {
        mc.gameSettings.gammaSetting = oldGamma;
        super.onDisable();
    }
}
