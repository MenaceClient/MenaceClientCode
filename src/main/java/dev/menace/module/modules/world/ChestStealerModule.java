package dev.menace.module.modules.world;

import dev.menace.event.Listener;
import dev.menace.event.annotations.EventLink;
import dev.menace.event.events.EventReceivePacket;
import dev.menace.event.events.EventRender2D;
import dev.menace.event.events.EventSendPacket;
import dev.menace.event.events.EventUpdate;
import dev.menace.module.Category;
import dev.menace.module.Module;
import dev.menace.module.settings.BooleanSetting;
import dev.menace.module.settings.NumberSetting;
import dev.menace.utils.math.MathUtils;
import dev.menace.utils.player.InventoryUtils;
import dev.menace.utils.render.RenderUtils;
import dev.menace.utils.timer.MSTimer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.server.S2DPacketOpenWindow;

import java.util.ArrayList;
import java.util.List;

public class ChestStealerModule extends Module {

    private boolean direction = true;
    int slotAmt;
    List<Slot> slotList = new ArrayList<>();
    MSTimer delayTimer = new MSTimer();
    long nextDelay;
    public boolean isInChest;
    public GuiChest guiChest;

    //Settings
    NumberSetting minDelay = new NumberSetting("Delay Min", true, 90, 0, 1000, 10, true);
    NumberSetting maxDelay = new NumberSetting("Delay Max", true, 100, 0, 1000, 10, true);
    BooleanSetting closeScreen = new BooleanSetting("CloseScreen", true, false);
    BooleanSetting randomize = new BooleanSetting("Randomize", true, true);
    BooleanSetting chestOnly = new BooleanSetting("OnlyChests", true, true);
    BooleanSetting noGui = new BooleanSetting("NoGui", true, true);

    public ChestStealerModule() {
        super("ChestStealer", "Steals all items from chests.", Category.WORLD);
        addSettings(minDelay, maxDelay, closeScreen, randomize, chestOnly, noGui);
    }

    @Override
    public void onGuiUpdate() {
        if (maxDelay.getValue() < minDelay.getValue()) {
            maxDelay.setValue(minDelay.getValue());
        }
        super.onGuiUpdate();
    }

    @Override
    public void onEnable() {
        reset();
        super.onEnable();
    }

    @EventLink
    Listener<EventUpdate> onUpdate = event -> {
        if (mc.currentScreen instanceof GuiChest && isInChest) {
            guiChest = (GuiChest) mc.currentScreen;
            if (isEmpty(guiChest) || isInvFull()) {
                reset();
                if (closeScreen.getValue()) {
                    mc.thePlayer.closeScreen();
                }
                return;
            }
            if (noGui.getValue()) {
                mc.displayGuiScreen(null);
            }
        }

        if (guiChest == null) {
            reset();
            return;
        }

        if (!isInChest || !delayTimer.hasTimePassed(nextDelay)) {
            return;
        }

        if (slotList.isEmpty()) {
            for (int i = 0; i < guiChest.inventoryRows * 9; i++) {
                Slot slot = guiChest.inventorySlots.getSlot(i);
                if (slot.getHasStack()) {
                    slotList.add(slot);
                }
            }

            if (!slotList.isEmpty()) {
                slotAmt = slotList.size();
            }
        }

        int i = randomize.getValue() ? MathUtils.randInt(0, slotList.size()) : 0;

        Slot slot = slotList.get(i);
        slotList.remove(slot);

        InventoryUtils.shiftClick(slot.slotNumber, guiChest.inventorySlots.windowId);

        if ((slotList.isEmpty() || isInvFull()) && closeScreen.getValue()) {
            reset();
            mc.thePlayer.closeScreen();
            return;
        }

        delayTimer.reset();
        nextDelay = MathUtils.randLong(minDelay.getValueL(), maxDelay.getValueL());
    };

    @EventLink
    Listener<EventSendPacket> onSendPacket = event -> {
        if (event.getPacket() instanceof C0DPacketCloseWindow) {
            reset();
        }
    };

    @EventLink
    Listener<EventReceivePacket> onReceivePacket = event -> {
        if (event.getPacket() instanceof S2DPacketOpenWindow) {
            S2DPacketOpenWindow packet = (S2DPacketOpenWindow) event.getPacket();
            if (!chestOnly.getValue() || packet.getWindowTitle().getUnformattedText().equalsIgnoreCase(I18n.format("container.chest") )
                    || packet.getWindowTitle().getUnformattedText().equalsIgnoreCase(I18n.format("container.chestDouble"))) {
                isInChest = true;
            }
        }
    };

    @EventLink
    Listener<EventRender2D> onRender2D = event -> {
        if (!isInChest || !noGui.getValue()) {return;}

        ScaledResolution sr = new ScaledResolution(mc);
        mc.fontRendererObj.drawCenteredString("Stealing chest...", sr.getScaledWidth() / 2, (sr.getScaledHeight() / 2) + 10, -1);

        //Progress bar
        int width = 100;
        int height = 10;
        int x = (sr.getScaledWidth() / 2) - width + 50;
        int y = (sr.getScaledHeight() / 2) - height + 30;

        double minPercent = getPercent(slotAmt - slotList.size(), slotAmt);

        double percent = minPercent + ((getPercent(delayTimer.timePassed(), nextDelay) / 10));

        RenderUtils.drawRect(x, y, x + width, y + height, 0x99000000);
        RenderUtils.drawRect(x, y, x + percent, y + height, 0x99FFFFFF);

    };

    private double getPercent(double currentValue, double totalValue) {
        return ((currentValue / totalValue) * 100);
    }

    private boolean isEmpty(GuiChest gui) {
        for (int i = 0; i < gui.inventoryRows * 9; i++) {
            Slot slot = gui.inventorySlots.getSlot(i);
            if (slot.getHasStack()) {
                return false;
            }
        }
        return true;
    }

    private boolean isInvFull() {
        for(int index = 9; index <= 44; ++index) {
            ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(index).getStack();
            if (stack == null) {
                return false;
            }
        }

        return true;
    }

    private void reset() {
        delayTimer.reset();
        nextDelay = MathUtils.randLong(minDelay.getValueL(), maxDelay.getValueL());
        slotList.clear();
        isInChest = false;
        guiChest = null;
        slotAmt = 0;
    }

}
