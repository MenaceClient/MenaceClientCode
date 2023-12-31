package dev.menace.ui.clickgui.menace.components.settings;

import dev.menace.module.settings.BooleanSetting;
import dev.menace.utils.math.MathUtils;
import dev.menace.utils.render.RenderUtils;
import dev.menace.utils.render.animtion.Animate;
import dev.menace.utils.render.animtion.Easing;

import java.awt.*;

public class ToggleComponent extends SettingComponent {

    Animate animation;

    public ToggleComponent(BooleanSetting setting) {
        super(setting);
        animation = new Animate().setMin(0).setMax(5).setSpeed(20).setEase(Easing.LINEAR).setReversed(false);
    }

    @Override
    public void draw(int mouseX, int mouseY, int x, int y) {
        RenderUtils.drawRoundedRect(x + 205, y + 5, x + 205 + 100, y + 5 + 20, 5, new Color(32, 31, 35).getRGB());
        fontRenderer.drawCenteredString(setting.getName(), x + 255, y + 15, Color.WHITE.getRGB());

        BooleanSetting set = (BooleanSetting) setting;

        animation.update();

        if (set.getValue()) {
            RenderUtils.drawFilledCircle(x + 205 + 5 + 5, y + 5 + 5 + 5, 5, Color.RED);
            RenderUtils.drawFilledCircle(x + 205 + 5 + 5, y + 5 + 5 + 5, animation.getValue(), Color.GREEN);
        } else {
            RenderUtils.drawFilledCircle(x + 205 + 5 + 5, y + 5 + 5 + 5, 5, Color.GREEN);
            RenderUtils.drawFilledCircle(x + 205 + 5 + 5, y + 5 + 5 + 5, animation.getValue(), Color.RED);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int x, int y, int mouseButton) {
        if (MathUtils.isMouseHovered2(x + 205 + 5, y + 5 + 5 + 5, x + 205 + 5 + 10, y + 5 + 20, mouseX, mouseY)) {
            ((BooleanSetting)setting).setValue(!((BooleanSetting)setting).getValue());
            animation.reset();
        }
    }

    @Override
    public int getHeight() {
        return 25;
    }
}
