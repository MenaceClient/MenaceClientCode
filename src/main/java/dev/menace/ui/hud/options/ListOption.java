package dev.menace.ui.hud.options;

import dev.menace.utils.math.MathUtils;

public class ListOption extends HUDOption {

    String[] options;

    String selected;

    public ListOption(String name, String[] options, String selected) {
        super(name);
        this.options = options;
        this.selected = selected;
        this.update();
    }

    @Override
    public void render(double posX, double posY) {
        fontRenderer.drawStringWithShadow(this.getName() + ": " + selected, (float) posX, (float) posY, -1);
    }

    @Override
    public void mouseClicked(double posX, double posY, int mouseX, int mouseY, int mouseButton) {
        if (MathUtils.isMouseHovered((int) posX, (int) posY, fontRenderer.getStringWidth(this.getName() + ": " + selected), fontRenderer.getHeight(), mouseX, mouseY)) {
            int index = 0;
            for (String option : options) {
                if (option.equals(selected)) {
                    break;
                }
                index++;
            }
            index++;
            if (index >= options.length) {
                index = 0;
            }
            selected = options[index];

            this.update();
        }
    }

    public String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        this.selected = selected;
        this.update();
    }

    @Override
    public int getHeight() {
        return fontRenderer.getHeight();
    }
}
