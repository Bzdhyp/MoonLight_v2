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
package wtf.moonlight.module.impl.misc;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import wtf.moonlight.Client;
import com.cubk.EventTarget;
import wtf.moonlight.events.packet.PacketEvent;
import wtf.moonlight.events.player.UpdateEvent;
import wtf.moonlight.module.Module;
import wtf.moonlight.module.Categor;
import wtf.moonlight.module.ModuleInfo;
import wtf.moonlight.module.impl.combat.AntiBot;
import wtf.moonlight.module.impl.misc.hackerdetector.Check;
import wtf.moonlight.module.impl.misc.hackerdetector.impl.*;
import wtf.moonlight.module.values.impl.BoolValue;
import wtf.moonlight.module.values.impl.MultiBoolValue;

import java.util.*;

@ModuleInfo(name = "HackerDetector", category = Categor.Misc)
public class HackerDetector extends Module {
    public final MultiBoolValue options = new MultiBoolValue("Detects", Arrays.asList(
            new BoolValue("Angle", false),
            new BoolValue("Auto Block", false),
            new BoolValue("Legit Scaffold", false),
            new BoolValue("Invalid motion", false),
            new BoolValue("No Fall", false),
            new BoolValue("No Slow", false),
            new BoolValue("Scaffold", false),
            new BoolValue("Velocity", false),
            new BoolValue("Omni Sprint", false)
    ), this);

    public final BoolValue selfCheck = new BoolValue("Self", false, this);
    private final Set<EntityPlayer> hackers = new HashSet<>();
    private final ArrayList<Check> checks = new ArrayList<>();

    public HackerDetector() {
        addChecks(
                new AngleCheck(),
                new AutoBlockCheck(),
                new LegitScaffoldCheck(),
                new MotionCheck(),
                new NoFallCheck(),
                new NoSlowCheck(),
                new ScaffoldCheck(),
                new VelocityCheck(),
                new OmniSprintCheck()

        );
    }

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        for (EntityPlayer player : mc.theWorld.playerEntities) {
            for (Check check : checks) {
                if ((selfCheck.get() || player != mc.thePlayer) && !player.isDead && !Client.INSTANCE.getFriendManager().isFriend(player)) {
                    if(isEnabled(AntiBot.class) && getModule(AntiBot.class).bots.contains(player))
                        continue;
                    if (options.isEnabled(check.getName())) {
                        check.onUpdate(player);
                    }
                }
            }
        }
    }

    @EventTarget
    public final void onPacketReceive(PacketEvent event) {
        if (event.getState() != PacketEvent.State.INCOMING) return;
        if (event.getPacket() instanceof S14PacketEntity || event.getPacket() instanceof S18PacketEntityTeleport) {
            for (EntityPlayer player : mc.theWorld.playerEntities) {
                for (Check check : checks) {
                    if ((selfCheck.get() || player != mc.thePlayer) && !player.isDead && !Client.INSTANCE.getFriendManager().isFriend(player)) {
                        if(isEnabled(AntiBot.class) && getModule(AntiBot.class).isBot(player))
                            continue;
                        if (options.isEnabled(check.getName())) {
                            check.onPacketReceive(event, player);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onDisable() {
        hackers.clear();
    }

    public void addChecks(Check... checks) {
        this.checks.addAll(Arrays.asList(checks));
    }

    public void mark(EntityPlayer ent) {
        hackers.add(ent);
    }

    public boolean isHacker(EntityPlayer ent) {
        for (EntityPlayer hacker : hackers) {
            if (!ent.getName().equals(hacker.getName())) continue;
            return true;
        }
        return false;
    }

}