package be.isach.ultracosmetics.version.dummy;

import be.isach.ultracosmetics.version.IEntityUtil;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.function.Predicate;

public class DummyEntityUtil implements IEntityUtil {

    @Override
    public void sendBlizzard(Player player, Location loc, Predicate<Entity> canAffectFunc, Vector v) {
    }

    @Override
    public void clearBlizzard(Player player) {
    }

    @Override
    public void sendDestroyPacket(Player player, Entity entity) {
    }

    @Override
    public void setStepHeight(Entity entity) {
    }

    @Override
    public void sendTeleportPacket(Player player, Entity entity) {
    }

}
