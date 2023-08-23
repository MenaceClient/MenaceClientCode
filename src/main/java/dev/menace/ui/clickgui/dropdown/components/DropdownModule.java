package dev.menace.ui.clickgui.dropdown.components;

import dev.menace.Menace;
import dev.menace.module.Module;
import dev.menace.module.settings.*;
import dev.menace.ui.clickgui.dropdown.components.settings.*;
import dev.menace.utils.math.MathUtils;
import dev.menace.utils.render.ColorUtils;
import dev.menace.utils.render.RenderUtils;
import dev.menace.utils.render.font.MenaceFontRenderer;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.ArrayList;

public class DropdownModule {

    Module module;
    private int width, height;
    private int offsetY = 0;
    private boolean expanded = false;
    private boolean binding = false;
    MenaceFontRenderer fr;
    ArrayList<DropdownSetting> settings = new ArrayList<>();

    public DropdownModule(Module module) {
        this.module = module;
        this.width = 125;
        this.height = 20;

        this.fr = Menace.instance.sfPro;

        for (Setting setting : module.getSettings()) {
            if (setting instanceof DividerSetting) {
                settings.add(new DropdownDividerSetting((DividerSetting) setting));
            } else if (setting instanceof BooleanSetting) {
                settings.add(new DropdownToggleSetting((BooleanSetting) setting));
            } else if (setting instanceof NumberSetting) {
                settings.add(new DropdownSliderSetting((NumberSetting) setting));
            } else if (setting instanceof ListSetting) {
                settings.add(new DropdownListSetting((ListSetting) setting));
            }
        }
    }

    public void draw(int x, int y, int mouseX, int mouseY) {

        RenderUtils.drawRect(x, y, x + width, y + height, ColorUtils.setAlpha(module.isToggled() ? Color.RED : Color.BLACK, 150).getRGB());
        String bind = binding ? "Binding..." : "Bind: " + Keyboard.getKeyName(module.getKeybind());
        fr.drawString(module.getName() + " §8[" + bind + "]§r", x + 2, y + 5, Color.WHITE.getRGB());

        if (expanded) {
            module.onGuiUpdate();
            int offY = y + 20;
            for (DropdownSetting setting : settings) {
                if (!setting.setting.isVisible()) continue;
                setting.render(x, offY, mouseX, mouseY);
                offY += setting.getHeight();
            }

            offsetY = offY - (y + 20);
        }
    }

    public void mouseClicked(int x, int y, int mouseX, int mouseY, int mouseButton) {
        if (MathUtils.isMouseHovered(x, y, width, height, mouseX, mouseY)) {
            if (mouseButton == 0) {
                module.toggle();
            } else if (mouseButton == 1) {
                expanded = !expanded;
            } else if (mouseButton == 2) {
                binding = true;
            }
        }

        if (expanded) {
            int offY = y + 20;
            for (DropdownSetting setting : settings) {
                if (!setting.setting.isVisible()) continue;
                setting.mouseClicked(x, offY, mouseX, mouseY, mouseButton);
                offY += setting.getHeight();
            }
        }
    }

    public void keyTyped(char typedChar, int keyCode) {
        //Keybinds
        if (binding) {
            if (keyCode == Keyboard.KEY_ESCAPE) {
                binding = false;
            } else if (keyCode == Keyboard.KEY_DELETE) {
                module.setKeybind(Keyboard.KEY_NONE);
                binding = false;
            } else {
                module.setKeybind(keyCode);
                binding = false;
            }
        }
    }

    public int getHeight() {
        return height + (expanded ? offsetY : 0);
    }
}
