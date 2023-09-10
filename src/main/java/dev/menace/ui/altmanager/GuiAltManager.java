package dev.menace.ui.altmanager;

import dev.menace.utils.session.Alt;
import dev.menace.utils.session.AltHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

import java.io.IOException;

public class GuiAltManager extends GuiScreen {

    private GuiButton btnEditAccount;
    private GuiButton btnSelectAccount;
    private GuiButton btnDeleteAccount;
    private final GuiScreen previousScreen;
    private GuiAltList list;

    public GuiAltManager(GuiScreen previousScreen) {
        this.previousScreen = previousScreen;
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        this.list = new GuiAltList(this, mc, width, height, 32, height - 64, 36);
        this.createButtons();
        this.btnSelectAccount.enabled = false;
        this.btnEditAccount.enabled = false;
        this.btnDeleteAccount.enabled = false;
        super.initGui();
    }

    public void createButtons() {
        this.buttonList.add(this.btnEditAccount = new GuiButton(7, this.width / 2 - 154, this.height - 28, 70, 20, "Edit"));
        this.buttonList.add(this.btnDeleteAccount = new GuiButton(2, this.width / 2 - 74, this.height - 28, 70, 20, "Delete"));
        this.buttonList.add(this.btnSelectAccount = new GuiButton(1, this.width / 2 - 154, this.height - 52, 100, 20, "Login"));
        this.buttonList.add(new GuiButton(4, this.width / 2 - 50, this.height - 52, 100, 20, "Direct Login"));
        this.buttonList.add(new GuiButton(3, this.width / 2 + 4 + 50, this.height - 52, 100, 20, "Add Account"));
        this.buttonList.add(new GuiButton(8, this.width / 2 + 4, this.height - 28, 70, 20, "Refresh"));
        this.buttonList.add(new GuiButton(0, this.width / 2 + 4 + 76, this.height - 28, 75, 20, I18n.format("gui.cancel")));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.list.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.fontRendererObj, "Alt Manager", this.width / 2, 20, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.enabled) {
            if (button.id == 1) {
                //Login from selected
                this.list.getSelectedAlt().login();
            } else if (button.id == 4) {
                mc.displayGuiScreen(new GuiDirectLogin(this));
            } else if (button.id == 3) {
                //Add account
                mc.displayGuiScreen(new GuiAddAccount(this));
            } else if (button.id == 2) {
                //Delete account
                AltHandler.removeAlt(this.list.getSelectedAlt());
                this.list.refreshList();
            } else if (button.id == 7) {
                //Edit account
            } else if (button.id == 8) {
                //Refresh account list
                this.list.refreshList();
            } else if (button.id == 0) {
                mc.displayGuiScreen(previousScreen);
            }
        }
        super.actionPerformed(button);
    }

    public void selectAlt(int index) {
        this.list.setSelectedSlotIndex(index);
        this.btnSelectAccount.enabled = index >= 0;
        this.btnEditAccount.enabled = index >= 0;
        this.btnDeleteAccount.enabled = index >= 0;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.list.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        this.list.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.list.handleMouseInput();
    }

    public void refreshAccountList() {
        this.list.refreshList();
    }
}
