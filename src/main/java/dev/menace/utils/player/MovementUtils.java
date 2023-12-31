package dev.menace.utils.player;

import net.minecraft.client.Minecraft;
import net.minecraft.potion.Potion;

public class MovementUtils {

    private static Minecraft mc = Minecraft.getMinecraft();

    public static boolean isMoving() {
        return mc.thePlayer.motionX != 0 || mc.thePlayer.motionZ != 0;
    }

    public static double getSpeed() {
        return Math.sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ);
    }

    public static void strafe() {
        strafe((float) getSpeed());
    }

    public static void strafe(final float speed) {
        if (!isMoving()) return;

        final double yaw = getDirection() / 180 * Math.PI;
        mc.thePlayer.motionX = -Math.sin(yaw) * speed;
        mc.thePlayer.motionZ = Math.cos(yaw) * speed;
    }

    public static double getBaseMoveSpeed() {
        double baseSpeed = 0.2873;
        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            final int amplifier = mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
            baseSpeed *= 1.0 + 0.2 * (amplifier + 1);
        }
        return baseSpeed;
    }

    public static double getDirection() {
        float yaw = mc.thePlayer.rotationYaw;
        float moveForward = mc.thePlayer.moveForward;
        float moveStrafe = mc.thePlayer.moveStrafing;

        if (moveForward < 0) {
            yaw += 180;
        }

        float forward = 1;
        if (moveForward < 0) {
            forward = -0.5f;
        } else if (moveForward > 0) {
            forward = 0.5f;
        }

        if (moveStrafe > 0) {
            yaw -= 70 * forward;
        }

        if (moveStrafe < 0) {
            yaw += 70 * forward;
        }

        return Math.toRadians(yaw);
    }

    public static double getDirection(float yaw, float moveForward, float moveStrafe) {
        if (moveForward < 0) {
            yaw += 180;
        }

        float forward = 1;
        if (moveForward < 0) {
            forward = -0.5f;
        } else if (moveForward > 0) {
            forward = 0.5f;
        }

        if (moveStrafe > 0) {
            yaw -= 90 * forward;
        }

        if (moveStrafe < 0) {
            yaw += 90 * forward;
        }

        return Math.toRadians(yaw);
    }

    public static float getPlayerDirection() {
        float direction = mc.thePlayer.rotationYaw;

        if (mc.thePlayer.moveForward > 0) {
            if (mc.thePlayer.moveStrafing > 0) {
                direction -= 45;
            } else if (mc.thePlayer.moveStrafing < 0) {
                direction += 45;
            }
        } else if (mc.thePlayer.moveForward < 0) {
            if (mc.thePlayer.moveStrafing > 0) {
                direction -= 135;
            } else if (mc.thePlayer.moveStrafing < 0) {
                direction += 135;
            } else {
                direction -= 180;
            }
        } else {
            if (mc.thePlayer.moveStrafing > 0) {
                direction -= 90;
            } else if (mc.thePlayer.moveStrafing < 0) {
                direction += 90;
            }
        }

        return direction;
    }

    public static float[] incrementMoveDirection(float forward, float strafe) {
        if(forward != 0 || strafe != 0) {
            float value = forward != 0 ? Math.abs(forward) : Math.abs(strafe);

            if(forward > 0) {
                if(strafe > 0) {
                    strafe = 0;
                } else if(strafe == 0) {
                    strafe = -value;
                } else if(strafe < 0) {
                    forward = 0;
                }
            } else if(forward == 0) {
                if(strafe > 0) {
                    forward = value;
                } else {
                    forward = -value;
                }
            } else {
                if(strafe < 0) {
                    strafe = 0;
                } else if(strafe == 0) {
                    strafe = value;
                } else if(strafe > 0) {
                    forward = 0;
                }
            }
        }

        return new float[] {forward, strafe};
    }

    public static void stop() {
        mc.thePlayer.motionX = 0;
        mc.thePlayer.motionZ = 0;
    }
}
