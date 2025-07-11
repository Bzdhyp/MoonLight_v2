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
package wtf.moonlight.module.impl.visual;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import com.cubk.EventTarget;
import wtf.moonlight.events.misc.TickEvent;
import wtf.moonlight.events.render.Render2DEvent;
import wtf.moonlight.module.Module;
import wtf.moonlight.module.Categor;
import wtf.moonlight.module.ModuleInfo;
import wtf.moonlight.module.values.impl.BoolValue;
import wtf.moonlight.module.values.impl.MultiBoolValue;
import wtf.moonlight.module.values.impl.SliderValue;
import wtf.moonlight.util.render.ColorUtil;
import wtf.moonlight.util.render.GLUtil;
import wtf.moonlight.util.render.RenderUtil;

import java.util.Arrays;

import static org.lwjgl.opengl.GL11.*;

@ModuleInfo(name = "Camera", category = Categor.Visual)
public class Camera extends Module {
    public final MultiBoolValue setting = new MultiBoolValue("Option", Arrays.asList(
            new BoolValue("View Clip", true),
            new BoolValue("Third Person Distance", false),
            new BoolValue("No Hurt Cam", false),
            new BoolValue("FPS Hurt Cam", false),
            new BoolValue("No Fire", false),
            new BoolValue("Shader Sky", false),
            new BoolValue("Bright Players", false),
            new BoolValue("Motion Camera",false),
            new BoolValue("Minimal Bobbing",true)
    ), this);

    public final SliderValue cameraDistance = new SliderValue("Distance", 4.0f, 1.0f, 8.0f, 1.0f, this, () -> setting.isEnabled("Third Person Distance"));
    public final SliderValue interpolation = new SliderValue("Motion Interpolation", 0.15f, 0.05f, 0.5f, 0.05f,this, () -> setting.isEnabled("Motion Camera"));

    @EventTarget
    public void onRender2D(Render2DEvent event) {
        if (setting.isEnabled("FPS Hurt Cam")) {
            final float hurtTimePercentage = (this.mc.thePlayer.hurtTime - event.partialTicks()) / this.mc.thePlayer.maxHurtTime;

            if (hurtTimePercentage > 0.0) {
                glDisable(GL_TEXTURE_2D);
                GLUtil.startBlend();
                glShadeModel(GL_SMOOTH);
                glDisable(GL_ALPHA_TEST);

                final ScaledResolution scaledResolution = event.scaledResolution();

                final float lineWidth = 20.f;

                glLineWidth(lineWidth);

                final int width = scaledResolution.getScaledWidth();
                final int height = scaledResolution.getScaledHeight();

                final int fadeOutColour = ColorUtil.fadeTo(0x00FF0000, 0xFFFF0000, hurtTimePercentage);

                glBegin(GL_QUADS);
                {
                    // Left
                    RenderUtil.color(fadeOutColour);
                    glVertex2f(0, 0);
                    glVertex2f(0, height);
                    RenderUtil.color(0x00FF0000);
                    glVertex2f(lineWidth, height - lineWidth);
                    glVertex2f(lineWidth, lineWidth);

                    // Right
                    RenderUtil.color(0x00FF0000);
                    glVertex2f(width - lineWidth, lineWidth);
                    glVertex2f(width - lineWidth, height - lineWidth);
                    RenderUtil.color(fadeOutColour);
                    glVertex2f(width, height);
                    glVertex2f(width, 0);

                    // Top
                    RenderUtil.color(fadeOutColour);
                    glVertex2f(0, 0);
                    RenderUtil.color(0x00FF0000);
                    glVertex2d(lineWidth, lineWidth);
                    glVertex2f(width - lineWidth, lineWidth);
                    RenderUtil.color(fadeOutColour);
                    glVertex2f(width, 0);

                    // Bottom
                    RenderUtil.color(0x00FF0000);
                    glVertex2f(lineWidth, height - lineWidth);
                    RenderUtil.color(fadeOutColour);
                    glVertex2d(0, height);
                    glVertex2f(width, height);
                    RenderUtil.color(0x00FF0000);
                    glVertex2f(width - lineWidth, height - lineWidth);
                }
                glEnd();

                glEnable(GL_ALPHA_TEST);
                glShadeModel(GL_FLAT);
                GLUtil.endBlend();
                glEnable(GL_TEXTURE_2D);
            }
        }
    }

    public boolean canMinimalBobbing(){
        return this.isEnabled() && setting.isEnabled("Minimal Bobbing");
    }
}
