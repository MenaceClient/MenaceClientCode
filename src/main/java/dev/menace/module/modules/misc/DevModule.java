package dev.menace.module.modules.misc;

import dev.menace.event.Listener;
import dev.menace.event.annotations.EventLink;
import dev.menace.event.events.EventPreMotion;
import dev.menace.event.events.EventSendPacket;
import dev.menace.module.Category;
import dev.menace.module.Module;
import dev.menace.utils.bypass.BypassUtils;
import net.minecraft.network.play.client.C03PacketPlayer;

public class DevModule extends Module {

    int i = 0;
    boolean m = false;

    public DevModule() {
        super("DevModule", "Cybermanfan likes men.", Category.MISC);
        addSettings();
    }

    @Override
    public void onEnable() {
        i = 0;
        m = false;
        super.onEnable();
    }

    @EventLink
    Listener<EventPreMotion> onPreMotion = event -> {
        if (i < 100) {
            if (!m) {
                event.setY(event.getY() + 0.0625);
            }
            m = !m;
            event.setOnGround(false);
            i++;
        } else {
            event.setOnGround(true);
            this.toggle();
        }
    };

    @EventLink
    Listener<EventSendPacket> onSendPacket = event -> {

    };

}
