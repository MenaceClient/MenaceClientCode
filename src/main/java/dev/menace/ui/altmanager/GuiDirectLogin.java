package dev.menace.ui.altmanager;

import dev.menace.utils.math.MathUtils;
import dev.menace.utils.render.GuiPasswordField;
import dev.menace.utils.session.Alt;
import dev.menace.utils.session.LoginManager;
import dev.menace.utils.session.LoginMode;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

public final class GuiDirectLogin
        extends GuiScreen {
    private GuiPasswordField password;
    private final GuiScreen previousScreen;
    private GuiButton loginModeButton;
    private GuiTextField username;
    private LoginMode loginModeName;
    private String status = EnumChatFormatting.GRAY + "Idle...";


    public GuiDirectLogin(GuiScreen previousScreen) {
        this.previousScreen = previousScreen;
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 2: {
                this.mc.displayGuiScreen(this.previousScreen);
                break;
            }

            case 1: {
                switch (loginModeName) {
                    case MICROSOFT:
                        loginModeName = LoginMode.MICROSOFT_BROWSER;
                        loginModeButton.displayString = "Mode: " + loginModeName.getName();
                        break;
                    case MICROSOFT_BROWSER:
                        loginModeName = LoginMode.RNG;
                        loginModeButton.displayString = "Mode: " + loginModeName.getName();
                        break;
                    case RNG:
                        loginModeName = LoginMode.MICROSOFT;
                        loginModeButton.displayString = "Mode: " + loginModeName.getName();
                        break;
                    default:
                        break;
                }
                break;
            }

            case 0: {
                String name = "";

                if (loginModeName == LoginMode.RNG) {
                    String[] firstNames = new String[]{"Time", "Past", "Future", "Dev", "Fly", "Flying", "Soar", "Soaring", "Power", "Falling", "Fall", "Jump", "Cliff", "Mountain", "Rend", "Red", "Blue", "Green", "Yellow", "Gold", "Demon", "Demonic", "Panda", "Cat", "Kitty", "Kitten", "Zero", "Memory", "Trooper", "XX", "Bandit", "Fear", "Light", "Glow", "Tread", "Deep", "Deeper", "Deepest", "Mine", "Your", "Worst", "Enemy", "Hostile", "Force", "Video", "Game", "Donkey", "Mule", "Colt", "Cult", "Cultist", "Magnum", "Gun", "Assault", "Recon", "Trap", "Trapper", "Redeem", "Code", "Script", "Writer", "Near", "Close", "Open", "Cube", "Circle", "Geo", "Genome", "Germ", "Spaz", "Sped", "Skid", "Shot", "Echo", "Beta", "Alpha", "Gamma", "Omega", "Seal", "Squid", "Money", "Cash", "Lord", "King", "Ominous", "Flow", "Skull", "Submissive"};
                    String[] lastNames = new String[]{"Duke","Rest","Fire","Flame","Morrow","Break","Breaker","Numb","Ice","Cold","Rotten","Sick","Sickly","Janitor","Camel","Rooster","Sand","Desert","Dessert","Hurdle","Racer","Eraser","Erase","Big","Small","Short","Tall","Sith","Bounty","Hunter","Cracked","Broken","Sad","Happy","Joy","Joyful","Crimson","Destiny","Deceit","Lies","Lie","Honest","Destined","Bloxxer","Hawk","Eagle","Hawker","Walker","Zombie","Sarge","Capt","Captain","Punch","One","Two","Uno","Slice","Slash","Melt","Melted","Melting","Fell","Wolf","Hound","Legacy","Sharp","Dead","Mew","Chuckle","Bubba","Bubble","Sandwich","Smasher","Extreme","Multi","Universe","Ultimate","Death","Ready","Monkey","Elevator","Wrench","Grease","Head","Theme","Grand","Cool","Kid","Boy","Girl","Vortex","Paradox","Omen", "Bussy"};

                    name = firstNames[MathUtils.randInt(0, firstNames.length)] + lastNames[MathUtils.randInt(0, lastNames.length)] + MathUtils.randInt(0, 9999);

                    name = name.length() > 16 ? name.substring(0, 16) : name;

                    username.setText(name);
                }

                final String finalName = name;

                new Thread(() -> {
                    try {
                        status = EnumChatFormatting.YELLOW + "Logging in...";
                        if (loginModeName == LoginMode.MICROSOFT && password.getText().isEmpty()) {
                            LoginManager.crackedLogin(username.getText());
                            return;
                        }

                        switch (loginModeName) {
                            case RNG:
                                LoginManager.crackedLogin(finalName);
                                break;
                            case MICROSOFT:
                                LoginManager.microsoftEmailLogin(username.getText(), password.getText());
                                break;
                            case MICROSOFT_BROWSER:
                                LoginManager.microsoftBrowserLogin();
                                break;
                        }
                        status = EnumChatFormatting.GREEN + "Logged in as " + mc.session.getUsername() + "!";
                    } catch (MicrosoftAuthenticationException | NoSuchFieldException | IllegalAccessException e) {
                        status = EnumChatFormatting.RED + "Login failed!";
                        e.printStackTrace();
                    }
                }).start();
                break;
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.username.drawTextBox();
        this.password.drawTextBox();
        this.loginModeButton.drawButton(mc, mouseX, mouseY);
        this.drawCenteredString(this.mc.fontRendererObj, "Alt Login", width / 2, 20, -1);
        this.drawCenteredString(this.mc.fontRendererObj, status, width / 2, 29, -1);
        if (this.username.getText().isEmpty()) {
            this.drawString(this.mc.fontRendererObj, "Username / E-Mail", width / 2 - 96, 66, -7829368);
        }
        if (this.password.getText().isEmpty()) {
            this.drawString(this.mc.fontRendererObj, "Password", width / 2 - 96, 106, -7829368);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void initGui() {
        this.loginModeName = LoginMode.MICROSOFT;
        int var3 = height / 4 + 24;
        this.loginModeButton = new GuiButton(1, width / 2 - 100, var3 + 72 + 12, 100, 20, "Mode: " + loginModeName.getName());
        this.buttonList.add(new GuiButton(2, width / 2 - 100, var3 + 72 + 12 + 24, "Back"));
        this.buttonList.add(new GuiButton(0, width / 2, var3 + 72 + 12, 100, 20, "Login"));
        this.username = new GuiTextField(var3, this.mc.fontRendererObj, width / 2 - 100, 60, 200, 20);
        this.username.setMaxStringLength(50);
        this.password = new GuiPasswordField(this.mc.fontRendererObj, width / 2 - 100, 100, 200, 20);
        this.password.setMaxStringLength(50);
        this.username.setFocused(true);
        Keyboard.enableRepeatEvents(true);
    }

    @Override
    protected void keyTyped(char character, int key) {
        if (character == '\t') {
            if (!this.username.isFocused() && !this.password.isFocused()) {
                this.username.setFocused(true);
            } else {
                this.username.setFocused(this.password.isFocused());
                this.password.setFocused(!this.username.isFocused());
            }
        }
        if (character == '\r') {
            this.actionPerformed(this.buttonList.get(0));
        }
        this.username.textboxKeyTyped(character, key);
        this.password.textboxKeyTyped(character, key);
    }

    @Override
    protected void mouseClicked(int x2, int y2, int button) {
        try {
            super.mouseClicked(x2, y2, button);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        this.username.mouseClicked(x2, y2, button);
        this.password.mouseClicked(x2, y2, button);

        if (button == 0)
        {

            if (this.loginModeButton.mousePressed(this.mc, x2, y2))
            {
                this.selectedButton = loginModeButton;
                loginModeButton.playPressSound(this.mc.getSoundHandler());
                this.actionPerformed(loginModeButton);
            }
        }

    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void updateScreen() {
        this.username.updateCursorCounter();
        this.password.updateCursorCounter();
    }
}