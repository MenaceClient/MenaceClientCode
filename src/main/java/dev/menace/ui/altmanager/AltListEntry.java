package dev.menace.ui.altmanager;

import dev.menace.utils.render.RenderUtils;
import dev.menace.utils.session.Alt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

import java.util.Objects;

public class AltListEntry implements GuiListExtended.IGuiListEntry {

    private final Minecraft mc = Minecraft.getMinecraft();
    private final GuiAltManager owner;
    private final Alt alt;

    public AltListEntry(GuiAltManager owner, Alt alt) {
        this.owner = owner;
        this.alt = alt;
    }

    @Override
    public void setSelected(int p_178011_1_, int p_178011_2_, int p_178011_3_) {

    }

    @Override
    public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
        this.mc.fontRendererObj.drawString(this.alt.getUsername(), x + 32 + 3, y + 1, 16777215);
        if (this.alt.isCracked()) {
            this.mc.fontRendererObj.drawString("Cracked", x + 32 + 3, y + 12, 8421504);
        } else {
            this.mc.fontRendererObj.drawString(this.alt.getEmail(), x + 32 + 3, y + 12, 8421504);
        }

        String loginStatus = Objects.equals(mc.session.getUsername(), alt.getUsername()) ? EnumChatFormatting.GREEN + "Logged in" : EnumChatFormatting.GRAY + "Not logged in";

        this.mc.fontRendererObj.drawString(loginStatus, x + 32 + 3, y + 23, 8421504);

        if (this.alt.isCracked()) {
            //Draw default skin
            RenderUtils.drawHead(new ResourceLocation("textures/entity/steve.png"), x, y, 32, 32);
        } else {
            //Draw alt skin

        }

    }

    @Override
    public boolean mousePressed(int slotIndex, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_, int p_148278_6_) {
        this.owner.selectAlt(slotIndex);
        return false;
    }

    @Override
    public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {

    }

    public Alt getAlt() {
        return alt;
    }
}
