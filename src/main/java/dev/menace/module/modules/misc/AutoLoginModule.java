package dev.menace.module.modules.misc;

import dev.menace.event.Listener;
import dev.menace.event.annotations.EventLink;
import dev.menace.event.events.EventReceivePacket;
import dev.menace.module.Category;
import dev.menace.module.Module;
import net.minecraft.network.play.server.S02PacketChat;

public class AutoLoginModule extends Module {
    public AutoLoginModule() {
        super("AutoLogin", "Automatically types /register and /login on servers", Category.MISC);
        addSettings();
    }

    @EventLink
    Listener<EventReceivePacket> onRecievePacket = event -> {
        if (event.getPacket() instanceof S02PacketChat) {
            String message = ((S02PacketChat)event.getPacket()).getChatComponent().getUnformattedText();
            if (message.contains("/register") || message.contains("/reg")) {
                new Thread(() -> {
                    try {
                        Thread.sleep(2000);
                        mc.thePlayer.sendChatMessage("/register password password");
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            } else if (message.contains("/login")) {
                new Thread(() -> {
                    try {
                        Thread.sleep(2000);
                        mc.thePlayer.sendChatMessage("/login password");
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        }
    };

}
