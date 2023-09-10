package dev.menace.module.modules.combat;

import dev.menace.event.Listener;
import dev.menace.event.annotations.EventLink;
import dev.menace.event.events.EventExplosion;
import dev.menace.event.events.EventReceivePacket;
import dev.menace.event.events.EventSendPacket;
import dev.menace.event.events.EventUpdate;
import dev.menace.module.Category;
import dev.menace.module.Module;
import dev.menace.module.settings.BooleanSetting;
import dev.menace.module.settings.ListSetting;
import dev.menace.module.settings.NumberSetting;
import dev.menace.utils.misc.ChatUtils;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

public class VelocityModule extends Module {

    ListSetting mode = new ListSetting("Mode", true, "Simple", "Simple", "Matrix", "Vulcan", "Clip", "Reverse");
    BooleanSetting explosions = new BooleanSetting("Explosions", true, true);
    NumberSetting horizontal = new NumberSetting("Horizontal", true, 0, 0, 100, true);
    NumberSetting vertical = new NumberSetting("Vertical", true, 0, 0, 100, true);
    BooleanSetting smooth = new BooleanSetting("Smooth", true, true);
    NumberSetting hurtTime = new NumberSetting("HurtTime", true, 0, 0, 10, true);

    //Reverse
    Queue<Double> motionX = new ConcurrentLinkedDeque<>();
    Queue<Double> motionY = new ConcurrentLinkedDeque<>();
    Queue<Double> motionZ = new ConcurrentLinkedDeque<>();

    public VelocityModule() {
        super("Velocity", "Edits your knockback", Category.COMBAT);
        addSettings(mode, explosions, horizontal, vertical, smooth, hurtTime);
    }

    @Override
    public void onGuiUpdate() {
        horizontal.setVisible(mode.getValue().equals("Simple") ||  mode.getValue().equals("Vulcan"));
        vertical.setVisible(mode.getValue().equals("Simple") ||  mode.getValue().equals("Vulcan"));
        smooth.setVisible(mode.getValue().equals("Reverse"));
        hurtTime.setVisible(mode.getValue().equals("Reverse"));
        super.onGuiUpdate();
    }

    @Override
    public void onEnable() {
        motionX.clear();
        motionY.clear();
        motionZ.clear();
        super.onEnable();
    }

    @EventLink
    public Listener<EventUpdate> onUpdate = event -> {
        this.setDisplayName(mode.getValue());

        if (mode.getValue().equalsIgnoreCase("Matrix")) {
            if (mc.thePlayer.hurtTime > 0) {
                mc.thePlayer.motionX *= 0.6;
                mc.thePlayer.motionZ *= 0.6;
            }
        } else if (mode.getValue().equalsIgnoreCase("Clip")) {
            if (mc.thePlayer.hurtTime == 10) {
                mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.3, mc.thePlayer.posZ);
                mc.thePlayer.motionX = 0;
                mc.thePlayer.motionY = 0;
                mc.thePlayer.motionZ = 0;
            }
        } else if (mode.getValue().equalsIgnoreCase("Reverse")) {
            if (mc.thePlayer.hurtTime > 0 && mc.thePlayer.hurtTime < hurtTime.getValue()) {
                motionX.add(mc.thePlayer.motionX);
                motionY.add(mc.thePlayer.motionY);
                motionZ.add(mc.thePlayer.motionZ);
            } else {
                if (!motionX.isEmpty()) {
                    mc.thePlayer.motionX = -motionX.poll();
                }
                if (!motionY.isEmpty()) {
                    if (smooth.getValue()) {
                        mc.thePlayer.motionY = (motionY.peek() > 0) ? -motionY.poll() : motionY.poll();
                    } else {
                        mc.thePlayer.motionY = -motionY.poll();
                    }
                }
                if (!motionZ.isEmpty()) {
                    mc.thePlayer.motionZ = -motionZ.poll();
                }
            }
        }
    };

    @EventLink
    Listener<EventSendPacket> onSendPacket = event -> {
        if (mode.getValue().equalsIgnoreCase("Vulcan")) {
            if (event.getPacket() instanceof C0FPacketConfirmTransaction && mc.thePlayer.hurtTime > 0) {
                event.cancel();
            }
        }
    };

    @EventLink
    Listener<EventReceivePacket> onReveivePacket = event -> {
        if (event.getPacket() instanceof S12PacketEntityVelocity) {

            S12PacketEntityVelocity packet = (S12PacketEntityVelocity) event.getPacket();
            if (mc.theWorld.getEntityByID(packet.getEntityID()) == null || !mc.theWorld.getEntityByID(packet.getEntityID()).equals(mc.thePlayer)) {return;}

            if (mode.getValue().equalsIgnoreCase("Simple") || mode.getValue().equalsIgnoreCase("Vulcan")) {

                if (horizontal.getValue() == 0 && vertical.getValue() == 0) {
                    event.cancel();
                } else {
                    packet.setMotionX(packet.getMotionX() * (horizontal.getValueI() / 100));
                    packet.setMotionY(packet.getMotionY() * (vertical.getValueI() / 100));
                    packet.setMotionZ(packet.getMotionZ() * (horizontal.getValueI() / 100));
                }

            }
        }
    };

    @EventLink
    Listener<EventExplosion> onExplosion = event -> {
        if (explosions.getValue()) {
            if (mode.getValue().equalsIgnoreCase("Simple") || mode.getValue().equalsIgnoreCase("Vulcan")) {
                if (horizontal.getValue() == 0 && vertical.getValue() == 0) {
                    event.cancel();
                } else {
                    event.setMotionX((event.getMotionX() * (horizontal.getValueF() / 100)));
                    event.setMotionY((event.getMotionY() * (vertical.getValueF() / 100)));
                    event.setMotionZ((event.getMotionZ() * (horizontal.getValueF() / 100)));
                }
            }
        }
    };

}
