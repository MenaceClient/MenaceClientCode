package dev.menace.module.modules.misc;

import dev.menace.event.Listener;
import dev.menace.event.annotations.EventLink;
import dev.menace.event.events.EventSendPacket;
import dev.menace.module.Category;
import dev.menace.module.Module;
import dev.menace.module.settings.ListSetting;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;

public class SpooferModule extends Module {

    ListSetting mode = new ListSetting("Mode", true, "Vanilla", "Vanilla", "Forge", "Lunar", "LabyMod", "PVP Lounge", "CheatBreaker", "Geyser", "Null", "Cancel");

    public SpooferModule() {
        super("Spoofer", "Tells the server you're on a different client", Category.MISC);
        addSettings(mode);
    }

    @EventLink
    Listener<EventSendPacket> onSendPacket = event -> {
        this.setDisplayName(mode.getValue());
        if (event.getPacket() instanceof C17PacketCustomPayload) {
            final C17PacketCustomPayload packet = (C17PacketCustomPayload) event.getPacket();
            switch (mode.getValue()) {
                case "Vanilla": {
                    packet.setData(createPacketBuffer("vanilla", true));
                    break;
                }
                case "Forge": {
                    packet.setData(createPacketBuffer("FML", true));
                    break;
                }
                case "Lunar": {
                    packet.setChannel("REGISTER");
                    packet.setData(createPacketBuffer("Lunar-Client", false));
                    break;
                }
                case "LabyMod": {
                    packet.setData(createPacketBuffer("LMC", true));
                    break;
                }
                case "PvP Lounge": {
                    packet.setData(createPacketBuffer("PLC18", false));
                    break;
                }
                case "CheatBreaker": {
                    packet.setData(createPacketBuffer("CB", true));
                    break;
                }
                case "Geyser": {
                    packet.setData(createPacketBuffer("Geyser", false));
                    break;
                }
                case "Null": {
                    packet.setData(null);
                    break;
                }
                case "Cancel": {
                    event.setCancelled(true);
                    break;
                }
            }
        }
    };

    private PacketBuffer createPacketBuffer(final String data, final boolean string) {
        if (string)
            return new PacketBuffer(Unpooled.buffer()).writeString(data);
        else
            return new PacketBuffer(Unpooled.wrappedBuffer(data.getBytes()));
    }

}
