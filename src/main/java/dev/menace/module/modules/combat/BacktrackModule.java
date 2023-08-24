package dev.menace.module.modules.combat;

import dev.menace.event.Listener;
import dev.menace.event.annotations.EventLink;
import dev.menace.event.events.EventAttack;
import dev.menace.event.events.EventReceivePacket;
import dev.menace.event.events.EventUpdate;
import dev.menace.module.Category;
import dev.menace.module.Module;
import dev.menace.module.settings.ListSetting;
import dev.menace.module.settings.NumberSetting;
import dev.menace.utils.misc.ChatUtils;
import dev.menace.utils.player.EntityFakePlayer;
import dev.menace.utils.timer.MSTimer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.network.play.server.S19PacketEntityHeadLook;

import java.util.ArrayList;
import java.util.List;

public class BacktrackModule extends Module {

    ListSetting mode = new ListSetting("Mode", true, "Normal", "Normal", "Desync", "SmartDesync", "PlayerSelect");
    NumberSetting delay = new NumberSetting("Delay", true, 100, 0, 10000, 50, true);
    List<PacketDelayTimer> packetQueue = new ArrayList<>();
    List<EntityFakePlayer> fakePlayers = new ArrayList<>();
    MSTimer desyncTimer = new MSTimer();
    boolean desyncMSG = false;

    public BacktrackModule() {
        super("Backtrack", "Attack targets in previous positions", Category.COMBAT);
        addSettings(mode, delay);
    }

    @Override
    public void onEnable() {
        desyncTimer.reset();
        if (mode.getValue().equalsIgnoreCase("Desync")) {
            desyncMSG = false;
            ChatUtils.message("Starting desync, players will be frozen for " + delay.getValue() + "ms");
        }
        super.onEnable();
    }

    @Override
    public void onDisable() {
        for (PacketDelayTimer packetDelayTimer : packetQueue) {
            packetDelayTimer.getPacket().processPacket(mc.getNetHandler());
        }
        packetQueue.clear();

        for (EntityPlayer fakePlayer : fakePlayers) {
            mc.theWorld.removeEntityFromWorld(fakePlayer.getEntityId());
        }
        fakePlayers.clear();

        super.onDisable();
    }

    @EventLink
    public Listener<EventReceivePacket> onRecievePacket = event -> {
        if (event.getPacket() instanceof S14PacketEntity || event.getPacket() instanceof S19PacketEntityHeadLook || event.getPacket() instanceof S18PacketEntityTeleport) {
            if (mode.getValue().equalsIgnoreCase("Desync")) {

                if (event.getPacket() instanceof S14PacketEntity) {
                    S14PacketEntity packet = (S14PacketEntity) event.getPacket();
                    if (!(packet.getEntity(mc.theWorld) instanceof EntityPlayer)) {
                        return;
                    }

                    EntityPlayer player = (EntityPlayer) packet.getEntity(mc.theWorld);
                    EntityPlayer fakePlayer = fakePlayers.stream().filter(entityPlayer -> entityPlayer.getUniqueID().equals(player.getUniqueID())).findFirst().orElse(null);

                    if (fakePlayer == null) {
                        //Create fake player
                        EntityFakePlayer fp = EntityFakePlayer.createFakePlayer(player);
                        handleEntityMovement(packet, fp);
                        fakePlayers.add(fp);
                    } else {
                        //Update fake player
                        handleEntityMovement(packet, fakePlayer);
                    }

                } else if (event.getPacket() instanceof S19PacketEntityHeadLook) {
                    S19PacketEntityHeadLook packet = (S19PacketEntityHeadLook) event.getPacket();
                    if (!(packet.getEntity(mc.theWorld) instanceof EntityPlayer)) {
                        return;
                    }

                    EntityPlayer player = (EntityPlayer) packet.getEntity(mc.theWorld);

                    EntityPlayer fakePlayer = fakePlayers.stream().filter(entityPlayer -> entityPlayer.getUniqueID().equals(player.getUniqueID())).findFirst().orElse(null);

                    if (fakePlayer == null) {
                        //Create fake player
                        EntityFakePlayer fp = EntityFakePlayer.createFakePlayer(player);
                        handleEntityHeadLook(packet, fp);
                        fakePlayers.add(fp);
                    } else {
                        //Update fake player
                        handleEntityHeadLook(packet, fakePlayer);
                    }
                } else if (event.getPacket() instanceof S18PacketEntityTeleport) {
                    S18PacketEntityTeleport packet = (S18PacketEntityTeleport) event.getPacket();
                    if (!(mc.theWorld.getEntityByID(packet.getEntityId()) instanceof EntityPlayer)) {
                        return;
                    }

                    EntityPlayer player = (EntityPlayer) mc.theWorld.getEntityByID(packet.getEntityId());

                    EntityPlayer fakePlayer = fakePlayers.stream().filter(entityPlayer -> entityPlayer.getUniqueID().equals(player.getUniqueID())).findFirst().orElse(null);

                    if (fakePlayer == null) {
                        //Create fake player
                        EntityFakePlayer fp = EntityFakePlayer.createFakePlayer(player);
                        handleEntityTeleport(packet, fp);
                        fakePlayers.add(fp);
                    } else {
                        //Update fake player
                        handleEntityTeleport(packet, fakePlayer);
                    }
                }

                packetQueue.add(new PacketDelayTimer(event.getPacket()));
                event.cancel();
            } else {
                //TODO: Normal mode
            }
        }
    };

    @EventLink
    public Listener<EventUpdate> eventUpdate = event -> {

        if (desyncTimer.hasTimePassed(delay.getValueL()) && !desyncMSG) {
            ChatUtils.message("Desync finished, you are now " + delay.getValue() + "ms behind");
            desyncMSG = true;
        }

        if (mode.getValue().equalsIgnoreCase("Desync") && !packetQueue.isEmpty()) {
            packetQueue.removeIf(packetDelayTimer -> {
                if (packetDelayTimer.hasTimePassed(delay.getValueL())) {
                    packetDelayTimer.getPacket().processPacket(mc.getNetHandler());
                    return true;
                }
                return false;
            });
        }
    };

    @EventLink
    public Listener<EventAttack> onAttack = event -> {
          if (mode.getValue().equalsIgnoreCase("Desync")) {
              EntityFakePlayer realPlayer = fakePlayers.stream().filter(entityPlayer -> entityPlayer.getUniqueID().equals(event.getTarget().getUniqueID())).findFirst().orElse(null);
              if (realPlayer != null && mc.thePlayer.getDistanceToEntity(realPlayer) > 6) {
                  ChatUtils.message("Cancelled attack, target is too far away");
                  event.cancel();
              }
          }
    };

    public void handleEntityMovement(S14PacketEntity packetIn, EntityPlayer player) {
        if (player != null) {
            player.serverPosX += packetIn.getPosX();
            player.serverPosY += packetIn.getPosY();
            player.serverPosZ += packetIn.getPosZ();
            double d0 = (double) player.serverPosX / 32.0D;
            double d1 = (double) player.serverPosY / 32.0D;
            double d2 = (double) player.serverPosZ / 32.0D;
            float f = packetIn.func_149060_h() ? (float)(packetIn.getYaw() * 360) / 256.0F : player.rotationYaw;
            float f1 = packetIn.func_149060_h() ? (float)(packetIn.getPitch() * 360) / 256.0F : player.rotationPitch;
            player.setPositionAndRotation2(d0, d1, d2, f, f1, 3, true);
            player.onGround = packetIn.getOnGround();
        }
    }

    public void handleEntityTeleport(S18PacketEntityTeleport packetIn, Entity entity)
    {
        if (entity != null)
        {
            entity.serverPosX = packetIn.getX();
            entity.serverPosY = packetIn.getY();
            entity.serverPosZ = packetIn.getZ();
            double d0 = (double)entity.serverPosX / 32.0D;
            double d1 = (double)entity.serverPosY / 32.0D;
            double d2 = (double)entity.serverPosZ / 32.0D;
            float f = (float)(packetIn.getYaw() * 360) / 256.0F;
            float f1 = (float)(packetIn.getPitch() * 360) / 256.0F;

            if (Math.abs(entity.posX - d0) < 0.03125D && Math.abs(entity.posY - d1) < 0.015625D && Math.abs(entity.posZ - d2) < 0.03125D)
            {
                entity.setPositionAndRotation2(entity.posX, entity.posY, entity.posZ, f, f1, 3, true);
            }
            else
            {
                entity.setPositionAndRotation2(d0, d1, d2, f, f1, 3, true);
            }

            entity.onGround = packetIn.getOnGround();
        }
    }

    public void handleEntityHeadLook(S19PacketEntityHeadLook packetIn, Entity entity)
    {
        if (entity != null)
        {
            float f = (float)(packetIn.getYaw() * 360) / 256.0F;
            entity.setRotationYawHead(f);
        }
    }

    private static class PacketDelayTimer extends MSTimer {

        Packet<?> packet;

        public PacketDelayTimer(Packet<?> packet) {
            this.packet = packet;
            this.reset();
        }

        public Packet getPacket() {
            return packet;
        }

    }

}
