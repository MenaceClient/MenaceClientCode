package dev.menace.utils.player;

import net.minecraft.client.Minecraft;

public class MovementUtils {

    private static Minecraft mc = Minecraft.getMinecraft();

    public static boolean isMoving() {
        return mc.thePlayer.motionX != 0 || mc.thePlayer.motionZ != 0 || mc.thePlayer.motionY != 0;
    }

}
