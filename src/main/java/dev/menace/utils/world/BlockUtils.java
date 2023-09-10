package dev.menace.utils.world;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class BlockUtils {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static BlockPos findNearbyBlocks(BlockPos pos) {

        EnumFacing[] values;
        for (int length = (values = EnumFacing.values()).length, i = 0; i < length; ++i) {
            final EnumFacing side = values[i];
            final BlockPos neighbor = pos.offset(side);
            if (BlockUtils.getMaterial(pos).isReplaceable() && BlockUtils.getBlock(neighbor).canCollideCheck(mc.theWorld.getBlockState(neighbor), false)) {
                return pos;
            }
        }

        for (int amt = 1; amt < 4; amt++) {
            for (int sides = EnumFacing.values().length, side_ = 0; side_ < sides; side_++) {
                BlockPos newPos = pos.offset(EnumFacing.values()[side_], amt);
                EnumFacing[] values2;
                for (int length = (values2 = EnumFacing.values()).length, i = 0; i < length; ++i) {
                    final EnumFacing side = values2[i];
                    final BlockPos neighbor = newPos.offset(side);
                    if (BlockUtils.getMaterial(newPos).isReplaceable() && BlockUtils.getBlock(neighbor).canCollideCheck(mc.theWorld.getBlockState(neighbor), false)) {
                        return newPos;
                    }
                }
            }
        }

        return null;
    }


    public static Block getBlock(BlockPos pos) {
        return mc.theWorld.getBlockState(pos).getBlock();
    }

    public static Material getMaterial(BlockPos pos) {
        return mc.theWorld.getBlockState(pos).getBlock().getMaterial();
    }

}
