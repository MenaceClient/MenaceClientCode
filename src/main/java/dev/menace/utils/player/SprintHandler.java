package dev.menace.utils.player;

import net.minecraft.client.Minecraft;

public class SprintHandler {

    //Max 100, Min 1
    private static int currentPriority = 0;

    public static void setSprinting(boolean sprinting, int priority) {
        if (priority < currentPriority) {
            return;
        }

        Minecraft.getMinecraft().thePlayer.setSprinting(sprinting);
        currentPriority = priority;
    }

    public static void resetPriority(int priority) {
        if (priority == currentPriority) {
            currentPriority = 0;
        }
    }

}
