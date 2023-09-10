package dev.menace.module.modules.player;

import dev.menace.module.Category;
import dev.menace.module.Module;
import dev.menace.module.settings.BooleanSetting;

public class SafeWalkModule extends Module {

    public BooleanSetting inAir = new BooleanSetting("InAir", true, false);

    public SafeWalkModule() {
        super("SafeWalk", "Stops you from falling off the edge of blocks", Category.PLAYER);
        addSettings(inAir);
    }

}
