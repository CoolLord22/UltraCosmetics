package be.isach.ultracosmetics.listeners;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.menu.MenuItemHandler;
import be.isach.ultracosmetics.util.UnmovableItemProvider;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class UnmovableItemListener implements Listener {
    private final Set<UnmovableItemProvider> providers = ConcurrentHashMap.newKeySet();

    public UnmovableItemListener(UltraCosmetics ultraCosmetics) {
        if (SettingsManager.getConfig().getBoolean("Menu-Item.Enabled")) {
            providers.add(new MenuItemHandler(ultraCosmetics));
        }
    }

    public void addProvider(UnmovableItemProvider provider) {
        providers.add(provider);
    }

    public void removeProvider(UnmovableItemProvider provider) {
        providers.remove(provider);
    }

    public void forEachProvider(Player player, Consumer<UnmovableItemProvider> func) {
        for (UnmovableItemProvider provider : providers) {
            if (provider.getPlayer() == null || provider.getPlayer() == player) {
                func.accept(provider);
            }
        }
    }

    public void forEachProviderWithItem(Player player, ItemStack stack, Consumer<UnmovableItemProvider> func) {
        forEachProvider(player, p -> {
            if (p.itemMatches(stack)) {
                func.accept(p);
            }
        });
    }

    public boolean isImmovable(Player player, ItemStack itemStack) {
        // Can't really use lambda methods here since we want to return
        // when we've found the item.
        for (UnmovableItemProvider provider : providers) {
            if (provider.getPlayer() == null || provider.getPlayer() == player) {
                if (provider.itemMatches(itemStack)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean slotCheck(UnmovableItemProvider provider, int slot, Player player) {
        if (provider.getSlot() != slot) {
            provider.moveItem(slot, player);
            return false;
        }
        return true;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onItemDrop(PlayerDropItemEvent event) {
        forEachProviderWithItem(event.getPlayer(), event.getItemDrop().getItemStack(), p -> {
            if (slotCheck(p, event.getPlayer().getInventory().getHeldItemSlot(), event.getPlayer())) {
                p.handleDrop(event);
            }
        });
    }

    @EventHandler
    public void cancelMove(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        forEachProvider(player, p -> {
            if (p.itemMatches(event.getCurrentItem())) {
                slotCheck(p, event.getSlot(), player);
            } else if (event.getClick() == ClickType.NUMBER_KEY && p.itemMatches(player.getInventory().getItem(event.getHotbarButton()))) {
                slotCheck(p, event.getHotbarButton(), player);
            } else {
                return;
            }
            event.setCancelled(true);
            player.updateInventory();
            p.handleClick(player);
        });

    }

    @EventHandler
    public void cancelMove(InventoryCreativeEvent event) {
        Player player = (Player) event.getWhoClicked();
        forEachProvider(player, p -> {
            if (p.itemMatches(event.getCurrentItem())) {
                slotCheck(p, event.getSlot(), player);
            } else if (!p.itemMatches(event.getCursor())) {
                return;
            }
            event.setCancelled(true);
            // Close the inventory because clicking again results in the event being handled client side
            player.closeInventory();
            p.handleClick(player);
        });
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        // Prevent losing the item when clicking on an entity.
        // This can happen in a number of different ways, so we just cancel the event unless it's a player,
        // because some gadgets target players and so clicking on a player may be useful.
        if (event.getRightClicked() instanceof Player) return;
        forEachProviderWithItem(event.getPlayer(), event.getPlayer().getInventory().getItemInMainHand(), p -> {
            slotCheck(p, event.getPlayer().getInventory().getHeldItemSlot(), event.getPlayer());
            event.setCancelled(true);
        });
    }

    @EventHandler
    public void onPlayerInteractArmorStand(PlayerArmorStandManipulateEvent event) {
        // Not really sure why a separate listener is required here
        onPlayerInteractEntity(event);
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL) return;
        forEachProviderWithItem(event.getPlayer(), event.getItem(), p -> {
            if (slotCheck(p, event.getPlayer().getInventory().getHeldItemSlot(), event.getPlayer())) {
                p.handleInteract(event);
            }
        });
    }

    @EventHandler
    public void onPlayerSwapoffHand(PlayerSwapHandItemsEvent event) {
        forEachProvider(event.getPlayer(), p -> {
            if (p.itemMatches(event.getMainHandItem()) || p.itemMatches(event.getOffHandItem())) {
                event.setCancelled(true);
            }
        });
    }
}
