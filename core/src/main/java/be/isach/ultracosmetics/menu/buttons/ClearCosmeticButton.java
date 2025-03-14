package be.isach.ultracosmetics.menu.buttons;

import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.menu.Button;
import be.isach.ultracosmetics.menu.ClickData;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import org.bukkit.inventory.ItemStack;

public class ClearCosmeticButton implements Button {
    private final ItemStack stack;
    private final Category category;

    public ClearCosmeticButton(Category category) {
        this.category = category;
        stack = ItemFactory.rename(ItemFactory.getItemStackFromConfig("Categories.Clear-Cosmetic-Item"),
                MessageManager.getMessage("Clear." + (category == null ? "Cosmetics" : category.getConfigPath())));
    }

    public ClearCosmeticButton() {
        this(null);
    }

    @Override
    public ItemStack getDisplayItem(UltraPlayer ultraPlayer) {
        return stack;
    }

    @Override
    public void onClick(ClickData clickData) {
        UltraPlayer clicker = clickData.getClicker();
        if (category == null) {
            clicker.clear(false );
            return;
        }
        if (category.isSuits()) {
            for (Category cat : Category.values()) {
                if (cat.isSuits()) {
                    clicker.removeCosmetic(cat);
                }
            }
        } else {
            clicker.removeCosmetic(category);
        }
        clickData.getMenu().refresh(clicker);
    }
}
