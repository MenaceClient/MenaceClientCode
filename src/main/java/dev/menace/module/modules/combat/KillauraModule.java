package dev.menace.module.modules.combat;

import dev.menace.event.Listener;
import dev.menace.event.annotations.EventLink;
import dev.menace.event.events.EventMove;
import dev.menace.event.events.EventPreMotion;
import dev.menace.module.Category;
import dev.menace.module.Module;
import dev.menace.module.settings.BooleanSetting;
import dev.menace.module.settings.DividerSetting;
import dev.menace.module.settings.ListSetting;
import dev.menace.module.settings.NumberSetting;
import dev.menace.utils.math.MathUtils;
import dev.menace.utils.misc.ChatUtils;
import dev.menace.utils.player.RotationUtils;
import dev.menace.utils.player.SprintHandler;
import dev.menace.utils.raycast.RaycastUtils;
import dev.menace.utils.timer.MSTimer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

public class KillauraModule extends Module {

    ArrayList<EntityLivingBase> targets = new ArrayList<>();
    MSTimer delayTimer = new MSTimer();
    long currentDelay;
    float[] lastRotations = new float[2];

    //Switch
    int switchIndex;
    MSTimer switchTimer = new MSTimer();

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
        addSettings(range, aps, attackMode, switchDelay, switchTargets, rotationDivider, attackPoint, sortMode, fov, ticksexisted, keepsprint, throughWalls, raycast, movementFix, targetdivider);
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
        super.onEnable();
    }

    @Override
    public void onDisable() {
        targets.clear();
        SprintHandler.resetPriority(100);
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

            SprintHandler.resetPriority(100);
            lastRotations = new float[]{mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch};
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

        //Calculate the percentage speed we should use
        double speedFactor = (rotationSpeed.getValue() / 10);
        float[] rotations = RotationUtils.getFixedRotation(RotationUtils.getAuraRotations(attackPos, lastRotations, speedFactor), lastRotations);

        //Handle Jitter
        if (jitter.getValue()) {
            float jitter = jitterStrength.getValueF() / 10;
            rotations = RotationUtils.doJitter(rotations, target, jitter, range.getValue());
        }

        //ChatUtils.message("Rotations: " + rotations[0] + " | " + rotations[1]);
        event.setYaw(rotations[0]);
        event.setPitch(rotations[1]);
        lastRotations = rotations;

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
    Listener<EventMove> onMove = event -> {
        //Movement correction
        //TODO: Improve this
        if (movementFix.getValue() && !targets.isEmpty()) {

            if (!keepsprint.getValue()) {
                SprintHandler.setSprinting(false, 100);
            }

            double motionX = event.getX();
            double motionZ = event.getZ();

            double speed = Math.sqrt(motionX * motionX + motionZ * motionZ);

            //Check if the player is moving
            if (speed <= 0.22) {
                return;
            }

            float yaw = lastRotations[0];

            //calculate moving direction
            double movingDirection = Math.atan2(motionZ, motionX) * 180 / Math.PI - 90;

            //calculate the difference between the player yaw and the moving direction
            double difference = Math.abs(MathUtils.getDistanceBetweenAngles(yaw, movingDirection));

            //if we are moving forward we don't need to correct the player
            if (difference < 45 || difference > 135) return;

            //If we are not moving forward we need to slow down the player to the default speed (0.22)
            //Exact Value: 0.21583238522039938
            double defaultSpeed = 0.21583238522039938;
            double newSpeed = defaultSpeed / speed;

            //Calculate the new motionX and motionZ
            event.setX(motionX * newSpeed);
            event.setZ(motionZ * newSpeed);

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
                .sorted(comparator)
                .forEach(targets::add);
    }

}
