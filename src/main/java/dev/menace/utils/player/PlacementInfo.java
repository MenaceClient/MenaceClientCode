package dev.menace.utils.player;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

public class PlacementInfo {

    private Vec3 hitVec;
    private EnumFacing side;
    private BlockPos neighbor;

    public PlacementInfo(Vec3 hitVec, EnumFacing side, BlockPos neighbor) {
        this.hitVec = hitVec;
        this.side = side;
        this.neighbor = neighbor;
    }

    public Vec3 getHitVec() {
        return hitVec;
    }

    public EnumFacing getSide() {
        return side;
    }

    public BlockPos getNeighbor() {
        return neighbor;
    }

}
