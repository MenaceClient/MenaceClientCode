package dev.menace.utils.raycast;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

import java.util.List;

public class RaycastUtils {

    private static Minecraft mc = Minecraft.getMinecraft();

    public static MovingObjectPosition getMouseOver(float maxRange) {
        Entity viewer = RaycastUtils.mc.getRenderViewEntity();

        if (viewer == null || RaycastUtils.mc.theWorld == null) {
            return null;
        }

        double reachDistance = RaycastUtils.mc.playerController.getBlockReachDistance();
        MovingObjectPosition targetHit = viewer.rayTrace(reachDistance, 1.0f);
        double closestDistance = reachDistance;

        Vec3 eyePosition = viewer.getPositionEyes(1.0f);
        Vec3 lookDirection = viewer.getLook(1.0f);
        Vec3 targetPoint = eyePosition.addVector(
                lookDirection.xCoord * maxRange,
                lookDirection.yCoord * maxRange,
                lookDirection.zCoord * maxRange
        );

        List<Entity> entitiesWithinSight = RaycastUtils.mc.theWorld.getEntitiesWithinAABBExcludingEntity(
                viewer,
                viewer.getEntityBoundingBox()
                        .addCoord(lookDirection.xCoord * maxRange, lookDirection.yCoord * maxRange, lookDirection.zCoord * maxRange)
                        .expand(1.0, 1.0, 1.0)
        );

        for (Entity entity : entitiesWithinSight) {
            if (!entity.canBeCollidedWith()) {
                continue;
            }

            float collisionBorderSize = entity.getCollisionBorderSize();
            AxisAlignedBB entityBoundingBox = entity.getEntityBoundingBox().expand(collisionBorderSize, collisionBorderSize, collisionBorderSize);
            MovingObjectPosition interceptPosition = entityBoundingBox.calculateIntercept(eyePosition, targetPoint);

            if (entityBoundingBox.isVecInside(eyePosition)) {
                if (closestDistance >= 0) {
                    closestDistance = 0;
                    targetHit = new MovingObjectPosition(entity, interceptPosition != null ? interceptPosition.hitVec : eyePosition);
                }
            } else if (interceptPosition != null) {
                double distanceToIntercept = eyePosition.distanceTo(interceptPosition.hitVec);

                if (distanceToIntercept < closestDistance || closestDistance == 0) {
                    if (entity != viewer.ridingEntity) {
                        closestDistance = distanceToIntercept;
                        targetHit = new MovingObjectPosition(entity, interceptPosition.hitVec);
                    }
                }
            }
        }

        if (targetHit != null && (closestDistance < reachDistance || RaycastUtils.mc.objectMouseOver == null)) {
            Entity pointedEntity = targetHit.entityHit;
            if (pointedEntity instanceof EntityLivingBase || pointedEntity instanceof EntityItemFrame) {
                return targetHit;
            }
        }

        return targetHit;
    }

    public static boolean isCrosshairOnEntity(float[] rotations, Entity targetEntity, double distance) {

        float yaw = rotations[0];
        float pitch = rotations[1];

        double playerX = mc.thePlayer.posX;
        double playerY = mc.thePlayer.posY + mc.thePlayer.getEyeHeight();
        double playerZ = mc.thePlayer.posZ;

        Vec3 start = new Vec3(playerX, playerY, playerZ);
        Vec3 end = start.add(new Vec3(
                -Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * distance,
                -Math.sin(Math.toRadians(pitch)) * distance,
                Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * distance
        ));

        AxisAlignedBB playerAABB = mc.thePlayer.getEntityBoundingBox();
        AxisAlignedBB targetAABB = targetEntity.getEntityBoundingBox();

        MovingObjectPosition playerResult = playerAABB.calculateIntercept(start, end);
        MovingObjectPosition targetResult = targetAABB.calculateIntercept(start, end);

        if (playerResult != null && targetResult != null) {
            return targetResult.hitVec != null;
        }

        return false;
    }

}
