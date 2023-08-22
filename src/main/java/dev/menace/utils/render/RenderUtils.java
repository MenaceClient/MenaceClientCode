package dev.menace.utils.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

public class RenderUtils {

    public static void drawRect(double left, double top, double right, double bottom, int color) {
        Gui.drawRect((int) left, (int) top, (int) right, (int) bottom, color);
    }
    public static void drawRect(int left, int top, int right, int bottom, int color) {
        Gui.drawRect(left, top, right, bottom, color);
    }

    public static void drawHollowRect(int left, int top, int right, int bottom, int width, int color) {
        //Draws a hollow rectangle with a specified width.
        drawRect(left, top, right, top + width, color);
        drawRect(left, top, left + width, bottom, color);
        drawRect(right - width, top, right, bottom, color);
        drawRect(left, bottom - width, right, bottom, color);
    }

    public static void drawHorizontalLine(int startX, int endX, int y, int lineWidth, int color) {
        if (endX < startX) {
            int i = startX;
            startX = endX;
            endX = i;
        }

        drawRect(startX, y, endX + lineWidth, y + lineWidth, color);
    }

    public static void drawVerticalLine(int x, int startY, int endY, int lineWidth, int color) {
        if (endY < startY) {
            int i = startY;
            startY = endY;
            endY = i;
        }

        drawRect(x, startY + lineWidth, x + lineWidth, endY, color);
    }

    public static void drawRoundedRect(float startX, float startY, float endX, float endY, float radius, int color) {
        float alpha = (color >> 24 & 0xFF) / 255.0F;
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;

        float z = 0;
        if (startX > endX) {
            z = startX;
            startX = endX;
            endX = z;
        }

        if (startY > endY) {
            z = startY;
            startY = endY;
            endY = z;
        }

        double x1 = startX + radius;
        double y1 = startY + radius;
        double x2 = endX - radius;
        double y2 = endY - radius;

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_LINE_SMOOTH);
        glLineWidth(1);

        GlStateManager.color(red, green, blue, alpha);
        GlStateManager.glBegin(GL_POLYGON);

        double degree = Math.PI / 180;
        for (double i = 0; i <= 90; i += 1)
            glVertex2d(x2 + Math.sin(i * degree) * radius, y2 + Math.cos(i * degree) * radius);
        for (double i = 90; i <= 180; i += 1)
            glVertex2d(x2 + Math.sin(i * degree) * radius, y1 + Math.cos(i * degree) * radius);
        for (double i = 180; i <= 270; i += 1)
            glVertex2d(x1 + Math.sin(i * degree) * radius, y1 + Math.cos(i * degree) * radius);
        for (double i = 270; i <= 360; i += 1)
            glVertex2d(x1 + Math.sin(i * degree) * radius, y2 + Math.cos(i * degree) * radius);
        GlStateManager.glEnd();

        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        glDisable(GL_LINE_SMOOTH);
        GlStateManager.popMatrix();
    }

    // rTL = radius top left, rTR = radius top right, rBR = radius bottom right, rBL = radius bottom left
    public static void customRounded(float startX, float startY, float endX, float endY, float rTL, float rTR, float rBR, float rBL, int color) {
        float alpha = (color >> 24 & 0xFF) / 255.0F;
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;

        float z = 0;
        if (startX > endX) {
            z = startX;
            startX = endX;
            endX = z;
        }

        if (startY > endY) {
            z = startY;
            startY = endY;
            endY = z;
        }

        double xTL = startX + rTL;
        double yTL = startY + rTL;

        double xTR = endX - rTR;
        double yTR = startY + rTR;

        double xBR = endX - rBR;
        double yBR = endY - rBR;

        double xBL = startX + rBL;
        double yBL = endY - rBL;

        glPushMatrix();
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_LINE_SMOOTH);
        glLineWidth(1);

        GlStateManager.color(red, green, blue, alpha);
        glBegin(GL_POLYGON);

        double degree = Math.PI / 180;
        for (double i = 0; i <= 90; i += 1)
            glVertex2d(xBR + Math.sin(i * degree) * rBR, yBR + Math.cos(i * degree) * rBR);
        for (double i = 90; i <= 180; i += 1)
            glVertex2d(xTR + Math.sin(i * degree) * rTR, yTR + Math.cos(i * degree) * rTR);
        for (double i = 180; i <= 270; i += 1)
            glVertex2d(xTL + Math.sin(i * degree) * rTL, yTL + Math.cos(i * degree) * rTL);
        for (double i = 270; i <= 360; i += 1)
            glVertex2d(xBL + Math.sin(i * degree) * rBL, yBL + Math.cos(i * degree) * rBL);
        glEnd();

        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        glDisable(GL_LINE_SMOOTH);
        glPopMatrix();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawVerticalGradient(double left, double top, double right, double bottom, int col1, int col2) {
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_LINE_SMOOTH);
        glShadeModel(GL_SMOOTH);

        quickDrawVerticalGradient(left, top, right, bottom, col1, col2);

        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        glDisable(GL_LINE_SMOOTH);
        glShadeModel(GL_FLAT);
    }

    public static void quickDrawVerticalGradient(double left, double top, double right, double bottom, int col1, int col2) {
        glBegin(GL_QUADS);

        glColor(col1);
        glVertex2d(right, top);
        glVertex2d(left, top);
        glColor(col2);
        glVertex2d(left, bottom);
        glVertex2d(right, bottom);

        glEnd();
    }

    public static void drawHorizontalGradient(double left, double top, double right, double bottom, int col1, int col2) {
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_LINE_SMOOTH);
        glShadeModel(GL_SMOOTH);

        drawHorizontalGradient(left, top, right, bottom, col1, col2);

        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        glDisable(GL_LINE_SMOOTH);
        glShadeModel(GL_FLAT);
    }

    public static void quickDrawHorizontalGradient(double left, double top, double right, double bottom, int col1, int col2) {
        glBegin(GL_QUADS);

        glColor(col1);
        glVertex2d(left, top);
        glVertex2d(left, bottom);
        glColor(col2);
        glVertex2d(right, bottom);
        glVertex2d(right, top);

        glEnd();
    }

    public static void drawImage(float x, float y, final int width, final int height, final ResourceLocation image, Color color) {
        glPushMatrix();
        glDisable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glDepthMask(false);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GlStateManager.color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, 1.0f);
        Minecraft.getMinecraft().getTextureManager().bindTexture(image);
        Gui.drawModalRectWithCustomSizedTexture((int) x, (int) y, 0.0f, 0.0f, width, height, (float) width, (float) height);
        glDepthMask(true);
        glDisable(GL_BLEND);
        glEnable(GL_DEPTH_TEST);
        glPopMatrix();
    }

    // From LiquidBounce
    public static void drawFilledCircle(final int xx, final int yy, final float radius, final Color color) {
        int sections = 50;
        double dAngle = 2 * Math.PI / sections;
        float x, y;

        glPushAttrib(GL_ENABLE_BIT);

        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_LINE_SMOOTH);
        glBegin(GL_TRIANGLE_FAN);

        for (int i = 0; i < sections; i++) {
            x = (float) (radius * Math.sin((i * dAngle)));
            y = (float) (radius * Math.cos((i * dAngle)));

            glColor(color);
            glVertex2f(xx + x, yy + y);
        }

        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

        glEnd();

        glPopAttrib();
    }

    public static void drawHead(ResourceLocation skin, int x, int y, int width, int height) {
        glPushMatrix();
        glEnable(3042);
        glColor4f(1F, 1F, 1F, 1F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(skin);
        RenderUtils.drawScaledCustomSizeModalRect(x, y, 8F, 8F, 8, 8, width, height, 64F, 64F);
        RenderUtils.drawScaledCustomSizeModalRect(x, y, 40F, 8F, 8, 8, width, height, 64F, 64F);
        glDisable(3042);
        glPopMatrix();
    }

    public static void drawScaledCustomSizeModalRect(int x, int y, float u, float v, int uWidth, int vHeight, int width, int height, float tileWidth, float tileHeight) {
        float f = 1.0F / tileWidth;
        float f1 = 1.0F / tileHeight;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(x, y + height, 0.0D).tex(u * f, (v + (float) vHeight) * f1).endVertex();
        worldrenderer.pos(x + width, y + height, 0.0D).tex((u + (float) uWidth) * f, (v + (float) vHeight) * f1).endVertex();
        worldrenderer.pos(x + width, y, 0.0D).tex((u + (float) uWidth) * f, v * f1).endVertex();
        worldrenderer.pos(x, y, 0.0D).tex(u * f, v * f1).endVertex();
        tessellator.draw();
    }

    private static void glColor(int hex) {
        float alpha = (hex >> 24 & 0xFF) / 255.0F;
        float red = (hex >> 16 & 0xFF) / 255.0F;
        float green = (hex >> 8 & 0xFF) / 255.0F;
        float blue = (hex & 0xFF) / 255.0F;
        GlStateManager.color(red, green, blue, alpha);
    }

    private static void glColor(Color color) {
        GlStateManager.color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, color.getAlpha() / 255F);
    }


}
