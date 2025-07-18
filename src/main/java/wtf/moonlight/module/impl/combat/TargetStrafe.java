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
package wtf.moonlight.module.impl.combat;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;
import org.lwjglx.input.Keyboard;
import com.cubk.EventPriority;
import com.cubk.EventTarget;
import wtf.moonlight.events.player.JumpEvent;
import wtf.moonlight.events.player.MoveInputEvent;
import wtf.moonlight.events.player.StrafeEvent;
import wtf.moonlight.events.player.UpdateEvent;
import wtf.moonlight.events.render.Render3DEvent;
import wtf.moonlight.module.Module;
import wtf.moonlight.module.Categor;
import wtf.moonlight.module.ModuleInfo;
import wtf.moonlight.module.impl.movement.Speed;
import wtf.moonlight.module.impl.movement.Scaffold;
import wtf.moonlight.module.values.impl.BoolValue;
import wtf.moonlight.module.values.impl.SliderValue;
import wtf.moonlight.util.player.MovementUtil;
import wtf.moonlight.util.player.PlayerUtil;

@ModuleInfo(name = "TargetStrafe", category = Categor.Combat)
public class TargetStrafe extends Module {

    public final SliderValue range = new SliderValue("Range", 1, 0.1f, 6, 0.1f, this);
    public final BoolValue holdJump = new BoolValue("Hold Jump", false, this);
    public final BoolValue render = new BoolValue("Render", true, this);
    public final BoolValue behind = new BoolValue("Behind", true, this);
    public float yaw;
    private boolean left, colliding;
    public boolean active;
    public EntityLivingBase target;
    @EventTarget
    @EventPriority(3)
    public void onJump(JumpEvent event) {
        if (target != null && active) {
            event.setYaw(yaw);
        }
    }

    @EventTarget
    @EventPriority(3)
    public void onStrafe(StrafeEvent event) {
        if (target != null && active) {
            event.setYaw(yaw);
        }
    }

    @EventTarget
    @EventPriority(3)
    public void onUpdate(UpdateEvent event) {

        Speed speed = getModule(Speed.class);

        if (holdJump.get() && !Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode()) || !(mc.gameSettings.keyBindForward.isKeyDown() && (speed != null && speed.isEnabled())) || !isEnabled(KillAura.class) || isEnabled(Scaffold.class)) {
            active = false;
            target = null;
            return;
        }

        target = getModule(KillAura.class).target;

        if (target == null) {
            active = false;
            return;
        }

        if(!speed.couldStrafe)
            return;

        if (mc.thePlayer.isCollidedHorizontally || !PlayerUtil.isBlockUnder(5, false)) {
            if (!colliding) {
                MovementUtil.strafe();
                left = !left;
            }
            colliding = true;
        } else {
            colliding = false;
        }

        active = true;


        float yaw;

        if (behind.get()) {
            yaw = target.rotationYaw + 180;
        } else {
            yaw = getYaw(mc.thePlayer, new Vec3(target.posX, target.posY, target.posZ)) + (90 + 45) * (left ? -1 : 1);
        }

        final double range = this.range.getValue() + Math.random() / 100f;
        final double posX = -MathHelper.sin((float) Math.toRadians(yaw)) * range + target.posX;
        final double posZ = MathHelper.cos((float) Math.toRadians(yaw)) * range + target.posZ;

        yaw = getYaw(mc.thePlayer, new Vec3(posX, target.posY, posZ));

        this.yaw = yaw;
    }

    public static float getYaw(EntityPlayer from, Vec3 pos) {
        return from.rotationYaw + MathHelper.wrapAngleTo180_float((float) Math.toDegrees(Math.atan2(pos.zCoord - from.posZ, pos.xCoord - from.posX)) - 90f - from.rotationYaw);
    }

    @EventTarget
    public void onMoveInput(MoveInputEvent event){
        if(Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode()) && holdJump.get() && active)
            event.setJumping(false);
    }

    @EventTarget
    public void onRender3D(Render3DEvent event) {
        if (render.get()) {
            if (target == null) {
                return;
            }
            GL11.glPushMatrix();
            GL11.glTranslated(
                    target.lastTickPosX + (target.posX - target.lastTickPosX) * mc.timer.renderPartialTicks - mc.getRenderManager().renderPosX,
                    target.lastTickPosY + (target.posY - target.lastTickPosY) * mc.timer.renderPartialTicks - mc.getRenderManager().renderPosY,
                    target.lastTickPosZ + (target.posZ - target.lastTickPosZ) * mc.timer.renderPartialTicks - mc.getRenderManager().renderPosZ
            );
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glRotatef(90F, 1F, 0F, 0F);

            GL11.glLineWidth(3 + 7.25F);

            GL11.glColor3f(0F, 0F, 0F);
            GL11.glBegin(GL11.GL_LINE_LOOP);

            for (int i = 0; i <= 360; i += 30) {
                GL11.glVertex2f((float) (Math.cos(i * Math.PI / 180.0) * range.getValue()), (float) (Math.sin(i * Math.PI / 180.0) * range.getValue()));
            }

            GL11.glEnd();

            GL11.glLineWidth(3);
            GL11.glBegin(GL11.GL_LINE_LOOP);

            for (int i = 0; i <= 360; i += 30) {
                if (active)
                    GL11.glColor4f(0.5f, 1, 0.5f, 1f);
                else
                    GL11.glColor4f(1f, 1f, 1f, 1f);

                GL11.glVertex2f((float) (Math.cos(i * Math.PI / 180.0) * range.getValue()), (float) (Math.sin(i * Math.PI / 180.0) * range.getValue()));
            }

            GL11.glEnd();

            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDisable(GL11.GL_LINE_SMOOTH);

            GL11.glPopMatrix();

            GlStateManager.resetColor();
            GL11.glColor4f(1F, 1F, 1F, 1F);
        }
    }
}
