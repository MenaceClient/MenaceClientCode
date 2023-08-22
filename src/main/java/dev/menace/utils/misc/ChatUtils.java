package dev.menace.utils.misc;

import com.google.gson.JsonObject;
import dev.menace.Menace;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IChatComponent;

public class ChatUtils {

    static Minecraft mc = Menace.instance.mc;

    public static void message(String message) {

        if (mc.thePlayer == null) return;

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("text", "§0[§4Menace§0]§r " + message);

        mc.thePlayer.addChatMessage(IChatComponent.Serializer.jsonToComponent(jsonObject.toString()));
    }

    public static void noPrefix(String message) {

        if (mc.thePlayer == null) return;

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("text", message);

        mc.thePlayer.addChatMessage(IChatComponent.Serializer.jsonToComponent(jsonObject.toString()));
    }

    public static void anticheat(String message) {

        if (mc.thePlayer == null) return;

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("text", "§0[§4MenaceNCP§0]§r " + message);

        mc.thePlayer.addChatMessage(IChatComponent.Serializer.jsonToComponent(jsonObject.toString()));
    }

    public static void irc(String message) {

        if (mc.thePlayer == null) return;

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("text", "§0[§4MenaceIRC§0]§r " + message);

        mc.thePlayer.addChatMessage(IChatComponent.Serializer.jsonToComponent(jsonObject.toString()));
    }

}
