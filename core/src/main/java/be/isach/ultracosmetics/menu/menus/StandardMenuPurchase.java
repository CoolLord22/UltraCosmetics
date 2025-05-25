package be.isach.ultracosmetics.menu.menus;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.menu.MenuPurchase;
import be.isach.ultracosmetics.menu.PurchaseData;
import be.isach.ultracosmetics.menu.buttons.PurchaseCancelButton;
import be.isach.ultracosmetics.menu.buttons.PurchaseConfirmButton;
import be.isach.ultracosmetics.menu.buttons.PurchasePreviewButton;
import be.isach.ultracosmetics.menu.buttons.PurchaseShowcaseButton;
import be.isach.ultracosmetics.player.UltraPlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.Inventory;

/**
 * Created by sacha on 04/04/2017.
 * <p>
 * Standard implementation of MenuPurchase
 */
public class StandardMenuPurchase extends MenuPurchase {

    public StandardMenuPurchase(UltraCosmetics ultraCosmetics, Component name, PurchaseData purchaseData) {
        super(ultraCosmetics, name, purchaseData);
    }

    @Override
    protected void putItems(Inventory inventory, UltraPlayer player) {
        // Showcase Item
        putItem(inventory, 13, new PurchaseShowcaseButton(purchaseData), player);
        putItem(inventory, 49, new PurchasePreviewButton(purchaseData), player);

        // Purchase Item
        PurchaseConfirmButton confirmButton = new PurchaseConfirmButton(purchaseData, ultraCosmetics.getEconomyHandler());
        for (int i = 28; i < 30; i++) {
            for (int j = i; j <= i + 9; j += 9) {
                putItem(inventory, j, confirmButton, player);
            }
        }

        // Cancel Item
        PurchaseCancelButton cancelButton = new PurchaseCancelButton(purchaseData);
        for (int i = 33; i < 35; i++) {
            for (int j = i; j <= i + 9; j += 9) {
                putItem(inventory, j, cancelButton, player);
            }
        }
    }
}
