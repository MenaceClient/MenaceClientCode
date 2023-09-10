package dev.menace.module.modules.render;

import dev.menace.module.Category;
import dev.menace.module.Module;

public class ItemPhysicsModule extends Module {
    public ItemPhysicsModule() {
        super("ItemPhysics", "Fancy dropped items.", Category.RENDER);
        addSettings();
    }
}
