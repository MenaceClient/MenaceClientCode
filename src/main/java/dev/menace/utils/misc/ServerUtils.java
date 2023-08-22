package dev.menace.utils.misc;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.menace.Menace;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;

public class ServerUtils {

    private static final Minecraft mc = Menace.instance.mc;

    public static ServerData serverData;

    public static void connectToLastServer(GuiScreen parent) {
        if(serverData == null)
            return;

        mc.displayGuiScreen(new GuiConnecting(parent, mc, serverData));
    }

    public static String getLastServerIp() {
        if (serverData == null)
            return "SinglePlayer";
        return serverData.serverIP;
    }

    public static String getRemoteIp() {
        String serverIp = "SinglePlayer";

        if (mc.theWorld!=null && mc.theWorld.isRemote) {
            final ServerData serverData = mc.getCurrentServerData();
            if(serverData != null)
                serverIp = serverData.serverIP;
        }

        return serverIp;
    }
}