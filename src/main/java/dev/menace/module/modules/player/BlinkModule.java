package dev.menace.module.modules.player;

import dev.menace.event.Listener;
import dev.menace.event.annotations.EventLink;
import dev.menace.event.events.EventReceivePacket;
import dev.menace.event.events.EventSendPacket;
import dev.menace.event.events.EventUpdate;
import dev.menace.module.Category;
import dev.menace.module.Module;
import dev.menace.module.settings.BooleanSetting;
import dev.menace.module.settings.ListSetting;
import dev.menace.module.settings.NumberSetting;
import dev.menace.utils.player.EntityFakePlayer;
import dev.menace.utils.player.PacketUtils;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

public class BlinkModule extends Module {

    ListSetting direction = new ListSetting("Direction", true, "Outgoing", "Outgoing", "Incoming", "Both");
    ListSetting cacelType = new ListSetting("Cancel Type", true, "Recommended", "Recommended", "Movement", "All");
    BooleanSetting pulse = new BooleanSetting("Pulse", true, false);
    NumberSetting pulseSpeed = new NumberSetting("Pulse Speed", true, 10, 1, 100, true);
    BooleanSetting fp = new BooleanSetting("Fake Player", true, true);

    Queue<Packet<?>> outgoingQueue = new ConcurrentLinkedDeque<>();
    Queue<Packet<?>> incomingQueue = new ConcurrentLinkedDeque<>();
    EntityFakePlayer fakePlayer;

    public BlinkModule() {
        super("Blink", "Strategically cancel packets", Category.PLAYER);
        addSettings(direction, cacelType, pulse, pulseSpeed, fp);
    }

    @Override
    public void onGuiUpdate() {
        cacelType.setVisible(!direction.getValue().equalsIgnoreCase("Incoming"));
        pulseSpeed.setVisible(pulse.getValue());
        super.onGuiUpdate();
    }

    @Override
    public void onEnable() {
        if (fp.getValue()) {
            fakePlayer = EntityFakePlayer.createFakePlayer(mc.thePlayer);
        }
        super.onEnable();
    }

    @Override
    public void onDisable() {
        blink();
        if (fakePlayer != null) {
            mc.theWorld.removeEntityFromWorld(fakePlayer.getEntityId());
            fakePlayer = null;
        }
        super.onDisable();
    }

    @EventLink
    Listener<EventUpdate> onUpdate = event -> {
        switch (direction.getValue()) {
            case "Outgoing":
                this.setDisplayName("" + outgoingQueue.size());
                break;
            case "Incoming":
                this.setDisplayName("" + incomingQueue.size());
                break;
            case "Both":
                this.setDisplayName(outgoingQueue.size() + " | " + incomingQueue.size());
                break;
        }
    };

    @EventLink
    Listener<EventSendPacket> onSendPacket = event -> {
        if (direction.getValue().equals("Outgoing") || direction.getValue().equals("Both")) {
            if (cacelType.getValue().equalsIgnoreCase("Recommended") && !(event.getPacket() instanceof C03PacketPlayer
                || event.getPacket() instanceof C08PacketPlayerBlockPlacement
                || event.getPacket() instanceof C0APacketAnimation
                || event.getPacket() instanceof C0BPacketEntityAction
                || event.getPacket() instanceof C02PacketUseEntity)) {
                return;
            } else if (cacelType.getValue().equalsIgnoreCase("Movement") && !(event.getPacket() instanceof C03PacketPlayer)) {
                return;
            }

            if (pulse.getValue() && outgoingQueue.size() > pulseSpeed.getValue()) {
                blink();
            }

            outgoingQueue.add(event.getPacket());
            event.cancel();
        }
    };

    @EventLink
    Listener<EventReceivePacket> onRecievePacket = event -> {
        if (direction.getValue().equalsIgnoreCase("Incoming") || direction.getValue().equalsIgnoreCase("Both")) {
            incomingQueue.add(event.getPacket());
            event.cancel();
        }
    };

    private void blink() {
        if (fp.getValue()) {
            mc.theWorld.removeEntityFromWorld(fakePlayer.getEntityId());
            fakePlayer = EntityFakePlayer.createFakePlayer(mc.thePlayer);
        }

        while (!outgoingQueue.isEmpty()) {
            PacketUtils.sendPacketNoEvent(outgoingQueue.poll());
        }

        while (!incomingQueue.isEmpty()) {
            PacketUtils.sendPacketNoEvent(incomingQueue.poll());
        }
    }

}
