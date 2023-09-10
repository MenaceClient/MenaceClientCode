package dev.menace.module.modules.render;

import dev.menace.event.Listener;
import dev.menace.event.annotations.EventLink;
import dev.menace.event.events.EventReceivePacket;
import dev.menace.event.events.EventUpdate;
import dev.menace.module.Category;
import dev.menace.module.Module;
import dev.menace.module.settings.NumberSetting;
import net.minecraft.network.play.server.S03PacketTimeUpdate;

public class TimeChangerModule extends Module {

    NumberSetting time = new NumberSetting("Time", true, 100, 0, 20000, 100, true);

    public TimeChangerModule() {
        super("TimeChanger", "", Category.RENDER);
        addSettings(time);
    }

    @EventLink
    Listener<EventReceivePacket> onRecievePacket = event -> {
        if (event.getPacket() instanceof S03PacketTimeUpdate) {
            event.setCancelled(true);
        }
    };

    @EventLink
    Listener<EventUpdate> onUpdate = event -> {
        mc.theWorld.setWorldTime(time.getValueL());
    };

}
