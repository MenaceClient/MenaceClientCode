package net.minecraft.client.gui;

import java.io.IOException;
import java.time.LocalTime;
import java.util.List;

import dev.menace.Menace;
import dev.menace.ui.altmanager.GuiAltManager;
import dev.menace.ui.hud.elements.GameStatsElement;
import dev.menace.utils.misc.ServerUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.IChatComponent;

public class GuiDisconnected extends GuiScreen
{
    private String reason;
    private IChatComponent message;
    private List<String> multilineMessage;
    private final GuiScreen parentScreen;
    private int field_175353_i;

    private final long time;

    public GuiDisconnected(GuiScreen screen, String reasonLocalizationKey, IChatComponent chatComp)
    {
        this.parentScreen = screen;
        this.reason = I18n.format(reasonLocalizationKey, new Object[0]);
        this.message = chatComp;
        time = System.currentTimeMillis();
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
    }

    public void initGui()
    {
        this.buttonList.clear();
        this.multilineMessage = this.fontRendererObj.listFormattedStringToWidth(this.message.getFormattedText(), this.width - 50);
        this.field_175353_i = this.multilineMessage.size() * this.fontRendererObj.FONT_HEIGHT;
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 2 + this.field_175353_i / 2 + this.fontRendererObj.FONT_HEIGHT, I18n.format("gui.toMenu", new Object[0])));
        this.buttonList.add(new GuiButton(69, width / 2 - 100, height / 2 + this.field_175353_i / 2 + this.fontRendererObj.FONT_HEIGHT + 25, "Reconnect"));
        this.buttonList.add(new GuiButton(420, width / 2 - 100, height / 2 + this.field_175353_i / 2 + this.fontRendererObj.FONT_HEIGHT + 50, "AltManager"));
    }

    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.id == 0)
        {
            this.mc.displayGuiScreen(this.parentScreen);
        } else if (button.id == 69) {
        Menace.instance.hudManager.getElements().stream().filter(element -> element instanceof GameStatsElement).forEach(element -> {
            ((GameStatsElement) element).reset();
        });
        ServerUtils.connectToLastServer(this.parentScreen);
        } else if (button.id == 420) {
            mc.displayGuiScreen(new GuiAltManager(this.parentScreen));
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, this.reason, this.width / 2, this.height / 2 - this.field_175353_i / 2 - this.fontRendererObj.FONT_HEIGHT * 2, 11184810);
        int i = this.height / 2 - this.field_175353_i / 2;

        if (this.multilineMessage != null)
        {
            for (String s : this.multilineMessage)
            {
                this.drawCenteredString(this.fontRendererObj, s, this.width / 2, i, 16777215);
                i += this.fontRendererObj.FONT_HEIGHT;
            }
        }

        //GameStats
        this.drawCenteredString(this.fontRendererObj, "Username: " + mc.session.getUsername(), width / 2, height / 2 + this.field_175353_i / 2 + this.fontRendererObj.FONT_HEIGHT + 75, -1);
        if (Menace.instance.hudManager.getElements().stream().anyMatch(element -> element instanceof GameStatsElement) &&
                ((GameStatsElement)Menace.instance.hudManager.getElements().stream().filter(element -> element instanceof GameStatsElement).findFirst().get()).timer.getStartTime() != -1) {
            LocalTime lt = LocalTime.ofSecondOfDay((time - ((GameStatsElement)Menace.instance.hudManager.getElements().stream().filter(element -> element instanceof GameStatsElement).findFirst().get()).timer.getStartTime()) / 1000);
            String second = lt.getSecond() < 10 ? "0" + lt.getSecond() : String.valueOf(lt.getSecond());
            String hour = lt.getHour() != 0 ? lt.getHour() + ":" : "";
            this.drawCenteredString(this.fontRendererObj, "Play time: " + hour + lt.getMinute() + ":" + second, width / 2, height / 2 + this.field_175353_i / 2 + this.fontRendererObj.FONT_HEIGHT + 85, -1);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
