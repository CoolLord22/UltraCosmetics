package be.isach.ultracosmetics.menu.buttons;

import be.isach.ultracosmetics.menu.Button;
import be.isach.ultracosmetics.menu.ClickData;
import be.isach.ultracosmetics.menu.PurchaseData;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import com.cryptomorin.xseries.XMaterial;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.inventory.ItemStack;

public class PurchasePreviewButton implements Button {
    private final PurchaseData purchaseData;

    public PurchasePreviewButton(PurchaseData purchaseData) {
        this.purchaseData = purchaseData;
    }

    @Override
    public ItemStack getDisplayItem(UltraPlayer ultraPlayer) {
        return ItemFactory.create(XMaterial.ARMOR_STAND, Component.text("Preview").color(NamedTextColor.GOLD));
    }

    @Override
    public void onClick(ClickData clickData) {
        purchaseData.runOnPreview();
    }
}
