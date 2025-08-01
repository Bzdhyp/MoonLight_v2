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
package wtf.moonlight.module.impl.misc.hackerdetector.impl;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.S14PacketEntity;
import wtf.moonlight.events.packet.PacketEvent;
import wtf.moonlight.module.impl.misc.hackerdetector.Check;
import wtf.moonlight.util.player.MovementUtil;

public class VelocityCheck extends Check {

    @Override
    public String getName() {
        return "Velocity";
    }

    @Override
    public void onPacketReceive(PacketEvent event, EntityPlayer player) {

        if (event.getPacket() instanceof S14PacketEntity) {
            if (MovementUtil.getSpeed(player) == 0.0 && player.hurtResistantTime > 6 && player.hurtResistantTime < 1 && !mc.theWorld.checkBlockCollision(player.getEntityBoundingBox().expand(0.05, 0.0, 0.05))) {
                flag(player, "Invalid velocity");
            }
        }
    }

    @Override
    public void onUpdate(EntityPlayer player) {

    }
}

