package dev.menace.ui.clickgui.dropdown.components.settings;

import dev.menace.module.settings.ListSetting;
import dev.menace.utils.misc.MathUtils;
import dev.menace.utils.render.ColorUtils;
import dev.menace.utils.render.RenderUtils;

import java.awt.*;

public class DropdownListSetting extends DropdownSetting {

    int height = 20;

    int expandedHeight = 20;
    boolean expanded = false;

    public DropdownListSetting(ListSetting setting) {
        super(setting);

        expandedHeight = 20 + (setting.getOptions().length * 20);
    }

    @Override
    public void render(int x, int y, int mouseX, int mouseY) {
        //TODO: Animation?
        RenderUtils.drawRect(x, y, x + width, y + (expanded ? expandedHeight : height), ColorUtils.setAlpha(Color.BLACK, 150).getRGB());

        RenderUtils.drawRect(x, y, x + 2, y + 20, Color.WHITE.getRGB());

        fontRenderer.drawString(setting.getName(), x + 3, y + 6, -1);

        if (expanded) {
            y += 20;
            for (String option : ((ListSetting) setting).getOptions()) {
                RenderUtils.drawRect(x, y, x + 2, y + 20, Color.WHITE.getRGB());
                if (option.equals(((ListSetting) setting).getValue())) {
                    fontRenderer.drawString("§a" + option + "§r", x + 3, y + 6, -1);
                } else {
                    fontRenderer.drawString(option, x + 3, y + 6, -1);
                }
                y += 20;
            }
        }

    }

    @Override
    public void mouseClicked(int x, int y, int mouseX, int mouseY, int mouseButton) {
        if (MathUtils.isMouseHovered(x, y, width, height, mouseX, mouseY)) {
            expanded = !expanded;
        }

        if (MathUtils.isMouseHovered(x, y + 20, width, expandedHeight - 20, mouseX, mouseY) && expanded) {
            int yOffset = 20;
            for (String option : ((ListSetting) setting).getOptions()) {
                if (MathUtils.isMouseHovered(x, y + yOffset, width, 20, mouseX, mouseY)) {
                    ((ListSetting) setting).setValue(option);
                }
                yOffset += 20;
            }
        }
    }

    @Override
    public int getHeight() {
        return expanded ? expandedHeight : height;
    }
}
