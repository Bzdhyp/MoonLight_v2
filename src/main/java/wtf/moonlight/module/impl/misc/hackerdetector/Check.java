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
package wtf.moonlight.module.impl.misc.hackerdetector;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import wtf.moonlight.events.packet.PacketEvent;
import wtf.moonlight.gui.notification.NotificationManager;
import wtf.moonlight.module.impl.misc.HackerDetector;
import wtf.moonlight.gui.notification.NotificationType;
import wtf.moonlight.util.misc.InstanceAccess;
import wtf.moonlight.util.TimerUtil;
import wtf.moonlight.util.DebugUtil;

public abstract class Check implements InstanceAccess {

    public TimerUtil flagTimer = new TimerUtil();
    public int time = 2;

    public abstract String getName();

    public abstract void onPacketReceive(PacketEvent event, EntityPlayer player);

    public abstract void onUpdate(EntityPlayer player);

    public void onMotion(EntityPlayer player, double x, double y, double z) {
    }

    public void flag(EntityPlayer player, String verbose) {
        if (flagTimer.hasTimeElapsed(time * 1000L)) {
            DebugUtil.sendMessage(player.getName() + EnumChatFormatting.WHITE + " detected for " + EnumChatFormatting.GRAY + getName() + EnumChatFormatting.WHITE + ", " + EnumChatFormatting.WHITE + verbose);
            NotificationManager.post(NotificationType.WARNING, player.getName() + EnumChatFormatting.WHITE + " detected for " + EnumChatFormatting.GRAY + getName() + EnumChatFormatting.WHITE, verbose, 2);
            INSTANCE.getModuleManager().getModule(HackerDetector.class).mark(player);
            flagTimer.reset();
        }
    }
}