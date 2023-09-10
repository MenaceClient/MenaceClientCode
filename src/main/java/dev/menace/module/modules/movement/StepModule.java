package dev.menace.module.modules.movement;

import dev.menace.event.Listener;
import dev.menace.event.annotations.EventLink;
import dev.menace.event.events.EventPreMotion;
import dev.menace.event.events.EventStep;
import dev.menace.event.events.EventUpdate;
import dev.menace.module.Category;
import dev.menace.module.Module;
import dev.menace.module.settings.ListSetting;
import dev.menace.module.settings.NumberSetting;
import dev.menace.utils.bypass.BypassUtils;
import dev.menace.utils.player.PacketUtils;
import dev.menace.utils.timer.MSTimer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;

public class StepModule extends Module {

    ListSetting mode = new ListSetting("Mode", true, "Normal", "Vanilla", "NCP", "OldNCP", "Verus", "Motion");
    NumberSetting height = new NumberSetting("Height", true, 1, 1, 10, 0.5, false);
    NumberSetting timer = new NumberSetting("Timer", true, 0.3, 0.1, 1, 0.1, false);

    //MotionStep
    int motionStepState = 0;

    //Smooth
    MSTimer smoothTimer = new MSTimer();
    boolean hasStepped = false;

    public StepModule() {
        super("Step", "Step up full blocks.", Category.MOVEMENT);
        addSettings(mode, height, timer);
    }

    @Override
    public void onEnable() {
        motionStepState = 0;
        smoothTimer.reset();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1;
        hasStepped = false;
        super.onDisable();
    }

    @EventLink
    Listener<EventUpdate> onUpdate = event -> {
        this.setDisplayName(mode.getValue());

        if (smoothTimer.hasTimePassed(100) && hasStepped) {
            mc.timer.timerSpeed = 1;
            hasStepped = false;
            smoothTimer.reset();
        }
    };

    @EventLink
    Listener<EventPreMotion> onPreMotion = event -> {
        if (mode.getValue().equalsIgnoreCase("Motion")) {
            if (motionStepState == 1) {
                mc.thePlayer.jump();
                motionStepState = 2;
            } else if (motionStepState == 2 && !mc.thePlayer.isCollidedHorizontally) {
                mc.thePlayer.motionY = 0;
                motionStepState = 0;
            }
        }
    };

    @EventLink
    Listener<EventStep> onStep = event -> {
        if (event.getState() == EventStep.StepState.POST && event.getStepHeight() > 0.5f) {
            mc.timer.timerSpeed = (float) timer.getValue();
            hasStepped = true;
        }

        if (mode.getValue().equalsIgnoreCase("Verus")) {
            BypassUtils.sendVerusFunnyPackets();
        } else if (mode.getValue().equalsIgnoreCase("OldNCP") || mode.getValue().equalsIgnoreCase("NCP")) {
            if (!canStep()) return;

            if (event.getState() == EventStep.StepState.PRE) {
                event.setStepHeight(mode.getValue().equalsIgnoreCase("NCP") ? 1f : Math.min(height.getValueF(), 2));
            } else {
                if (event.getStepHeight() < 1) return;

                double[] packets = new double[0];
                if (event.getStepHeight() == 1) {
                    packets = new double[] {0.41999998688698, 0.7531999805212};
                } else if (event.getStepHeight() == 1.5) {
                    packets = new double[] {0.4, 0.75, 0.5, 0.41, 0.83, 1.16, 1.41999998688698};
                    //packets = new double[] {0.41999998688698, 0.7531999805212, 1.1199999, 0.83, 0.51, 1.16, 1.41999998688698};
                } else if (event.getStepHeight() == 2) {
                    packets = new double[] {0.4, 0.75, 0.5, 0.41, 0.83, 1.16, 1.41999998688698, 1.57, 1.58, 1.42};
                }

                fakeJump();
                for (double offset : packets) {
                    PacketUtils.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + offset, mc.thePlayer.posZ, true));
                }

                if (mode.getValue().equalsIgnoreCase("NCP")) {
                    PacketUtils.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + event.getStepHeight(), mc.thePlayer.posZ, true));
                }
            }
            return;
        } else if (mode.getValue().equalsIgnoreCase("Spider")) {
            return;
        } else if (mode.getValue().equalsIgnoreCase("Motion")) {
            if (event.getState() == EventStep.StepState.PRE && canStep() && mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0, 0.3, 0)).isEmpty()) {
                motionStepState = 1;
            }
            return;
        }

        event.setStepHeight(height.getValueF());
    };

    private boolean canStep() {
        return this.mc.thePlayer.isCollidedVertically && this.mc.thePlayer.onGround && this.mc.thePlayer.motionY < 0.0 && !this.mc.thePlayer.movementInput.jump;
    }

    private void fakeJump() {
        mc.thePlayer.isAirBorne = true;
        mc.thePlayer.triggerAchievement(StatList.jumpStat);
    }

}
