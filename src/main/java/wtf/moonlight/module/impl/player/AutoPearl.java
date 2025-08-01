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
package wtf.moonlight.module.impl.player;

import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemStack;
import org.joml.Vector2f;
import com.cubk.EventTarget;
import wtf.moonlight.events.player.MotionEvent;
import wtf.moonlight.module.Module;
import wtf.moonlight.module.Categor;
import wtf.moonlight.module.ModuleInfo;
import wtf.moonlight.module.impl.movement.Stuck;
import wtf.moonlight.util.Workers;
import wtf.moonlight.util.MathUtil;
import wtf.moonlight.util.TimerUtil;
import wtf.moonlight.util.DebugUtil;
import wtf.moonlight.component.FallDistanceComponent;
import wtf.moonlight.util.player.PlayerUtil;
import wtf.moonlight.util.player.ProjectileUtil;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@ModuleInfo(name = "AutoPearl", category = Categor.Player)
public class AutoPearl extends Module {
    private Future<Vector2f> calculationFuture;
    private boolean attempted;
    private boolean calculating;
    private int bestPearlSlot;

    @EventTarget
    public void onMotion(final MotionEvent event) throws ExecutionException, InterruptedException {
        if (mc.thePlayer.onGround) {
            this.attempted = false;
            this.calculating = false;
        }
        if (event.isPost() && this.calculating && (this.calculationFuture == null || this.calculationFuture.isDone())) {
            this.calculating = false;
            if (this.calculationFuture != null) {
                Stuck.throwPearl(this.calculationFuture.get());
            }
        }
        final boolean overVoid = !mc.thePlayer.onGround && !PlayerUtil.isBlockUnder(30.0, true);
        if (!this.attempted && !mc.thePlayer.onGround && overVoid && FallDistanceComponent.distance > 2.0f) {
            FallDistanceComponent.distance = 0.0f;
            this.attempted = true;
            for (int slot = 5; slot < 45; ++slot) {
                final ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(slot).getStack();
                if (stack != null && stack.getItem() instanceof ItemEnderPearl && slot >= 36) {
                    this.bestPearlSlot = slot;
                    mc.thePlayer.inventory.currentItem = this.bestPearlSlot - 36;
                }
            }
            if (this.bestPearlSlot == 0) {
                return;
            }
            DebugUtil.sendMessage(String.valueOf(this.bestPearlSlot));
            if (!(mc.thePlayer.inventoryContainer.getSlot(this.bestPearlSlot).getStack().getItem() instanceof ItemEnderPearl)) {
                return;
            }
            this.calculating = true;
            var job = new CalculateJob(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, 0.0, 0.0);
            this.calculationFuture = Workers.Default.submit(job);
            this.getModule(Stuck.class).setEnabled(true);
        }
    }

    private static class CalculateJob implements Callable<Vector2f> {
        private int iteration;
        private double temperature;
        private double energy;
        public boolean stop;
        private final ProjectileUtil.EnderPearlPredictor predictor;

        private CalculateJob(final double predictX, final double predictY, final double predictZ, final double minMotionY, final double maxMotionY) {
            this.predictor = new ProjectileUtil.EnderPearlPredictor(predictX, predictY, predictZ, minMotionY, maxMotionY);
            this.iteration = 0;
            this.temperature = 10.0;
            this.energy = 0.0;
            this.stop = false;
        }

        @Override
        public Vector2f call() {
            final TimerUtil timer = new TimerUtil();
            timer.reset();
            Vector2f solution = new Vector2f((float) MathUtil.randomizeDouble(-180, 180), (float) MathUtil.randomizeDouble(-90, 90));
            Vector2f current = solution;
            this.energy = this.predictor.assessRotation(solution);
            double solutionE = this.energy;
            while (this.temperature >= 1.0E-4 && !this.stop) {
                final Vector2f rotation = new Vector2f((float) (current.x + MathUtil.randomizeDouble(-this.temperature * 18.0, this.temperature * 18.0)), (float) (current.y + MathUtil.randomizeDouble(-this.temperature * 9.0, this.temperature * 9.0)));
                if (rotation.y > 90.0f) {
                    rotation.y = 90.0f;
                }
                if (rotation.y < -90.0f) {
                    rotation.y = -90.0f;
                }
                final double assessment = this.predictor.assessRotation(rotation);
                final double deltaE = assessment - this.energy;
                if (deltaE >= 0.0 || MathUtil.randomizeDouble(0, 1) < Math.exp(-deltaE / this.temperature * 100.0)) {
                    this.energy = assessment;
                    current = rotation;
                    if (assessment > solutionE) {
                        solutionE = assessment;
                        solution = new Vector2f(rotation.x, rotation.y);
                        DebugUtil.sendMessage("Find a better solution: (" + solution.x + ", " + solution.y + "), value: " + solutionE);
                    }
                }
                this.temperature *= 0.997;
                ++this.iteration;
            }
            DebugUtil.sendMessage("Simulated annealing completed within " + this.iteration + " iterations");
            DebugUtil.sendMessage("Time used: " + timer.getTime() + " solution energy: " + solutionE);
            return solution;
        }
    }
}