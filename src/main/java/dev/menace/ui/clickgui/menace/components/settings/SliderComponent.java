package dev.menace.ui.clickgui.menace.components.settings;

import dev.menace.module.settings.NumberSetting;
import dev.menace.utils.math.MathUtils;
import dev.menace.utils.render.RenderUtils;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.text.DecimalFormat;

public class SliderComponent extends SettingComponent {

    private boolean drag;

    public SliderComponent(NumberSetting s) {
        super(s);
    }

    @Override
    public void draw(int mouseX, int mouseY, int x, int y) {
        if(!Mouse.isButtonDown(0)) drag = false;

        RenderUtils.drawRoundedRect(x + 205, y + 5, x + 205 + 100, y + 10 + 20, 5, new Color(32, 31, 35).getRGB());
        fontRenderer.drawCenteredString(setting.getName() + ": " + new DecimalFormat("#.##").format(((NumberSetting)setting).getValue()), x + 255, y + 12, Color.WHITE.getRGB());
        RenderUtils.drawRect(x + 205 + 5, y + 12 + 9, x + 205 + 5 + 90, y + 12 + 11, Color.GRAY.getRGB());

        //Draw slider
        float sliderPercent = (float) ((((NumberSetting)setting).getValue() - ((NumberSetting)setting).getMin()) / (((NumberSetting)setting).getMax() - ((NumberSetting)setting).getMin()));
        RenderUtils.drawRect(x + 205 + 5, y + 12 + 9, x + 205 + 5 + (int) (90 * sliderPercent), y + 12 + 11, Color.WHITE.getRGB());
        RenderUtils.drawRect(x + 205 + 5 + (int) (90 * sliderPercent), y + 12 + 7, x + 205 + 5 + (int) (90 * sliderPercent) + 2, y + 12 + 13, Color.WHITE.getRGB());

        double min = ((NumberSetting)setting).getMin();
        double max = ((NumberSetting)setting).getMax();
        double diff = Math.min(90, Math.max(0, mouseX - (x + 205 + 7)));
        if (drag) {
            if (diff == 0)
                ((NumberSetting)setting).setValue(((NumberSetting)setting).getMin());
            else {
                double newValue = MathUtils.roundToPlace((diff / 90) * (max - min) + min, 2);
                if (newValue <= ((NumberSetting)setting).getMax())
                    this.setValue(newValue);
            }
        }

    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int x, int y, int mouseButton) {
        drag = MathUtils.isMouseHovered2(x + 205 + 5, y + 12 + 7, x + 205 + 5 + 90, y + 12 + 13, mouseX, mouseY);
    }

    private void setValue(double value) {
        final NumberSetting set = (NumberSetting) setting;
        set.setValue(MathHelper.clamp_double(MathUtils.snapToStep(value, set.getIncrement()), set.getMin(), set.getMax()));
    }

    @Override
    public int getHeight() {
        return 30;
    }
}
