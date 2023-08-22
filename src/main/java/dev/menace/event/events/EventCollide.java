package dev.menace.event.events;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;

public class EventCollide extends Event {

    double x, y, z;
    AxisAlignedBB boundingBox;
    Block block;
    Entity collidingEntity;

    public EventCollide(double x, double y, double z, AxisAlignedBB boundingBox, Block block, Entity collidingEntity) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.boundingBox = boundingBox;
        this.block = block;
        this.collidingEntity = collidingEntity;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public AxisAlignedBB getBoundingBox() {
        return boundingBox;
    }

    public Block getBlock() {
        return block;
    }

    public Entity getCollidingEntity() {
        return collidingEntity;
    }

    public void setBoundingBox(AxisAlignedBB boundingBox) {
        this.boundingBox = boundingBox;
    }
}
