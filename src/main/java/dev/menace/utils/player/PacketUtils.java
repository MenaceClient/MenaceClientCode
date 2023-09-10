package dev.menace.utils.player;

import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

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
            PacketBalanceUtils.instance.handlePacket();
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

    public static void sendBlocking(boolean place) {
        C08PacketPlayerBlockPlacement packet = place ?
                new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.getHeldItem(), 0, 0, 0) :
                new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem());

        sendPacket(packet);
    }

    public static void releaseUseItem() {
        C07PacketPlayerDigging packet = new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN);

        sendPacket(packet);
    }

}
