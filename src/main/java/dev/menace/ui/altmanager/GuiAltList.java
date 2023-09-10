package dev.menace.ui.altmanager;

import dev.menace.utils.session.Alt;
import dev.menace.utils.session.AltHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;

import java.util.ArrayList;
import java.util.List;

public class GuiAltList extends GuiListExtended {

    private final GuiAltManager owner;
    List<AltListEntry> altList = new ArrayList<>();
    private int selectedSlotIndex = -1;

    public GuiAltList(GuiAltManager owner, Minecraft mcIn, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn) {
        super(mcIn, widthIn, heightIn, topIn, bottomIn, slotHeightIn);
        this.owner = owner;
        this.refreshList();
    }

    @Override
    public IGuiListEntry getListEntry(int index) {
        return altList.get(index);
    }

    @Override
    protected int getSize() {
        return altList.size();
    }

    public void setSelectedSlotIndex(int selectedSlotIndexIn)
    {
        this.selectedSlotIndex = selectedSlotIndexIn;
    }

    protected boolean isSelected(int slotIndex)
    {
        return slotIndex == this.selectedSlotIndex;
    }

    public Alt getSelectedAlt() {
        return altList.get(selectedSlotIndex).getAlt();
    }

    public void refreshList() {
        altList.clear();
        AltHandler.loadAlts();
        for (Alt alt : AltHandler.getAlts()) {
            altList.add(new AltListEntry(owner, alt));
        }
    }
}
