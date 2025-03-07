package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.Area;
import be.isach.ultracosmetics.util.EntitySpawner;
import be.isach.ultracosmetics.util.MathUtils;
import com.cryptomorin.xseries.XSound;
import com.cryptomorin.xseries.particles.ParticleDisplay;
import com.cryptomorin.xseries.particles.XParticle;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import me.gamercoder215.mobchip.EntityBrain;
import me.gamercoder215.mobchip.ai.goal.PathfinderPanic;
import me.gamercoder215.mobchip.bukkit.BukkitBrain;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents an instance of a explosive sheep gadget summoned by a player.
 *
 * @author iSach
 * @since 08-08-2015
 */
public class GadgetExplosiveSheep extends Gadget {

    // I know 'sheeps' isn't the plural form of 'sheep' but it's funny
    // and it distinguishes it from the local variables named 'sheep' (singular)
    private final Set<Sheep> sheeps = new HashSet<>();
    private WrappedTask sheepRemovalRunnable = null;
    private final XSound.SoundPlayer tickSound = XSound.BLOCK_NOTE_BLOCK_HAT.record().withVolume(1.4f).withPitch(1.5f).soundPlayer();
    private final XSound.SoundPlayer explodeSound = XSound.ENTITY_GENERIC_EXPLODE.record().withVolume(1.4f).withPitch(1.5f).soundPlayer();
    private final ParticleDisplay emitter = ParticleDisplay.of(XParticle.EXPLOSION_EMITTER);
    private final ParticleDisplay lava = ParticleDisplay.of(XParticle.LAVA).withCount(5);

    public GadgetExplosiveSheep(UltraPlayer owner, GadgetType type, UltraCosmetics ultraCosmetics) {
        super(owner, type, ultraCosmetics);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected boolean checkRequirements(PlayerInteractEvent event) {
        if (!getPlayer().isOnGround()) {
            MessageManager.send(getPlayer(), "Gadgets.Rocket.Not-On-Ground");
            return false;
        }
        return true;
    }

    @Override
    protected void onRightClick() {
        Location loc = getPlayer().getLocation().add(getPlayer().getEyeLocation().getDirection().multiply(0.5));
        loc.setY(getPlayer().getLocation().getBlockY() + 1);
        Sheep sheep = getPlayer().getWorld().spawn(loc, Sheep.class);

        sheep.setNoDamageTicks(100000);
        sheeps.add(sheep);

        EntityBrain brain = BukkitBrain.getBrain(sheep);
        brain.getGoalAI().clear();
        brain.getTargetAI().clear();

        new SheepColorRunnable(sheep, 7, true);
    }

    @EventHandler
    public void onShear(PlayerShearEntityEvent event) {
        if (sheeps.contains(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (sheeps.contains(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onClear() {
        for (Sheep sheep : sheeps) {
            sheep.remove();
        }
        if (sheepRemovalRunnable != null) {
            // No try-catch because this gadget doesn't run on legacy versions anyway.
            sheepRemovalRunnable.cancel();
        }
    }

    private class SheepColorRunnable {
        private final Sheep s;
        private boolean red;
        private double time;

        private final WrappedTask task;

        private SheepColorRunnable(Sheep s, double time, boolean red) {
            this.s = s;
            this.red = red;
            this.time = time;
            task = getUltraCosmetics().getScheduler().runAtEntityLater(s, this::run, (int) time);
        }

        public void run() {
            if (getOwner() == null || getPlayer() == null || !s.isValid()) {
                task.cancel();
                return;
            }
            s.setColor(red ? DyeColor.RED : DyeColor.WHITE);
            tickSound.atLocation(s.getLocation()).play();
            red = !red;
            time -= 0.2;

            if (time >= 0.5) {
                // unfortunately we can't reschedule this existing task, we have to make a new one.
                new SheepColorRunnable(s, time, red);
                return;
            }
            explodeSound.atLocation(s.getLocation()).play();
            emitter.spawn(s.getLocation());
            sheeps.remove(s);
            s.remove();
            DyeColor[] colors = DyeColor.values();
            Player player = getPlayer();
            EntitySpawner<Sheep> sheeps = new EntitySpawner<>(EntityType.SHEEP, s.getLocation(), 50, sheep -> {
                sheep.setColor(colors[RANDOM.nextInt(colors.length)]);
                MathUtils.applyVelocity(sheep, new Vector(RANDOM.nextDouble() - 0.5, RANDOM.nextDouble() / 2, RANDOM.nextDouble() - 0.5).multiply(2).add(new Vector(0, 0.8, 0)));
                sheep.setBaby();
                sheep.setAgeLock(true);
                sheep.setNoDamageTicks(120);
                EntityBrain brain = BukkitBrain.getBrain(sheep);
                brain.getGoalAI().clear();
                brain.getTargetAI().clear();
                // Pathfinder requires the entity has been damaged by another entity
                sheep.damage(1, player);
                brain.getGoalAI().put(new PathfinderPanic(sheep, 2), 0);
            }, getUltraCosmetics());

            sheepRemovalRunnable = getUltraCosmetics().getScheduler().runAtEntityLater(player, () -> {
                for (Sheep sheep : sheeps.getEntities()) {
                    lava.spawn(sheep.getLocation());
                }
                sheeps.removeEntities();
            }, 110);
        }
    }
}
