package be.isach.ultracosmetics.menu;

import be.isach.ultracosmetics.cosmetics.type.CosmeticType;
import org.bukkit.inventory.ItemStack;

import java.util.function.BooleanSupplier;

/**
 * Created by sacha on 04/04/2017.
 */
public class PurchaseData {

    private int price;
    private BooleanSupplier canPurchase;
    private Runnable onPurchase;
    private Runnable onCancel;
    private Runnable onPreview;
    private ItemStack showcaseItem;

    public int getBasePrice() {
        return price;
    }

    public ItemStack getShowcaseItem() {
        return showcaseItem;
    }

    public boolean canPurchase() {
        if (canPurchase != null) return canPurchase.getAsBoolean();
        return true;
    }

    public void runOnPurchase() {
        onPurchase.run();
    }

    public void runOnPreview() {
        onPreview.run();
    }

    public void runOnCancel() {
        if (onCancel != null) onCancel.run();
    }

    public void setBasePrice(int price) {
        this.price = price;
    }

    public void setCanPurchase(BooleanSupplier canPurchase) {
        this.canPurchase = canPurchase;
    }

    public void setOnPurchase(Runnable onPurchase) {
        this.onPurchase = onPurchase;
    }

    public void setOnCancel(Runnable onCancel) {
        this.onCancel = onCancel;
    }

    public void setOnPreview(Runnable onPreview) {
        this.onPreview = onPreview;
    }

    public void setShowcaseItem(ItemStack showcaseItem) {
        this.showcaseItem = showcaseItem;
    }

}
