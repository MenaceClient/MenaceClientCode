package dev.menace.utils.player;

import dev.menace.Menace;
import dev.menace.event.Listener;
import dev.menace.event.Priorities;
import dev.menace.event.annotations.EventLink;
import dev.menace.event.events.EventReceivePacket;
import dev.menace.event.events.EventSendPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class PacketBalanceUtils {

    public static PacketBalanceUtils instance = new PacketBalanceUtils();
    Minecraft mc = Minecraft.getMinecraft();

    private long balance;

    private long lastNanoTime;


    public void start() {
        Menace.instance.eventManager.subscribe(this);
    }

    @EventLink
    Listener<EventReceivePacket> onRecievePacket = event -> {
        if(event.getPacket() instanceof S08PacketPlayerPosLook) {
            balance -= 50000000;
        }
    };

    @EventLink(Priorities.VERY_LOW)
    Listener<EventSendPacket> onSendPacket = event -> {
        if(event.getPacket() instanceof C03PacketPlayer) {
            if(!event.isCancelled()) {
                handlePacket();
            }
        }
    };

    protected void handlePacket() {
        long nanoTime = System.nanoTime();

        if(!mc.getNetHandler().doneLoadingTerrain || mc.thePlayer.ticksExisted < 30) {
            balance = 0;
            lastNanoTime = nanoTime - 50000000;
        }

        balance += 50000000;

        balance -= nanoTime - lastNanoTime;

        lastNanoTime = nanoTime;
    }

    public long getBalanceInMS() {
        return (long) (balance / 1E6);
    }

}
