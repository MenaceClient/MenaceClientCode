package dev.menace.module.modules.player;

import dev.menace.event.Listener;
import dev.menace.event.annotations.EventLink;
import dev.menace.event.events.EventUpdate;
import dev.menace.module.Category;
import dev.menace.module.Module;
import dev.menace.module.settings.ListSetting;
import dev.menace.utils.player.MovementUtils;
import dev.menace.utils.player.PacketUtils;
import dev.menace.utils.player.PlayerUtils;
import net.minecraft.network.play.client.C03PacketPlayer;

public class AntiVoidModule extends Module {

    private double lastOnGroundX;
    private double lastOnGroundY;
    private double lastOnGroundZ;

    ListSetting mode = new ListSetting("Mode", true, "Teleport", "Teleport", "Flag");

    public AntiVoidModule() {
        super("AntiVoid", "You shouldn't need this.", Category.PLAYER);
        addSettings(mode);
    }

    @EventLink
    Listener<EventUpdate> onUpdate = event -> {
        this.setDisplayName(mode.getValue());

        if (mc.thePlayer.onGround && PlayerUtils.isBlockUnder()) {
            this.lastOnGroundX = mc.thePlayer.posX;
            this.lastOnGroundY = mc.thePlayer.posY;
            this.lastOnGroundZ = mc.thePlayer.posZ;

        }

        if (mc.thePlayer.fallDistance > 3 && !PlayerUtils.isBlockUnder()) {
            switch (mode.getValue()) {
                case "Teleport":
                    if (mc.thePlayer.getDistance(lastOnGroundX, mc.thePlayer.posY, lastOnGroundZ) > 5) {
                        return;
                    }
                    mc.thePlayer.setPositionAndUpdate(lastOnGroundX, lastOnGroundY, lastOnGroundZ);
                    MovementUtils.stop();
                    break;
                case "Flag":
                    MovementUtils.stop();
                    PacketUtils.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - 100, mc.thePlayer.posZ, true));
                    break;
            }
        }
    };

}
