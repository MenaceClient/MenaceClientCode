package dev.menace.ui.hud.elements;

import dev.menace.Menace;
import dev.menace.ui.hud.Element;
import dev.menace.ui.hud.options.HUDOption;
import dev.menace.utils.misc.ChatUtils;
import dev.menace.utils.misc.MathUtils;
import dev.menace.utils.render.RenderUtils;

import java.awt.*;
import java.util.Optional;

public class HUDSettingsElement extends Element {

    private Optional<Element> selectedElement = Optional.empty();

    public HUDSettingsElement() {
        super("HUD Settings");
    }

    @Override
    public void setup() {
    }

    @Override
    public void render() {
        //ignore
    }

    @Override
    public void renderDummy() {
        RenderUtils.drawRect(this.getPosX(), this.getPosY(), this.getPosX() + getWidth(), this.getPosY() + getHeight(), Color.BLACK.getRGB());

        if (selectedElement.isPresent() && !(selectedElement.get() instanceof HUDSettingsElement)) {
            //Element Settings
            this.drawString(selectedElement.get().getElementName(), this.getPosX() + 2, this.getPosY() + 2, -1, true);

            int yOff = this.getFontHeight(true) + 4;
            for (HUDOption option : selectedElement.get().getOptions()) {
                option.render(this.getPosX() + 2, this.getPosY() + yOff);

                yOff += option.getHeight() + 3;
            }
        } else {
            //Element Creator
            this.drawString("Create Element", this.getPosX() + 2, this.getPosY() + 2, -1, true);

            int yOff = this.getFontHeight(true) + 4;
            for (Class<? extends Element> element : Menace.instance.hudManager.getElementList()) {
                String elementName;
                //Find the element name by making an instance of the element and getting the name
                try {
                    elementName = element.newInstance().getElementName();
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                    continue;
                }

                this.drawString(elementName, this.getPosX() + 2, this.getPosY() + yOff, -1, true);

                yOff += this.getFontHeight(true) + 2;
            }
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (selectedElement.isPresent() && !(selectedElement.get() instanceof HUDSettingsElement)) {
            int yOff = this.getFontHeight(true) + 4;
            for (HUDOption option : selectedElement.get().getOptions()) {
                option.mouseClicked(this.getPosX(), this.getPosY() + yOff, mouseX, mouseY, mouseButton);

                yOff += option.getHeight() + 3;
            }
        } else {
            //Element Creator

            int yOff = this.getFontHeight(true) + 4;
            for (Class<? extends Element> element : Menace.instance.hudManager.getElementList()) {
                if (MathUtils.isMouseHovered(this.getPosX() + 2, this.getPosY() + yOff, getWidth(), 10, mouseX, mouseY)) {
                    try {
                        Element elementInstance = element.newInstance();
                        ChatUtils.message("Added " + elementInstance.getElementName() + " to the HUD");
                        Menace.instance.hudManager.registerElement(elementInstance);
                    } catch (InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }

                yOff += this.getFontHeight(true) + 2;
            }
        }
    }

    public void setSelectedElement(Optional<Element> element) {
        this.selectedElement = element;
    }

    @Override
    public int getWidth() {
        return 80;
    }

    @Override
    public int getHeight() {
        return 200;
    }
}
