package dev.menace.ui.custom;

import dev.menace.Menace;
import dev.menace.ui.altmanager.GuiAltManager;
import dev.menace.utils.math.MathUtils;
import dev.menace.utils.render.GLSLShader;
import dev.menace.utils.render.RenderUtils;
import dev.menace.utils.render.font.Fonts;
import dev.menace.utils.render.font.MenaceFontRenderer;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.demo.DemoWorldServer;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GLContext;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.Calendar;
import java.util.Date;

public class MenaceMainMenu extends GuiScreen implements GuiYesNoCallback {
    private static final Logger logger = LogManager.getLogger();
    private String openGLWarningLink;
    private final GLSLShader backgroundShader;
    private long initTime = System.currentTimeMillis();
    MenaceFontRenderer text;
    MenaceFontRenderer largeText;

    public MenaceMainMenu() {
        text = new MenaceFontRenderer(Fonts.fontFromTTF(new ResourceLocation("menace/fonts/SF-Pro.ttf"), 30, Font.PLAIN), true);
        largeText = new MenaceFontRenderer(Fonts.fontFromTTF(new ResourceLocation("menace/fonts/SF-Pro.ttf"), 110, Font.PLAIN), true);

        if (!GLContext.getCapabilities().OpenGL20 && !OpenGlHelper.areShadersSupported()) {
            this.openGLWarningLink = "https://help.mojang.com/customer/portal/articles/325948?ref=game";
        }

        try {
            this.backgroundShader = new GLSLShader("/assets/minecraft/menace/shaders/redflow.fsh");
        } catch (IOException var13) {
            throw new IllegalStateException("Failed to load backgound shader", var13);
        }
    }

    public void updateScreen() {
        Menace.instance.discordRP.update("In a menu.");
    }

    public boolean doesGuiPauseGame() {
        return false;
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {
    }

    public void initGui() {
        DynamicTexture viewportTexture = new DynamicTexture(256, 256);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        this.initTime = System.currentTimeMillis();

    }

    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 11) {
            this.mc.launchIntegratedServer("Demo_World", "Demo_World", DemoWorldServer.demoWorldSettings);
        }

        if (button.id == 12) {
            ISaveFormat isaveformat = this.mc.getSaveLoader();
            WorldInfo worldinfo = isaveformat.getWorldInfo("Demo_World");
            if (worldinfo != null) {
                GuiYesNo guiyesno = GuiSelectWorld.makeDeleteWorldYesNo(this, worldinfo.getWorldName(), 12);
                this.mc.displayGuiScreen(guiyesno);
            }
        }

        /*if (button.id == 69) {
            this.mc.displayGuiScreen(new GuiProtocolSelector(this));
        }*/

    }

    public void confirmClicked(boolean result, int id) {
        if (result && id == 12) {
            ISaveFormat isaveformat = this.mc.getSaveLoader();
            isaveformat.flushCache();
            isaveformat.deleteWorldDirectory("Demo_World");
            this.mc.displayGuiScreen(this);
        } else if (id == 13) {
            if (result) {
                try {
                    Class<?> oclass = Class.forName("java.awt.Desktop");
                    Object object = oclass.getMethod("getDesktop").invoke(null);
                    oclass.getMethod("browse", URI.class).invoke(object, new URI(this.openGLWarningLink));
                } catch (Throwable var5) {
                    logger.error("Couldn't open link", var5);
                }
            }

            this.mc.displayGuiScreen(this);
        }

    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.enableAlpha();
        GlStateManager.disableCull();
        ScaledResolution sr = new ScaledResolution(this.mc);
        this.backgroundShader.useShader(sr.getScaledWidth() * sr.getScaleFactor(), sr.getScaledHeight() * sr.getScaleFactor(), (float)mouseX, (float)mouseY, (float)(System.currentTimeMillis() - this.initTime) / 1000.0F);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(-1.0F, -1.0F);
        GL11.glVertex2f(-1.0F, 1.0F);
        GL11.glVertex2f(1.0F, 1.0F);
        GL11.glVertex2f(1.0F, -1.0F);
        GL11.glEnd();
        GL20.glUseProgram(0);

        this.largeText.drawCenteredString("Menace", (float)(width / 2), ((float) height / 4 - 24), -1);

        this.drawExit(mouseX, mouseY);
        this.drawOptions(mouseX, mouseY);
        this.drawAltmanager(mouseX, mouseY);
        this.drawSinglePlayer(mouseX, mouseY);
        this.drawMultiPlayer(mouseX, mouseY);
        this.text.drawString("Welcome back, " + Menace.instance.user.getUsername() + " [" + Menace.instance.user.getUID() + "]", 5, 5, Color.white.getRGB());
        this.text.drawString("Discord: " + Menace.instance.user.getDiscord(), 5, 25, Color.white.getRGB());
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawExit(int mouseX, int mouseY) {
        int l1 = height / 4 + 48;
        int w = 98;
        int h = 20;
        int x1 = width / 2 + 2;
        int x2 = x1 + 2 + w;
        int y1 = l1 + 80 + 12;
        int y2 = y1 + h;
        boolean hovered = MathUtils.isMouseHovered(x1, y1, w, h, mouseX, mouseY);
        RenderUtils.drawRect((float)x1, (float)y1, (float)x2, (float)y2, hovered ? (new Color(203, 26, 26, 255)).getRGB() : Color.black.getRGB());
        RenderUtils.drawRect((float)x1, (float)y1 + 17, (float)x2, (float)y2, (new Color(200, 0, 0)).getRGB());
        this.text.drawCenteredString("Exit", (float)((x1 + x2) / 2), (float)((y1 + y2 - 3) / 2), Color.white.getRGB());
    }

    private void drawOptions(int mouseX, int mouseY) {
        int l1 = height / 4 + 48;
        int w = 98;
        int h = 20;
        int x1 = width / 2 - 100;
        int x2 = x1 + w;
        int y1 = l1 + 80 + 12;
        int y2 = y1 + h;
        boolean hovered = MathUtils.isMouseHovered(x1, y1, w, h, mouseX, mouseY);
        RenderUtils.drawRect((float)x1, (float)y1, (float)x2, (float)y2, hovered ? (new Color(203, 26, 26, 255)).getRGB() : Color.black.getRGB());
        RenderUtils.drawRect((float)x1, (float)y1 + 17, (float)x2, (float)y2, (new Color(200, 0, 0)).getRGB());
        this.text.drawCenteredString("Settings", (float)((x1 + x2) / 2), (float)((y1 + y2 - 3) / 2), Color.white.getRGB());
    }

    private void drawAltmanager(int mouseX, int mouseY) {
        int l1 = height / 4 + 48;
        int w = 200;
        int h = 20;
        int x1 = width / 2 - 100;
        int x2 = x1 + w;
        int y1 = l1 + 48;
        int y2 = y1 + h;
        boolean hovered = MathUtils.isMouseHovered(x1, y1, w, h, mouseX, mouseY);
        RenderUtils.drawRect((float)x1, (float)y1, (float)x2, (float)y2, hovered ? (new Color(203, 26, 26, 255)).getRGB() : Color.black.getRGB());
        RenderUtils.drawRect((float)x1, (float)y1 + 17, (float)x2, (float)y2, (new Color(200, 0, 0)).getRGB());
        this.text.drawCenteredString("Login", (float)((x1 + x2) / 2), (float)((y1 + y2 - 3) / 2), Color.white.getRGB());
    }

    public void drawSinglePlayer(int mouseX, int mouseY) {
        int l1 = height / 4 + 48;
        int w = 200;
        int h = 20;
        int x1 = width / 2 - 100;
        int x2 = x1 + w;
        int y2 = l1 + h;
        boolean hovered = MathUtils.isMouseHovered(x1, l1, w, h, mouseX, mouseY);
        RenderUtils.drawRect((float)x1, (float)l1, (float)x2, (float)y2, hovered ? (new Color(203, 26, 26, 255)).getRGB() : Color.black.getRGB());
        RenderUtils.drawRect((float)x1, (float)l1 + 17, (float)x2, (float)y2, (new Color(200, 0, 0)).getRGB());
        this.text.drawCenteredString("Singleplayer", (float)((x1 + x2) / 2), (float)((l1 + y2 - 3) / 2), Color.white.getRGB());
    }

    public void drawMultiPlayer(int mouseX, int mouseY) {
        int l1 = height / 4 + 48;
        int w = 200;
        int h = 20;
        int x1 = width / 2 - 100;
        int x2 = x1 + w;
        int y1 = l1 + 24;
        int y2 = y1 + h;
        boolean hovered = MathUtils.isMouseHovered(x1, y1, w, h, mouseX, mouseY);
        RenderUtils.drawRect((float)x1, (float)y1, (float)x2, (float)y2, hovered ? (new Color(203, 26, 26, 255)).getRGB() : Color.black.getRGB());
        RenderUtils.drawRect((float)x1, (float)y1 + 17, (float)x2, (float)y2, (new Color(200, 0, 0)).getRGB());
        this.text.drawCenteredString("Multiplayer", (float)((x1 + x2) / 2), (float)((y1 + y2 - 3) / 2), Color.white.getRGB());
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        int l1 = height / 4 + 48;
        if (MathUtils.isMouseHovered(width / 2 + 2, l1 + 80 + 12, 98, 20, mouseX, mouseY) && mouseButton == 0) {
            this.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
            this.mc.shutdown();
        }

        if (MathUtils.isMouseHovered(width / 2 + 2 - 100, l1 + 80 + 12, 98, 20, mouseX, mouseY) && mouseButton == 0) {
            this.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
            this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
        }

        if (MathUtils.isMouseHovered(width / 2 - 100, l1 + 48, 200, 20, mouseX, mouseY) && mouseButton == 0) {
            this.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
            this.mc.displayGuiScreen(new GuiAltManager(this));
        }

        if (MathUtils.isMouseHovered(width / 2 - 100, l1, 200, 20, mouseX, mouseY) && mouseButton == 0) {
            this.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
            this.mc.displayGuiScreen(new GuiSelectWorld(this));
        }

        if (MathUtils.isMouseHovered(width / 2 - 100, l1 + 24, 200, 20, mouseX, mouseY) && mouseButton == 0) {
            this.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
            this.mc.displayGuiScreen(new GuiMultiplayer(this));
        }

    }
}