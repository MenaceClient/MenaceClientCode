package dev.menace.ui.clickgui.intellij.components.impl;

import dev.menace.module.settings.ListSetting;
import dev.menace.ui.clickgui.intellij.components.Component;
import dev.menace.ui.clickgui.intellij.components.IntellijModule;

public class EnumComponent extends Component {

    public EnumComponent(int x, int y, IntellijModule owner, ListSetting setting) {
        super(x, y, owner, setting);
    }

    @Override
    public void initGui() {

    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {

    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        return false;
    }

    @Override
    public void onGuiClosed(int mouseX, int mouseY, int mouseButton) {

    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {

    }

    @Override
    public int getOffset() {
        return 0;
    }
}
