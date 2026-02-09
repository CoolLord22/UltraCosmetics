package be.isach.ultracosmetics.cosmetics.type;

import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.custom.IceSkates;
import be.isach.ultracosmetics.util.ItemFactory;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

public class IceSkateType extends CosmeticType<IceSkates> {

    private final ItemStack boots;

    public IceSkateType() {
        super(Category.CUSTOM, "IceSkates", XMaterial.LEATHER_BOOTS, IceSkates.class, true);
        this.boots = createItem();
    }

    private ItemStack createItem() {
        ItemStack boots = ItemFactory.createColouredLeather(Material.LEATHER_BOOTS, 85, 255, 255);
        ItemFactory.rename(boots, getName());
        ItemFactory.applyCosmeticMarker(boots);
        ItemFactory.addGlow(boots);
        setTrim(boots);
        ItemFactory.setFlags(boots);
        ItemFactory.hideAttributes(boots);
        return boots;
    }

    private void setTrim(ItemStack item) {
        ArmorMeta meta = (ArmorMeta) item.getItemMeta();
        meta.setLore(getDescription());
        meta.setTrim(new ArmorTrim(TrimMaterial.IRON, TrimPattern.WILD));
        item.setItemMeta(meta);
    }

    public static void register() {
        new IceSkateType();
    }

    @Override
    public ItemStack getItemStack() {
        return boots.clone();
    }
}
