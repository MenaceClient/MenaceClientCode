package dev.menace.module.modules.combat;

import dev.menace.event.Listener;
import dev.menace.event.annotations.EventLink;
import dev.menace.event.events.EventAttack;
import dev.menace.event.events.EventPreMotion;
import dev.menace.event.events.EventSendPacket;
import dev.menace.module.Category;
import dev.menace.module.Module;
import dev.menace.module.settings.ListSetting;
import dev.menace.utils.player.PacketUtils;
import dev.menace.utils.player.PlayerUtils;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;

public class CriticalsModule extends Module {

    ListSetting mode = new ListSetting("Mode", true, "Packet", "Packet", "MiniJump", "OldNCP");

    private int editCrit;

    public CriticalsModule() {
        super("Criticals", "Forces your attacks to be criticals.", Category.COMBAT);
        addSettings(mode);
    }

    @Override
    public void onEnable() {
        editCrit = 0;
        super.onEnable();
    }

    @EventLink
    Listener<EventSendPacket> onSendPacket = event -> {
        if (event.getPacket() instanceof C02PacketUseEntity
                && ((C02PacketUseEntity)event.getPacket()).getAction() == C02PacketUseEntity.Action.ATTACK
                && canCrit()) {

            switch (mode.getValue()) {
                case "Packet":
                    PacketUtils.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0625, mc.thePlayer.posZ, true));
                    PacketUtils.sendPacket(new C03PacketPlayer(false));
                    PacketUtils.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1.1E-5, mc.thePlayer.posZ, false));
                    PacketUtils.sendPacket(new C03PacketPlayer(false));
                    break;
                case "MiniJump":
                    mc.thePlayer.jump();
                    mc.thePlayer.motionY -= .30000001192092879;
                    break;
                case "OldNCP":
                    editCrit = 1;
                    break;
            }

            mc.thePlayer.onCriticalHit(((C02PacketUseEntity) event.getPacket()).getEntityFromWorld(mc.theWorld));
        }
    };

    @EventLink
    Listener<EventPreMotion> onPreMotion = event -> {
        this.setDisplayName(mode.getValue());
        if (canCrit() && editCrit >= 1) {
            if (editCrit == 1) {
                editCrit = 2;
                event.setY(event.getY() + 0.11);
            } else if (editCrit == 2) {
                editCrit = 3;
                event.setY(event.getY() + 0.1100013579);
            } else if (editCrit == 3) {
                editCrit = 0;
                event.setY(event.getY() + 0.0000013579);
            }
            event.setOnGround(false);
        }
    };

    private boolean canCrit() {
        return mc.thePlayer != null && !PlayerUtils.isInLiquid() && mc.thePlayer.onGround;
    }


}
