package be.isach.ultracosmetics.util;

import be.isach.ultracosmetics.UltraCosmeticsData;
import com.cryptomorin.xseries.XEntityType;
import me.gamercoder215.mobchip.EntityBrain;
import me.gamercoder215.mobchip.ai.goal.CustomPathfinder;
import me.gamercoder215.mobchip.bukkit.BukkitBrain;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.util.Vector;

public class PetPathfinder extends CustomPathfinder {
    private final Player target;
    private final EntityBrain brain;
    private final double maxRange;
    private final double tpRange;
    private final double yOffset;
    private final double speed;
    private final boolean useEyeLocation;
    private boolean stopped = false;

    public PetPathfinder(Mob mob, Player target, double maxRange, double tpRange, double yOffset) {
        super(mob);
        this.target = target;
        this.brain = BukkitBrain.getBrain(mob);
        this.maxRange = maxRange;
        this.tpRange = tpRange;
        this.yOffset = yOffset;
        double speed = 1.15;
        boolean useEyeLocation = false;
        if (mob.getType() == EntityType.VEX) {
            speed = 0.75;
            useEyeLocation = true;
        } else if (mob instanceof Slime) {
            speed = 2.5;
        }
        this.speed = speed;
        this.useEyeLocation = useEyeLocation;
    }

    public PetPathfinder(Mob mob, Player target) {
        this(mob, target, 3, 10, 0);
    }

    @Override
    public boolean canStart() {
        return true;
    }

    @Override
    public PathfinderFlag[] getFlags() {
        return new PathfinderFlag[] {PathfinderFlag.MOVEMENT, PathfinderFlag.JUMPING};
    }

    @Override
    public void start() {

    }

    @Override
    public void tick() {
        Location targetLoc = useEyeLocation ? target.getEyeLocation() : target.getLocation();
        Location ourLoc = entity.getLocation().add(0, yOffset, 0);
        // Entity will be respawned by Pet instance
        if (ourLoc.getWorld() != targetLoc.getWorld()) {
            return;
        }

        // Slime or magma cube
        if (entity instanceof Slime) {
            Location deltaLoc = ourLoc.clone().subtract(targetLoc);
            double direction = -Math.atan2(deltaLoc.getX(), deltaLoc.getZ());
            float degrees = (float) (Math.toDegrees(direction) + 180);
            brain.getBody().setRotation(degrees, 0);
        }

        if (entity.isLeashed()) {
            return;
        }

        double distSquared = ourLoc.distanceSquared(targetLoc);
        if (distSquared > tpRange * tpRange) {
            // Very far from target, teleport there
            UltraCosmeticsData.get().getPlugin().getScheduler().teleportAsync(entity, targetLoc);
            brain.getController().moveTo(targetLoc.subtract(0, yOffset, 0), speed);
            return;
        }
        if (distSquared > maxRange * maxRange) {
            // Far from target, move toward it
            // Approach the target location but stop a little short of it
            Vector direction = targetLoc.clone().subtract(ourLoc).toVector().normalize().multiply(0.5);
            brain.getController().moveTo(targetLoc.subtract(direction).subtract(0, yOffset, 0), speed);
        } else if (entity.getType() == XEntityType.HAPPY_GHAST.get()) {
            // If we are a happy ghast and are close enough to the target, tell it to stop.

            // For some reason this is the only thing I can figure out that will keep the ghast hovering in place.
            // Everything else I've tried just results in the happy ghast spinning around in the air indefinitely.
            Location loc = ourLoc.add(ourLoc.subtract(targetLoc).toVector());
            brain.getController().moveTo(loc.subtract(0, yOffset, 0), 0);
        }
    }

}
