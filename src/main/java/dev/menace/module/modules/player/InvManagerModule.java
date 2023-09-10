package dev.menace.module.modules.player;

import dev.menace.Menace;
import dev.menace.event.Listener;
import dev.menace.event.annotations.EventLink;
import dev.menace.event.events.EventSendPacket;
import dev.menace.event.events.EventUpdate;
import dev.menace.module.Category;
import dev.menace.module.Module;
import dev.menace.module.settings.BooleanSetting;
import dev.menace.module.settings.DividerSetting;
import dev.menace.module.settings.NumberSetting;
import dev.menace.utils.math.MathUtils;
import dev.menace.utils.player.InventoryUtils;
import dev.menace.utils.player.PacketUtils;
import dev.menace.utils.timer.MSTimer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.*;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;

import java.util.ArrayList;
import java.util.List;

public class InvManagerModule extends Module {

    List<Slot> slotList = new ArrayList<>();
    MSTimer delayTimer = new MSTimer();
    long nextDelay;
    float damage, pickValue, axeValue, shovelValue = 0;
    int blockValue = 0;

    //Settings
    NumberSetting minDelay = new NumberSetting("Delay Min", true, 90, 0, 1000, 10, true);
    NumberSetting maxDelay = new NumberSetting("Delay Max", true, 100, 0, 1000, 10, true);
    BooleanSetting inInv = new BooleanSetting("In Inventory", true, true);
    BooleanSetting closeScreen = new BooleanSetting("CloseScreen", true, false);
    BooleanSetting randomize = new BooleanSetting("Randomize", true, true);
    NumberSetting swordSet = new NumberSetting("Sword Slot", true, 1, 1, 9, 1, true);
    NumberSetting pickSet = new NumberSetting("Pick Slot", true, 2, 1, 9, 1, true);
    NumberSetting axeSet = new NumberSetting("Axe Slot", true, 3, 1, 9, 1, true);
    NumberSetting shovelSet = new NumberSetting("Shovel Slot", true, 4, 1, 9, 1, true);
    NumberSetting gapSet = new NumberSetting("Gap Slot", true, 5, 1, 9, 1, true);
    NumberSetting blockSet = new NumberSetting("Block Slot", true, 6, 1, 9, 1, true);
    DividerSetting slotsDivider = new DividerSetting("Slots", true, swordSet, pickSet, axeSet, shovelSet, gapSet, blockSet);

    public InvManagerModule() {
        super("InventoryManager", "Manages your inventory.", Category.PLAYER);
        addSettings(minDelay, maxDelay, inInv, closeScreen, randomize, slotsDivider);
    }

    @Override
    public void onGuiUpdate() {
        if (maxDelay.getValue() < minDelay.getValue()) {
            maxDelay.setValue(minDelay.getValue());
        }

        closeScreen.setVisible(inInv.getValue());

        super.onGuiUpdate();
    }

    @Override
    public void onEnable() {
        delayTimer.reset();
        nextDelay = MathUtils.randLong(minDelay.getValueL(), maxDelay.getValueL());
        slotList.clear();
        super.onEnable();
    }

    @EventLink
    Listener<EventUpdate> onUpdate = event -> {

        int swordSlot = swordSet.getValueI() - 1;
        int pickSlot = pickSet.getValueI() - 1;
        int axeSlot = axeSet.getValueI() - 1;
        int shovelSlot = shovelSet.getValueI() - 1;
        int gappleSlot = gapSet.getValueI() - 1;
        int blockSlot = blockSet.getValueI() - 1;

        damage = InventoryUtils.getDamage(mc.thePlayer.inventory.getStackInSlot(swordSlot));

        pickValue = InventoryUtils.getToolEffect(mc.thePlayer.inventory.getStackInSlot(pickSlot));

        axeValue = InventoryUtils.getToolEffect(mc.thePlayer.inventory.getStackInSlot(axeSlot));

        shovelValue = InventoryUtils.getToolEffect(mc.thePlayer.inventory.getStackInSlot(shovelSlot));

        blockValue = mc.thePlayer.inventory.getStackInSlot(blockSlot) != null ?
                mc.thePlayer.inventory.getStackInSlot(blockSlot).stackSize : 0;


        scanInv();

        if (slotList.isEmpty()
                || !delayTimer.hasTimePassed(nextDelay)
                || (inInv.getValue() && !(mc.currentScreen instanceof GuiInventory))
                || Menace.instance.moduleManager.chestStealerModule.isInChest
                || !Menace.instance.moduleManager.killauraModule.targets.isEmpty()
                || Menace.instance.moduleManager.scaffoldModule.isToggled()
                || mc.thePlayer.hurtTime > 0) return;

        int o = randomize.getValue() ? MathUtils.randInt(0, slotList.size()) : 0;

        Slot slot = slotList.get(o);
        slotList.remove(slot);
        int i = slot.slotNumber;
        ItemStack is = slot.getStack();
        int durabilityThreshold = 50;

        if (!inInv.getValue()) {
            PacketUtils.sendPacketNoEvent(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
        }

        if (slot.getStack().getItem() instanceof ItemSword && InventoryUtils.getDamage(slot.getStack()) <= 0) {
            //DONT DROP MY FUCKING OP SWORD
            return;
        }

        //Sword
        if (is.getItem() instanceof ItemSword && InventoryUtils.getDamage(is) > damage && is.getMaxDamage() - is.getItemDamage() > durabilityThreshold) {
            InventoryUtils.swap(i, swordSlot);
            reset();
            return;
        }


        //Pickaxe
        if (InventoryUtils.getToolEffect(is) > pickValue && is.getItem() instanceof ItemPickaxe) {
            InventoryUtils.swap(i, pickSlot);
            reset();
            return;
        }

        //Axe
        if (InventoryUtils.getToolEffect(is) > axeValue && is.getItem() instanceof ItemAxe) {
            InventoryUtils.swap(i, axeSlot);
            reset();
            return;
        }

        //Shovel
        if (InventoryUtils.getToolEffect(is) > shovelValue && is.getItem() instanceof ItemSpade) {
            InventoryUtils.swap(i, shovelSlot);
            reset();
            return;
        }

        //Gapple
        if (is.getItem() instanceof ItemAppleGold && (mc.thePlayer.inventory.getStackInSlot(gappleSlot) == null || mc.thePlayer.inventory.getStackInSlot(gappleSlot).stackSize < 64)) {
            InventoryUtils.swap(i, gappleSlot);
            reset();
            return;
        }

        //Blocks
        if (is.stackSize > blockValue && is.getItem() instanceof ItemBlock && !InventoryUtils.BLOCK_BLACKLIST.contains(((ItemBlock) is.getItem()).getBlock())) {
            InventoryUtils.swap(i, blockSlot);
            reset();
            return;
        }

        //Armour
        if (is.getItem() instanceof ItemArmor) {
            ItemArmor armour = (ItemArmor) is.getItem();
            if (armour.armorType == 0 && InventoryUtils.getArmorStrength(is) > InventoryUtils.getArmorStrength(mc.thePlayer.inventory.armorInventory[3])) {
                InventoryUtils.drop(5);
                InventoryUtils.shiftClick(i, mc.thePlayer.inventoryContainer.windowId);
            } else if (armour.armorType == 1 && InventoryUtils.getArmorStrength(is) > InventoryUtils.getArmorStrength(mc.thePlayer.inventory.armorInventory[2])) {
                InventoryUtils.drop(6);
                InventoryUtils.shiftClick(i, mc.thePlayer.inventoryContainer.windowId);
            } else if (armour.armorType == 2 && InventoryUtils.getArmorStrength(is) > InventoryUtils.getArmorStrength(mc.thePlayer.inventory.armorInventory[1])) {
                InventoryUtils.drop(7);
                InventoryUtils.shiftClick(i, mc.thePlayer.inventoryContainer.windowId);
            } else if (armour.armorType == 3 && InventoryUtils.getArmorStrength(is) > InventoryUtils.getArmorStrength(mc.thePlayer.inventory.armorInventory[0])) {
                InventoryUtils.drop(8);
                InventoryUtils.shiftClick(i, mc.thePlayer.inventoryContainer.windowId);
            } else {
                InventoryUtils.drop(i);
            }
            reset();
            return;
        }

        //Clean
        if (InventoryUtils.TRASH.contains(is.getItem()) || is.getItem() instanceof ItemBlock && InventoryUtils.BLOCK_BLACKLIST.contains(((ItemBlock) is.getItem()).getBlock())) {
            InventoryUtils.drop(i);
            if (!inInv.getValue()) PacketUtils.sendPacketNoEvent(new C0DPacketCloseWindow());
            nextDelay = MathUtils.randLong(minDelay.getValueL(), maxDelay.getValueL());
            delayTimer.reset();
        } else if (i != 36 + swordSlot && is.getItem() instanceof ItemSword) {
            InventoryUtils.drop(i);
            reset();
        } else if (i != 36 + pickSlot && is.getItem() instanceof ItemPickaxe) {
            InventoryUtils.drop(i);
            reset();
        } else if (i != 36 + axeSlot && is.getItem() instanceof ItemAxe) {
            InventoryUtils.drop(i);
            reset();
        } else if (i != 36 + shovelSlot && is.getItem() instanceof ItemSpade) {
            InventoryUtils.drop(i);
            reset();
        }
    };

    @EventLink
    Listener<EventSendPacket> onSendPacket = event -> {
        if (event.getPacket() instanceof C0DPacketCloseWindow) {
            delayTimer.reset();
            nextDelay = MathUtils.randLong(minDelay.getValueL(), maxDelay.getValueL());
            slotList.clear();
        }
    };

    private void scanInv() {
        slotList.clear();
        for (int i = 9; i < 45; ++i) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                slotList.add(mc.thePlayer.inventoryContainer.getSlot(i));
            }
        }
    }

    private void reset() {
        if (!inInv.getValue()) PacketUtils.sendPacketNoEvent(new C0DPacketCloseWindow());
        nextDelay = MathUtils.randLong(minDelay.getValueL(), maxDelay.getValueL());
        delayTimer.reset();
    }

}
