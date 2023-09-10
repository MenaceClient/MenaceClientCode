package dev.menace.utils.player;

import dev.menace.utils.world.BlockUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

public class PlayerUtils {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static boolean isInLiquid() {
        return mc.thePlayer.isInWater() || mc.thePlayer.isInLava();
    }

    public static boolean isBlockUnder() {
        for (int offset = 0; offset < mc.thePlayer.posY + mc.thePlayer.getEyeHeight(); offset += 2) {
            final AxisAlignedBB boundingBox = mc.thePlayer.getEntityBoundingBox().offset(0, -offset, 0);

            if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, boundingBox).isEmpty())
                return true;
        }
        return false;
    }

    public static PlacementInfo getInfoPlacement(final BlockPos pos) {
        final Vec3 eyesPos = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
        EnumFacing[] values;
        for (int length = (values = EnumFacing.values()).length, i = 0; i < length; ++i) {
            final EnumFacing side = values[i];
            final BlockPos neighbor = pos.offset(side);
            final EnumFacing side2 = side.getOpposite();
            if (BlockUtils.getBlock(neighbor).canCollideCheck(mc.theWorld.getBlockState(neighbor), false)) {
                final Vec3 hitVec = new Vec3(neighbor).addVector(0.5, 0.5, 0.5).add(new Vec3(side2.getDirectionVec()).scale(0.5));
                if (eyesPos.squareDistanceTo(hitVec) <= 36.0) {
                    return new PlacementInfo(hitVec, side, neighbor);
                }
            }
        }
        return null;
    }

    public static boolean placeBlockSimple(final BlockPos pos) {
        return placeBlockSimple(pos, getInfoPlacement(pos));
    }

    public static boolean placeBlockSimple(final BlockPos pos, PlacementInfo info) {
        if (info == null) {
            return false;
        }

        EnumFacing side2 = info.getSide().getOpposite();

        mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getCurrentEquippedItem(), info.getNeighbor(), side2, info.getHitVec());

        mc.thePlayer.swingItem();

        mc.rightClickDelayTimer = 4;

        return true;
    }

    public static void placeBlockPacket(final BlockPos pos) {
        placeBlockPacket(pos, getInfoPlacement(pos));
    }

    public static void placeBlockPacket(final BlockPos pos, PlacementInfo info) {
        if (info == null) {
            return;
        }

        float f = (float)(info.getHitVec().xCoord - (double)info.getNeighbor().getX());
        float f1 = (float)(info.getHitVec().yCoord - (double)info.getNeighbor().getY());
        float f2 = (float)(info.getHitVec().zCoord - (double)info.getNeighbor().getZ());
        PacketUtils.sendPacket(new C08PacketPlayerBlockPlacement(info.getNeighbor(), info.getSide().getIndex(), mc.thePlayer.inventory.getCurrentItem(), f, f1, f2));
    }

}
