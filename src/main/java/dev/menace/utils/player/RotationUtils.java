package dev.menace.utils.player;

import dev.menace.utils.math.DoubleExponentialSmoothingForLinearSeries;
import dev.menace.utils.math.Model;
import dev.menace.utils.misc.ChatUtils;
import dev.menace.utils.raycast.RaycastUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RotationUtils {

    private static Minecraft mc = Minecraft.getMinecraft();

    public static Vec3 getHead(final AxisAlignedBB bb) {
        return new Vec3(bb.minX + (bb.maxX - bb.minX) * 0.5, bb.maxY, bb.minZ + (bb.maxZ - bb.minZ) * 0.5);
    }

    public static Vec3 getEyes(Entity entity) {
        return new Vec3(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);
    }

    public static Vec3 getCenter(final AxisAlignedBB bb) {
        return new Vec3(bb.minX + (bb.maxX - bb.minX) * 0.5, bb.minY + (bb.maxY - bb.minY) * 0.5, bb.minZ + (bb.maxZ - bb.minZ) * 0.5);
    }

    public static Vec3 getCock(final AxisAlignedBB bb) {
        return new Vec3(bb.minX + (bb.maxX - bb.minX) * 0.5, bb.minY + (bb.maxY - bb.minY) * 0.3, bb.minZ + (bb.maxZ - bb.minZ) * 0.5);
    }

    public static Vec3 getFeet(final AxisAlignedBB bb) {
        return new Vec3(bb.minX + (bb.maxX - bb.minX) * 0.5, bb.minY, bb.minZ + (bb.maxZ - bb.minZ) * 0.5);
    }

    public static Vec3 calculateClosestPoint(AxisAlignedBB aabb, float[] rotations) {
        // Calculate the center of the AABB
        double centerX = (aabb.minX + aabb.maxX) * 0.5;
        double centerY = (aabb.minY + aabb.maxY) * 0.5;
        double centerZ = (aabb.minZ + aabb.maxZ) * 0.5;

        // Convert yaw and pitch to radians
        double yawRad = Math.toRadians(rotations[0]);
        double pitchRad = Math.toRadians(rotations[1]);

        // Calculate rotation matrix components
        double cosYaw = Math.cos(yawRad);
        double sinYaw = Math.sin(yawRad);
        double cosPitch = Math.cos(pitchRad);
        double sinPitch = Math.sin(pitchRad);

        // Calculate direction vector based on rotations
        double directionX = cosPitch * cosYaw;
        double directionY = sinPitch;
        double directionZ = cosPitch * sinYaw;

        // Calculate the dimensions of the AABB
        double sizeX = aabb.maxX - aabb.minX;
        double sizeY = aabb.maxY - aabb.minY;
        double sizeZ = aabb.maxZ - aabb.minZ;

        // Calculate closest point on the AABB using the direction vector
        double closestX = centerX + sizeX * 0.5 * MathHelper.clamp_double(directionX, -1.0, 1.0);
        double closestY = centerY + sizeY * 0.5 * MathHelper.clamp_double(directionY, -1.0, 1.0);
        double closestZ = centerZ + sizeZ * 0.5 * MathHelper.clamp_double(directionZ, -1.0, 1.0);

        // Ensure the closest point is within the AABB boundaries
        closestX = MathHelper.clamp_double(closestX, aabb.minX, aabb.maxX);
        closestY = MathHelper.clamp_double(closestY, aabb.minY, aabb.maxY);
        closestZ = MathHelper.clamp_double(closestZ, aabb.minZ, aabb.maxZ);

        return new Vec3(closestX, closestY, closestZ);
    }

    /**
     * Smooths the current rotation using the last for it to make aura harder to flag.
     *
     * @param rotations     Current rotations.
     * @param lastRotations Last rotations.
     * @return Current rotation smoothed according to last.
     */
    public static float[] getFixedRotation(final float[] rotations, final float[] lastRotations) {
        final Minecraft mc = Minecraft.getMinecraft();

        final float yaw = rotations[0];
        final float pitch = rotations[1];

        final float lastYaw = lastRotations[0];
        final float lastPitch = lastRotations[1];

        final float f = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
        final float gcd = f * f * f * 1.2F;

        final float deltaYaw = yaw - lastYaw;
        final float deltaPitch = pitch - lastPitch;

        final float fixedDeltaYaw = deltaYaw - (deltaYaw % gcd);
        final float fixedDeltaPitch = deltaPitch - (deltaPitch % gcd);

        final float fixedYaw = lastYaw + fixedDeltaYaw;
        final float fixedPitch = lastPitch + fixedDeltaPitch;

        return new float[]{fixedYaw, fixedPitch};
    }

    public static float[] getRotationsToVector(Vec3 point, float[] lastRotations, double speedFactor) {
        double x = point.xCoord - Minecraft.getMinecraft().thePlayer.posX;
        double z = point.zCoord - Minecraft.getMinecraft().thePlayer.posZ;
        double y = point.yCoord - Minecraft.getMinecraft().thePlayer.posY - 0.2;
        double dist = Math.sqrt(x * x + z * z);
        float requiredYaw = (float) Math.toDegrees(Math.atan2(z, x)) - 90F;
        float requiredPitch = (float) -Math.toDegrees(Math.atan2(y, dist));

        //Smooth the rotations
        double[] interpolatedYaw = interpolateRotation(lastRotations[0], requiredYaw, 30, speedFactor);
        double[] interpolatedPitch = interpolateRotation(lastRotations[1], requiredPitch, 30, speedFactor);

        Model yawModel = DoubleExponentialSmoothingForLinearSeries.fit(interpolatedYaw, 0.8, 0.2);
        Model pitchModel = DoubleExponentialSmoothingForLinearSeries.fit(interpolatedPitch, 0.8, 0.2);

        double[] yawData = yawModel.getSmoothedData();
        double[] pitchData = pitchModel.getSmoothedData();

        //Decide which yaw and pitch are the best
        float bestYaw = (float) findBestRotation(yawData, speedFactor);
        float bestPitch = (float) findBestRotation(pitchData, speedFactor);

        return new float[]{bestYaw, bestPitch};
    }

    public static float[] doJitter(float[] rotations, Entity entity, float jitterStrength, double range) {
        //Jitter the rotations but make sure they are still within the entity's hitbox
        float jitteredYaw = rotations[0] + (float) (Math.random() * jitterStrength * 2 - jitterStrength);
        float jitteredPitch = rotations[1] + (float) (Math.random() * jitterStrength * 2 - jitterStrength);

        //Check if the jittered rotations are within the entity's hitbox
        if (RaycastUtils.isCrosshairOnEntity(new float[] {jitteredYaw, jitteredPitch}, entity, range)) {
            return new float[]{jitteredYaw, jitteredPitch};
        } else {
            return rotations;
        }
    }

    public static float[] getAuraRotations(Vec3 point, float[] lastRotations, double speedFactor) {
        final Vec3 eyesPos = getEyesPos();
        final double diffX = point.xCoord - eyesPos.xCoord;
        final double diffY = point.yCoord - eyesPos.yCoord;
        final double diffZ = point.zCoord - eyesPos.zCoord;
        final double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        final float requiredYaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f;
        final float requiredPitch = (float)(-Math.toDegrees(Math.atan2(diffY, diffXZ)));

        //Smooth the rotations
        return smoothRotations(new float[]{requiredYaw, requiredPitch}, lastRotations, speedFactor);

    }

    public static float[] smoothRotations(float[] rotations, float[] lastRotations, double speedFactor) {
        //Smooth the rotations
        double[] interpolatedYaw = interpolateRotation(lastRotations[0], rotations[0], 20, speedFactor);
        double[] interpolatedPitch = interpolateRotation(lastRotations[1], rotations[1], 20, speedFactor);

        Model yawModel = DoubleExponentialSmoothingForLinearSeries.fit(interpolatedYaw, 0.8, 0.2);
        Model pitchModel = DoubleExponentialSmoothingForLinearSeries.fit(interpolatedPitch, 0.8, 0.2);

        //ChatUtils.message("SSE: " + yawModel.getSSE() + " | " + pitchModel.getSSE());

        double[] yawData = yawModel.getSmoothedData();
        double[] pitchData = pitchModel.getSmoothedData();

        //Decide which  yaw and pitch are the best
        float bestYaw = (float) findBestRotation(yawData, speedFactor);
        float bestPitch = (float) findBestRotation(pitchData, speedFactor);

        return new float[]{bestYaw, bestPitch};
    }

    public static Vec3 getEyesPos() {
        return new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
    }

    public static double[] interpolateRotation(double oldRotation, double newRotation, int steps, double speedFactor) {
        //Spherical Linear interpolation
        List<Double> rotations = new ArrayList<>();

        // Calculate a factor to adjust the number of steps
        double angleDifference = Math.abs(newRotation - oldRotation);
        int adjustedSteps = angleDifference < 30 ? 30 : (int) (steps * speedFactor);

        // Convert degrees to radians
        double previousRotationRad = Math.toRadians(oldRotation);
        double targetRotationRad = Math.toRadians(newRotation);

        // Calculate the angle between the two rotations
        double angle = targetRotationRad - previousRotationRad;

        // Ensure the angle is within the range [-pi, pi]
        if (angle > Math.PI) {
            angle -= 2 * Math.PI;
        } else if (angle < -Math.PI) {
            angle += 2 * Math.PI;
        }

        for (int step = 0; step <= adjustedSteps; step++) {
            // Calculate the interpolation parameter t (0 to 1)
            double t = (double) step / adjustedSteps;

            // Perform spherical linear interpolation
            double interpolatedRotationRad = previousRotationRad + angle * t;

            // Convert the interpolated rotation back to degrees
            double interpolatedRotationDeg = Math.toDegrees(interpolatedRotationRad);

            rotations.add(interpolatedRotationDeg);
        }

        return rotations.stream().mapToDouble(Double::doubleValue).toArray();
    }

    public static double calculateAngularVelocity(double[] rotations) {
        if (rotations.length < 2) {
            return 0.0; // Not enough data points to calculate angular velocity
        }

        double totalAngleChange = rotations[rotations.length - 1] - rotations[0];
        double totalTime = rotations.length - 1; // Time steps between rotations

        // Calculate angular velocity (degrees per time step)
        double angularVelocity = totalAngleChange / totalTime;

        return angularVelocity;
    }

    public static double calculateSmoothness(double[] rotations, double[] smoothedRotations) {
        // Calculate the angular velocities for the original and smoothed rotations
        double originalAngularVelocity = calculateAngularVelocity(rotations);
        double smoothedAngularVelocity = calculateAngularVelocity(smoothedRotations);

        // Calculate the difference in angular velocities (smoothness)
        return Math.abs(smoothedAngularVelocity - originalAngularVelocity);
    }

    public static double findBestRotation(double[] interpolatedRotations, double speedFactor) {
        double bestRotation = 0.0;
        double smoothestScore = Double.MAX_VALUE;

        for (double rotation : interpolatedRotations) {
            double[] smoothedRotations = interpolateRotation(interpolatedRotations[0], rotation, 100, speedFactor);

            double smoothness = calculateSmoothness(interpolatedRotations, smoothedRotations);
            if (smoothness < smoothestScore) {
                smoothestScore = smoothness;
                bestRotation = rotation;
            }
        }

        return bestRotation;
    }


    public static float[] getBasicRotations(Entity e) {
        double x = e.posX - e.lastTickPosX;
        double z = e.posZ - e.lastTickPosZ;
        double y = e.posY - e.lastTickPosY - 0.2;
        double dist = Math.sqrt(x * x + z * z);
        float yaw = (float) Math.toDegrees(Math.atan2(z, x)) - 90F;
        float pitch = (float) -Math.toDegrees(Math.atan2(y, dist));
        return new float[]{yaw, pitch};
    }

    public static Vec3 getVectorForRotation(float yaw, float pitch) {
        float f = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
        float f1 = MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
        float f2 = -MathHelper.cos(-pitch * 0.017453292F);
        float f3 = MathHelper.sin(-pitch * 0.017453292F);
        return new Vec3(f1 * f2, f3, f * f2);
    }
}
