package dev.menace.module.modules.player;

import dev.menace.event.Listener;
import dev.menace.event.annotations.EventLink;
import dev.menace.event.events.EventPreMotion;
import dev.menace.module.Category;
import dev.menace.module.Module;
import dev.menace.module.settings.BooleanSetting;
import dev.menace.module.settings.DividerSetting;
import dev.menace.module.settings.ListSetting;
import dev.menace.utils.misc.ChatUtils;
import dev.menace.utils.player.*;
import dev.menace.utils.world.BlockUtils;
import net.minecraft.util.BlockPos;

public class ScaffoldModule extends Module {

    //Settings
    ListSetting mode = new ListSetting("Mode", true, "Normal", "Normal", "Telly");
    BooleanSetting tower = new BooleanSetting("Tower", true, false);
    ListSetting towerMode = new ListSetting("Tower Mode", true, "Normal", "Normal", "NCP");
    BooleanSetting towerMove = new BooleanSetting("Tower Move", true, false);
    DividerSetting towerDivider = new DividerSetting("Tower", true, tower, towerMode, towerMove);
    BooleanSetting sprint = new BooleanSetting("Sprint", true, false);
    BooleanSetting keepY = new BooleanSetting("Keep Y", true, false);

    //Vars
    private float[] lastRotations = new float[2];
    private int oldSlot = -1;
    private double startY = -1;
    boolean isTowering;
    int towerHeight;

    public ScaffoldModule() {
        super("Scaffold", "Builds a bridge so you don't have to.", Category.PLAYER);
        addSettings(mode, towerDivider, sprint, keepY);
    }

    @Override
    public void onEnable() {
        oldSlot = mc.thePlayer.inventory.currentItem;
        lastRotations[0] = mc.thePlayer.rotationYaw;
        lastRotations[1] = mc.thePlayer.rotationPitch;
        startY = mc.thePlayer.posY;
        isTowering = false;
        towerHeight = 0;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        if (oldSlot != -1) {
            mc.thePlayer.inventory.currentItem = oldSlot;
            oldSlot = -1;
        }
        super.onDisable();
    }

    @EventLink
    Listener<EventPreMotion> onPreMotion = event -> {
        if (!MovementUtils.isMoving()) {
            startY = mc.thePlayer.posY - 1;
        }

        int bestSlot = InventoryUtils.getBlocksInHotbar();

        if (bestSlot == -1) {
            ChatUtils.message("You don't have any blocks in your hotbar.");
            this.toggle();
            return;
        }

        mc.thePlayer.setSprinting(sprint.getValue());

        if (mc.thePlayer.inventory.currentItem != bestSlot) {
            mc.thePlayer.inventory.currentItem = bestSlot;
        }

        BlockPos belowPlayer = new BlockPos(mc.thePlayer.posX, keepY.getValue() ? startY : mc.thePlayer.posY - 1, mc.thePlayer.posZ);

        BlockPos bestPos = BlockUtils.findNearbyBlocks(belowPlayer);

        if (bestPos == null) {
            return;
        }

        PlacementInfo info = PlayerUtils.getInfoPlacement(bestPos);

        if (info == null) {
            return;
        }

        float[] rotations = RotationUtils.getScaffoldRotations(info.getNeighbor(), info.getSide(), lastRotations, 2);
        event.setYaw(rotations[0]);
        event.setPitch(rotations[1]);
        lastRotations = rotations;

        if (!BlockUtils.getMaterial(belowPlayer).isReplaceable()
            //|| !(mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0, -0.0001, 0)).isEmpty())
            || (mode.getValue().equalsIgnoreCase("Telly") && mc.thePlayer.motionY >= 0)) {
            return;
        }

        if (mc.gameSettings.keyBindJump.isKeyDown() && tower.getValue() && ((!keepY.getValue() && towerMove.getValue()) || !MovementUtils.isMoving()) && towerMode.getValue().equalsIgnoreCase("NCP")) {
            isTowering = true;
            if (towerHeight >= 10) {
                towerHeight = 0;
                mc.thePlayer.motionY = 0;
            } else {
                mc.thePlayer.motionY = 0.42;
            }
        } else if (towerMode.getValue().equalsIgnoreCase("NCP")){
            isTowering = false;
        }

        PlayerUtils.placeBlockSimple(bestPos, info);

        if (isTowering && towerMode.getValue().equalsIgnoreCase("NCP")) {
            towerHeight++;
        }

    };

}
