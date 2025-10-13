package be.isach.ultracosmetics.cosmetics;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.CosmeticEntType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.EntitySpawningManager;
import com.cryptomorin.xseries.XAttribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.entity.EntityPortalEvent;

public abstract class EntityCosmetic<T extends CosmeticEntType<?>, E extends Entity> extends Cosmetic<T> {
    /**
     * The Entity, if it isn't a Custom Entity.
     */
    protected E entity;

    public EntityCosmetic(UltraPlayer owner, T type, UltraCosmetics ultraCosmetics) {
        super(owner, type, ultraCosmetics);
    }

    public E getEntity() {
        return entity;
    }

    @SuppressWarnings("unchecked")
    protected E spawnEntity() {
        // Bypass WorldGuard protection.
        return EntitySpawningManager.withBypass(() -> (E) getPlayer().getWorld().spawnEntity(getPlayer().getLocation(), getType().getEntityType()));
    }

    protected void removeEntity() {
        removeEntitySafe(entity);
        entity = null;
    }

    protected void removeEntitySafe(Entity entity) {
        if (entity != null) {
            if (getUltraCosmetics().isEnabled()) {
                getUltraCosmetics().getScheduler().runAtEntity(entity, t -> entity.remove());
            } else {
                try {
                    entity.remove();
                } catch (Exception ignored) {
                    // On Folia this is likely to fail, but pets are non-persistent anyway so it's not a big deal.
                }
            }
        }
    }

    protected void setupEntity() {
    }

    @SuppressWarnings("DataFlowIssue")
    protected void setMovementSpeed(double speed) {
        ((LivingEntity) entity).getAttribute(XAttribute.MOVEMENT_SPEED.get()).setBaseValue(speed);
    }

    protected void onPortal() {
    }

    @EventHandler
    public void onPortal(EntityPortalEvent event) {
        if (event.getEntity() == getEntity()) {
            event.setCancelled(true);
            onPortal();
        }
    }

    // Going through portals seems to break pathfinders
    @EventHandler
    public void onPortalEnter(EntityPortalEnterEvent event) {
        // EntityPortalEnterEvent is cancellable on recent versions of Paper, but not Spigot.
        // On Folia, EntityPortalEvent isn't called at all so this is the only way to do it.
        if (!(event instanceof Cancellable c)) {
            return;
        }
        if (event.getEntity() == getEntity()) {
            c.setCancelled(true);
            onPortal();
        }
    }

}
