package dev.menace.utils.player;

import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;

public class PacketUtils {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static void sendPacket(Packet<?> packetIn) {
        if (mc.getNetHandler() == null || mc.getNetHandler().getNetworkManager() == null) {
            return;
        }
        mc.getNetHandler().getNetworkManager().sendPacket(packetIn);
    }

    public static void sendPacketNoEvent(Packet<?> packetIn) {
        if (mc.getNetHandler() == null || mc.getNetHandler().getNetworkManager() == null) {
            return;
        }

        //Update PacketBalance even if we dont send an event
        if (packetIn instanceof C03PacketPlayer) {
            //PacketBalanceUtils.instance.handleNoEvent();
        }

        mc.getNetHandler().getNetworkManager().sendPacketNoEvent(packetIn);
    }

    public static void sendPacketNoEventDelayed(Packet<?> packetIn, long delay) {
        if (mc.getNetHandler() == null || mc.getNetHandler().getNetworkManager() == null) {
            return;
        }

        try {
            new Thread(() -> {
                try {
                    Thread.sleep(delay);
                    sendPacketNoEvent(packetIn);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public static void addToSendQueue(Packet<?> packetIn) {
        mc.thePlayer.sendQueue.addToSendQueue(packetIn);
    }

}
