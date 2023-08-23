package dev.menace.module.modules.movement;

import dev.menace.event.Listener;
import dev.menace.event.annotations.EventLink;
import dev.menace.event.events.EventUpdate;
import dev.menace.module.Category;
import dev.menace.module.Module;
import dev.menace.module.settings.BooleanSetting;
import dev.menace.utils.player.MovementUtils;
import dev.menace.utils.player.SprintHandler;
import net.minecraft.potion.Potion;

public class SprintModule extends Module {

    public BooleanSetting omni = new BooleanSetting("OmniSprint", true, false);

    public SprintModule() {
        super("Sprint", "Sprints for you", Category.MOVEMENT);
        addSettings();
    }

    @EventLink
    Listener<EventUpdate> onUpdate = event -> {
        if (MovementUtils.isMoving() && mc.thePlayer.moveForward > 0 && !mc.thePlayer.isCollidedHorizontally && !mc.thePlayer.isUsingItem() && !mc.thePlayer.isPotionActive(Potion.blindness) && mc.thePlayer.getFoodStats().getFoodLevel() > 6) {
            SprintHandler.setSprinting(true, 1);
        }
    };

}
