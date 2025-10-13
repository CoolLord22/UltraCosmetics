package be.isach.ultracosmetics.listeners;

import org.bukkit.entity.EnderDragonPart;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Zombie;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityTransformEvent;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

/**
 * Main listener
 *
 * @author iSach
 * @since 12-25-2015
 */
public class MainListener implements Listener {

    @EventHandler
    public void onInteractAtEntity(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked().hasMetadata("NO_INTER") || event.getRightClicked().hasMetadata("C_AD_ArmorStand")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamageEntity(EntityDamageEvent event) {
        if (isPet(event.getEntity()) || isMount(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        if (isPet(event.getDamager()) || isMount(event.getDamager())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onHopperPickup(InventoryPickupItemEvent event) {
        processPickup(event.getItem(), event);
    }

    @EventHandler
    public void onPlayerPickup(EntityPickupItemEvent event) {
        processPickup(event.getItem(), event);
    }

    public void processPickup(Item item, Cancellable event) {
        if (item.hasMetadata("UNPICKABLEUP")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onMerge(ItemMergeEvent event) {
        if (event.getEntity().hasMetadata("UNPICKABLEUP") || event.getTarget().hasMetadata("UNPICKABLEUP")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityPickup(EntityPickupItemEvent event) {
        if (isPet(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrowned(EntityTransformEvent event) {
        if (event.getTransformReason() == EntityTransformEvent.TransformReason.DROWNED && event.getEntity().hasMetadata("Pet")) {
            event.setCancelled(true);
            ((Zombie) event.getEntity()).setConversionTime(Integer.MAX_VALUE);
        }
    }

    @EventHandler
    public void stopDragonDamage(EntityExplodeEvent event) {
        Entity e = event.getEntity();
        if (e instanceof EnderDragonPart) e = ((EnderDragonPart) e).getParent();
        if (isPet(e) || isMount(e)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Entity e = event.getDamager();
        if (e instanceof EnderDragonPart) {
            e = ((EnderDragonPart) e).getParent();
        }
        if (isPet(e) || isMount(e)) {
            event.setCancelled(true);
        }
    }

    private boolean isPet(Entity entity) {
        if (entity == null) return false;
        return entity.hasMetadata("Pet");
    }

    private boolean isMount(Entity entity) {
        if (entity == null) return false;
        return entity.hasMetadata("Mount");
    }
}
