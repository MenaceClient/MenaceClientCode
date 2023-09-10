package dev.menace.module.modules.combat;

import dev.menace.Menace;
import dev.menace.event.Listener;
import dev.menace.event.annotations.EventLink;
import dev.menace.event.events.EventJump;
import dev.menace.event.events.EventPostMotion;
import dev.menace.event.events.EventPreMotion;
import dev.menace.event.events.EventStrafe;
import dev.menace.module.Category;
import dev.menace.module.DontSaveState;
import dev.menace.module.Module;
import dev.menace.module.settings.BooleanSetting;
import dev.menace.module.settings.DividerSetting;
import dev.menace.module.settings.ListSetting;
import dev.menace.module.settings.NumberSetting;
import dev.menace.utils.math.MathUtils;
import dev.menace.utils.misc.ChatUtils;
import dev.menace.utils.player.EntityFakePlayer;
import dev.menace.utils.player.MovementUtils;
import dev.menace.utils.player.PacketUtils;
import dev.menace.utils.player.RotationUtils;
import dev.menace.utils.raycast.RaycastUtils;
import dev.menace.utils.timer.MSTimer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

@DontSaveState
public class KillauraModule extends Module {

    public ArrayList<EntityLivingBase> targets = new ArrayList<>();
    MSTimer delayTimer = new MSTimer();
    long currentDelay;
    float[] lastRotations = new float[2];

    //Switch
    int switchIndex;
    MSTimer switchTimer = new MSTimer();

    //AutoBlock
    public boolean blocking;
    boolean couldBlock;

    //Settings
    NumberSetting range = new NumberSetting("Range", true, 4, 3.3, 6, 0.1, false);
    NumberSetting aps = new NumberSetting("APS", true, 10, 1, 20, 1, false);
    ListSetting attackMode = new ListSetting("AttackMode", true, "Single", "Single", "Switch", "Multi");
    NumberSetting switchDelay = new NumberSetting("SwitchDelay", true, 500, 0, 1000, true);
    NumberSetting switchTargets = new NumberSetting("SwitchTargets", true, 1, 1, 5, true);
    NumberSetting rotationSpeed = new NumberSetting("RotationSpeed", true, 10, 1, 20, true);
    BooleanSetting jitter = new BooleanSetting("Jitter", true, false);
    NumberSetting jitterStrength = new NumberSetting("JitterStrength", true, 1, 1, 5, true);
    DividerSetting rotationDivider = new DividerSetting("Rotation", true, rotationSpeed, jitter, jitterStrength);
    ListSetting autoblock = new ListSetting("Autoblock", true, "None", "None", "Vanilla", "NCP", "Fake");
    ListSetting attackPoint = new ListSetting("AttackPoint", true, "Head", "Head", "Eyes", "Body", "Cock", "Feet", "Smart");
    ListSetting sortMode = new ListSetting("Sort", true, "Health", "Health", "Distance", "Angle", "TicksExisted");
    NumberSetting fov = new NumberSetting("FOV", true, 180, 10, 180, true);
    NumberSetting ticksexisted = new NumberSetting("TicksExisted", true, 0, 0, 100, true);
    public BooleanSetting keepsprint = new BooleanSetting("KeepSprint", true, true);
    BooleanSetting throughWalls = new BooleanSetting("ThroughWalls", true, false);
    BooleanSetting raycast = new BooleanSetting("Raycast", true, true);
    BooleanSetting movementFix = new BooleanSetting("MovementFix", true, false);
    BooleanSetting players = new BooleanSetting("Players", true, true);
    BooleanSetting monsters = new BooleanSetting("Mobs", true, false);
    BooleanSetting passives = new BooleanSetting("Animals", true, false);
    BooleanSetting invisibles = new BooleanSetting("Invisibles", true, false);
    DividerSetting targetdivider = new DividerSetting("Targets", true, players, monsters, passives, invisibles);


    public KillauraModule() {
        super("Killaura", "Attacks targets for you.", Category.COMBAT);
        addSettings(range, aps, attackMode, switchDelay, switchTargets, rotationDivider, autoblock, attackPoint, sortMode, fov, ticksexisted, keepsprint, throughWalls, raycast, movementFix, targetdivider);
    }

    @Override
    public void onGuiUpdate() {
        switchDelay.setVisible(attackMode.getValue().equalsIgnoreCase("Switch"));
        switchTargets.setVisible(attackMode.getValue().equalsIgnoreCase("Switch"));
        jitterStrength.setVisible(jitter.getValue());
        super.onGuiUpdate();
    }

    @Override
    public void onEnable() {
        delayTimer.reset();
        switchTimer.reset();
        switchIndex = 0;
        lastRotations = new float[]{mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch};
        currentDelay = MathUtils.calculateDelay(aps.getValueI());
        blocking = false;
        couldBlock = false;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        targets.clear();
        if (blocking) {
            releaseBlocking();
        }
        super.onDisable();
    }

    @EventLink
    Listener<EventPreMotion> onPreMotion = event -> {
        getTargets();

        if (targets.isEmpty()) {

            //Fix up rotations
            /*if (MathUtils.getDistanceBetweenAngles(lastRotations[0], mc.thePlayer.rotationYaw) > 5 || MathUtils.getDistanceBetweenAngles(lastRotations[1], mc.thePlayer.rotationPitch) > 5) {
                float[] rotations = RotationUtils.smoothRotations(new float[]{mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch}, lastRotations, rotationSpeed.getValue() / 10);
                event.setYaw(rotations[0]);
                event.setPitch(rotations[1]);
                lastRotations = rotations;
                return;
            }*/

            couldBlock = false;
            lastRotations = new float[]{mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch};

            if (blocking) {
                releaseBlocking();
            }

            return;
        }

        EntityLivingBase target;

        if (attackMode.getValue().equalsIgnoreCase("Switch")) {

            if (switchTimer.hasTimePassed((long) switchDelay.getValue())) {
                switchTimer.reset();
                switchIndex++;
            }

            if (switchIndex >= switchTargets.getValueI() || switchIndex >= targets.size()) {
                switchIndex = 0;
            }

            target = targets.get(switchIndex);
        } else {
            target = targets.get(0);
        }

        Vec3 attackPos = RotationUtils.getCenter(target.getEntityBoundingBox());
        switch (attackPoint.getValue()) {
            case "Head":
                attackPos = RotationUtils.getHead(target.getEntityBoundingBox());
                break;
            case "Eyes":
                attackPos = RotationUtils.getEyes(target);
                break;
            case "Body":
                attackPos = RotationUtils.getCenter(target.getEntityBoundingBox());
                break;
            case "Cock":
                attackPos = RotationUtils.getCock(target.getEntityBoundingBox());
                break;
            case "Feet":
                attackPos = RotationUtils.getFeet(target.getEntityBoundingBox());
                break;
            case "Smart":
                attackPos = RotationUtils.calculateClosestPoint(target.getEntityBoundingBox(), lastRotations);
                break;
        }

        if (!keepsprint.getValue() && movementFix.getValue()) {
            mc.thePlayer.setSprinting(false);
        }

        //Calculate the percentage speed we should use
        double speedFactor = (rotationSpeed.getValue() / 10);
        float[] rotations = RotationUtils.getAuraRotations(attackPos, lastRotations, speedFactor);

        //Handle Jitter
        if (jitter.getValue()) {
            float jitter = jitterStrength.getValueF() / 10;
            rotations = RotationUtils.doJitter(rotations, target, jitter, range.getValue());
        }

        event.setYaw(rotations[0]);
        event.setPitch(rotations[1]);
        lastRotations = rotations;

        //AutoBlock
        couldBlock = canBlock();
        if (canBlock()) {
            beforeAttackAutoblock();
        } else {
            releaseBlocking();
        }

        //Check if the player is actually looking at the entity
        if (raycast.getValue() && !RaycastUtils.isCrosshairOnEntity(rotations, target, range.getValue())) {
            return;
        }

        if (!delayTimer.hasTimePassed(currentDelay)) return;

        mc.thePlayer.swingItem();

        if (attackMode.getValue().equalsIgnoreCase("Multi")) {
            for (EntityLivingBase entity : targets) {
                mc.playerController.attackEntity(mc.thePlayer, entity);
            }
        } else {
            mc.playerController.attackEntity(mc.thePlayer, target);
        }
        currentDelay = MathUtils.calculateDelay(aps.getValueI());
        delayTimer.reset();

    };

    @EventLink
    Listener<EventPostMotion> onPostMotion = event -> {
        if (couldBlock) {
            postAutoblock();
        }
    };

    @EventLink
    Listener<EventStrafe> onMoveFix = event -> {
        if (movementFix.getValue() && !targets.isEmpty()) {
            event.setYaw(lastRotations[0]);

            float diff = MathHelper.wrapAngleTo180_float(MathHelper.wrapAngleTo180_float(lastRotations[0]) - MathHelper.wrapAngleTo180_float(MovementUtils.getPlayerDirection())) + 22.5F;

            if (diff < 0) {
                diff = 360 + diff;
            }

            int a = (int) (diff / 45.0);

            float value = event.getForward() != 0 ? Math.abs(event.getForward()) : Math.abs(event.getStrafe());

            float forward = value;
            float strafe = 0;

            for (int i = 0; i < 8 - a; i++) {
                float dirs[] = MovementUtils.incrementMoveDirection(forward, strafe);

                forward = dirs[0];
                strafe = dirs[1];
            }

            event.setForward(forward);
            event.setStrafe(strafe);
        }
    };

    @EventLink
    Listener<EventJump> onJump = event -> {
        if (movementFix.getValue() && !targets.isEmpty()) {
            event.setYaw(lastRotations[0]);
        }
    };

    private void getTargets() {
        targets.clear();

        Comparator<EntityLivingBase> comparator;
        switch (sortMode.getValue()) {
            default:
            case "Health":
                comparator = Comparator.comparingDouble(EntityLivingBase::getHealth);
                break;
            case "Distance":
                comparator = Comparator.comparingDouble(entity -> mc.thePlayer.getDistanceToEntity(entity));
                break;
            case "Angle":
                comparator = Comparator.comparingDouble(entity -> MathUtils.getDistanceBetweenAngles(mc.thePlayer.rotationYaw, RotationUtils.getBasicRotations(entity)[0]));
                break;
            case "TicksExisted":
                comparator = Comparator.comparingInt(entity -> entity.ticksExisted);
                break;
        }

        ArrayList<EntityLivingBase> targetlist = (ArrayList<EntityLivingBase>) mc.theWorld.loadedEntityList.stream().filter(EntityLivingBase.class::isInstance).map(EntityLivingBase.class::cast).collect(Collectors.toList());
        targetlist.stream()
                .filter(e -> e != mc.thePlayer)
                .filter(e -> !e.isDead && e.getHealth() > 0)
                .filter(e -> mc.thePlayer.getDistanceToEntity(e) <= range.getValue())
                .filter(e -> e.ticksExisted >= ticksexisted.getValue())
                .filter(e -> MathUtils.getDistanceBetweenAngles(mc.thePlayer.rotationYaw, RotationUtils.getBasicRotations(e)[0]) <= fov.getValue())
                .filter(e -> mc.thePlayer.canEntityBeSeen(e) || throughWalls.getValue())
                .filter(e -> players.getValue() && e instanceof EntityPlayer || monsters.getValue() && e instanceof EntityMob || passives.getValue() && (e instanceof EntityAnimal || e instanceof EntityVillager))
                .filter(e -> invisibles.getValue() || !e.isInvisible())
                .filter(e -> !(e instanceof EntityFakePlayer))
                .sorted(comparator)
                .forEach(targets::add);
    }

    private boolean canBlock() {
        return mc.thePlayer != null && !targets.isEmpty() && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword;
    }

    private void beforeAttackAutoblock() {
        switch (autoblock.getValue()) {
            case "Vanilla":
                if (blocking) {
                    PacketUtils.sendBlocking(false);
                    blocking = true;
                }
                break;
            case "NCP":
                if (blocking) {
                    PacketUtils.releaseUseItem();
                    blocking = false;
                }
                break;
        }
    }

    private void postAutoblock() {
        switch (autoblock.getValue()) {
            case "NCP":
                if (!blocking) {
                    PacketUtils.sendBlocking(true);
                    blocking = true;
                }
                break;
        }
    }

    private void releaseBlocking() {
        if(blocking) {
            switch (autoblock.getValue()) {
                case "Vanilla":
                case "NCP":
                    if (mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
                        PacketUtils.releaseUseItem();
                        blocking = false;
                    }
                    break;
            }
        }
    }

}
