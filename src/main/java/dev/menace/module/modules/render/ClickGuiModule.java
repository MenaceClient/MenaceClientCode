package dev.menace.module.modules.render;

import dev.menace.module.Category;
import dev.menace.module.Module;
import dev.menace.module.settings.ListSetting;
import dev.menace.ui.clickgui.dropdown.DropdownClickGui;
import dev.menace.ui.clickgui.intellij.IntellijClickGui;
import dev.menace.ui.clickgui.menace.MenaceClickGui;
import org.lwjgl.input.Keyboard;

public class ClickGuiModule extends Module {

    ListSetting mode = new ListSetting("Mode", true, "Dropdown", "Dropdown", "Menace", "IntelliJ");

    public ClickGuiModule() {
        super("ClickGui", "Opens the ClickGui", Category.RENDER, Keyboard.KEY_RSHIFT);
        addSettings(mode);
    }

    @Override
    public void onEnable() {
        switch (mode.getValue()) {
            case "Dropdown":
                mc.displayGuiScreen(new DropdownClickGui());
                break;
            case "Menace":
                mc.displayGuiScreen(new MenaceClickGui());
                break;
            case "IntelliJ":
                mc.displayGuiScreen(new IntellijClickGui());
                break;
        }

        super.onEnable();

        this.toggle();
    }
}
