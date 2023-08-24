package dev.menace.module.modules.render;

import dev.menace.Menace;
import dev.menace.event.Listener;
import dev.menace.event.annotations.EventLink;
import dev.menace.event.events.EventRender3D;
import dev.menace.module.Category;
import dev.menace.module.Module;
import dev.menace.module.settings.BooleanSetting;
import dev.menace.utils.player.EntityFakePlayer;
import dev.menace.utils.render.ESPUtils;
import net.minecraft.entity.player.EntityPlayer;

public class ESPModule extends Module {

    BooleanSetting healthBar = new BooleanSetting("Health Bar", true, true);

    public ESPModule() {
        super("ESP", "Allows you to see players through walls", Category.RENDER);
        addSettings(healthBar);
    }

    @EventLink
    Listener<EventRender3D> onRender3D = event -> {
        for (EntityPlayer entity : mc.theWorld.playerEntities) {
            if (entity != mc.thePlayer && entity.isEntityAlive() && !(entity instanceof EntityFakePlayer)) {
                ESPUtils.draw2DESP(entity);

                if (healthBar.getValue()) {
                    ESPUtils.drawEntityHealthBar(entity);
                }
            }
        }
    };

}
