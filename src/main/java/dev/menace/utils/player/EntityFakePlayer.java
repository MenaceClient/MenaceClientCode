package dev.menace.utils.player;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class EntityFakePlayer extends EntityOtherPlayerMP {

    private EntityFakePlayer(World worldIn, GameProfile gameProfileIn) {
        super(worldIn, gameProfileIn);
    }

    @Override
    public boolean canAttackPlayer(EntityPlayer other) {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    public static EntityFakePlayer createFakePlayer(EntityPlayer player) {
        EntityFakePlayer fakePlayer = new EntityFakePlayer(player.worldObj, player.getGameProfile());
        fakePlayer.clonePlayer(player, true);
        fakePlayer.copyLocationAndAnglesFrom(player);
        fakePlayer.serverPosX = player.serverPosX;
        fakePlayer.serverPosY = player.serverPosY;
        fakePlayer.serverPosZ = player.serverPosZ;
        Minecraft.getMinecraft().theWorld.addEntityToWorld(fakePlayer.getEntityId(), fakePlayer);
        return fakePlayer;
    }

}
