/*
 * MoonLight Hacked Client
 *
 * A free and open-source hacked client for Minecraft.
 * Developed using Minecraft's resources.
 *
 * Repository: https://github.com/randomguy3725/MoonLight
 *
 * Author(s): [Randumbguy & wxdbie & opZywl & MukjepScarlet & lucas & eonian]
 */
package wtf.moonlight.util.render.shader.impl;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import wtf.moonlight.util.misc.InstanceAccess;
import wtf.moonlight.util.render.shader.ShaderUtil;

public class Sky implements InstanceAccess {
    private static final ShaderUtil sky = new ShaderUtil("mainmenu");

    public static void draw(long initTime) {
        ScaledResolution sr = new ScaledResolution(mc);
        sky.init();
        sky.setUniformf("TIME", (float) (System.currentTimeMillis() - initTime) / 1000);
        sky.setUniformf("RESOLUTION", (float) ((double) sr.getScaledWidth() * sr.getScaleFactor()), (float) ((double) sr.getScaledHeight() * sr.getScaleFactor()));
        drawHorizon();
        sky.unload();
    }

    public static void drawHorizon() {
        WorldRenderer worldrenderer = Tessellator.getInstance().getWorldRenderer();
        float f = (float) (mc.gameSettings.renderDistanceChunks * 16);
        double d0 = (double) f * 0.9238D; //25
        double d1 = (double) f * 0.3826D; //25
        double d2 = -d1; //24
        double d3 = -d0; //24
        double d4 = 256; //24
        double d5 = -256; //24
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);

        worldrenderer.pos(d2, d5, d3).endVertex();
        worldrenderer.pos(d2, d4, d3).endVertex();
        worldrenderer.pos(d3, d4, d2).endVertex();
        worldrenderer.pos(d3, d5, d2).endVertex();
        worldrenderer.pos(d3, d5, d2).endVertex();
        worldrenderer.pos(d3, d4, d2).endVertex();
        worldrenderer.pos(d3, d4, d1).endVertex();
        worldrenderer.pos(d3, d5, d1).endVertex();
        worldrenderer.pos(d3, d5, d1).endVertex();
        worldrenderer.pos(d3, d4, d1).endVertex();
        worldrenderer.pos(d2, d4, d0).endVertex();
        worldrenderer.pos(d2, d5, d0).endVertex();
        worldrenderer.pos(d2, d5, d0).endVertex();
        worldrenderer.pos(d2, d4, d0).endVertex();
        worldrenderer.pos(d1, d4, d0).endVertex();
        worldrenderer.pos(d1, d5, d0).endVertex();
        worldrenderer.pos(d1, d5, d0).endVertex();
        worldrenderer.pos(d1, d4, d0).endVertex();
        worldrenderer.pos(d0, d4, d1).endVertex();
        worldrenderer.pos(d0, d5, d1).endVertex();
        worldrenderer.pos(d0, d5, d1).endVertex();
        worldrenderer.pos(d0, d4, d1).endVertex();
        worldrenderer.pos(d0, d4, d2).endVertex();
        worldrenderer.pos(d0, d5, d2).endVertex();
        worldrenderer.pos(d0, d5, d2).endVertex();
        worldrenderer.pos(d0, d4, d2).endVertex();
        worldrenderer.pos(d1, d4, d3).endVertex();
        worldrenderer.pos(d1, d5, d3).endVertex();
        worldrenderer.pos(d1, d5, d3).endVertex();
        worldrenderer.pos(d1, d4, d3).endVertex();
        worldrenderer.pos(d2, d4, d3).endVertex();
        worldrenderer.pos(d2, d5, d3).endVertex();
        worldrenderer.pos(d3, d5, d3).endVertex();
        worldrenderer.pos(d3, d5, d0).endVertex();
        worldrenderer.pos(d0, d5, d0).endVertex();
        worldrenderer.pos(d0, d5, d3).endVertex();

        Tessellator.getInstance().draw();
    }
}

