package dev.menace.module.modules.render;

import dev.menace.module.Category;
import dev.menace.module.Module;
import dev.menace.ui.hud.GuiHUDEditor;
import org.lwjgl.input.Keyboard;

public class HUDEditorModule extends Module {

    public HUDEditorModule() {
        super("HUDEditor", "Edit the HUD", Category.RENDER);
    }

    @Override
    public void onEnable() {
        mc.displayGuiScreen(new GuiHUDEditor());

        super.onEnable();

        this.toggle();
    }

}
