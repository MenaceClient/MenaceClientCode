package dev.menace.module.modules.player;

import dev.menace.Menace;
import dev.menace.event.Listener;
import dev.menace.event.annotations.EventLink;
import dev.menace.event.events.Event;
import dev.menace.event.events.EventReceivePacket;
import dev.menace.event.events.EventSlowdown;
import dev.menace.module.Category;
import dev.menace.module.Module;
import dev.menace.module.settings.ListSetting;
import dev.menace.utils.player.MovementUtils;
import dev.menace.utils.player.PacketUtils;
import net.minecraft.network.play.server.S30PacketWindowItems;

public class NoSlowModule extends Module {

    ListSetting mode = new ListSetting("Mode", true, "Vanilla", "Vanilla", "NCP");

    public NoSlowModule() {
        super("NoSlow", "Allows you to walk normally while blocking your sword.", Category.PLAYER);
        addSettings(mode);
    }

    @EventLink
    Listener<EventReceivePacket> onRecievePacket = event -> {
        if (mc.thePlayer == null || mc.theWorld == null || !mode.getValue().equalsIgnoreCase("NCP")) return;
        if((mc.thePlayer.isBlocking() || Menace.instance.moduleManager.killauraModule.blocking) && MovementUtils.isMoving() && event.getPacket() instanceof S30PacketWindowItems) {
            PacketUtils.sendBlocking(false);
            event.setCancelled(true);
        }
    };

    @EventLink
    Listener<EventSlowdown> onSlowdown = Event::cancel;
}
