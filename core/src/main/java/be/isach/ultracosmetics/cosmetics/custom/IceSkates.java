package be.isach.ultracosmetics.cosmetics.custom;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.ArmorCosmetic;
import be.isach.ultracosmetics.cosmetics.Updatable;
import be.isach.ultracosmetics.cosmetics.suits.ArmorSlot;
import be.isach.ultracosmetics.cosmetics.type.IceSkateType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import com.cryptomorin.xseries.XAttribute;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.function.Consumer;

public class IceSkates extends ArmorCosmetic<IceSkateType> implements Updatable {
    private static final Attribute SPEED = XAttribute.MOVEMENT_SPEED.get();
    private static final Attribute STEP = XAttribute.STEP_HEIGHT.get();
    private final AttributeModifier speedModifier;
    private final AttributeModifier stepModifier;

    public IceSkates(UltraPlayer owner, IceSkateType type, UltraCosmetics ultraCosmetics) {
        super(owner, type, ultraCosmetics);
        this.itemStack = type.getItemStack();
        this.speedModifier = ItemFactory.createAttributeModifier("speed", 1.25, AttributeModifier.Operation.MULTIPLY_SCALAR_1, EquipmentSlot.FEET);
        this.stepModifier = ItemFactory.createAttributeModifier("stepheight", 1, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.FEET);
    }

    protected void updateMeta(Consumer<ItemMeta> func) {
        ItemStack item = getArmorItem();
        ItemMeta meta = item.getItemMeta();
        try {
            func.accept(meta);
        } catch (Exception ignored) {}
        item.setItemMeta(meta);
        setArmorItem(item);
    }

    @Override
    protected void onEquip() {
        // should be handled by armor cosmetic?
    }

    @Override
    protected ArmorSlot getArmorSlot() {
        return ArmorSlot.BOOTS;
    }

    @Override
    public void onUpdate() {
        Block block = getPlayer().getLocation().subtract(0, 1, 0).getBlock();
        if(block.getType().equals(Material.ICE) || block.getType().equals(Material.PACKED_ICE) || block.getType().equals(Material.BLUE_ICE)) {
            updateMeta(meta -> meta.addAttributeModifier(SPEED, this.speedModifier));
            updateMeta(meta -> meta.addAttributeModifier(STEP, this.stepModifier));
            return;
        } else if(block.getType().equals(Material.AIR)) {
            block = block.getRelative(BlockFace.DOWN);
        }
        if(block.getType().equals(Material.AIR)) {
            block = block.getRelative(BlockFace.DOWN);
        }
        if(!block.getType().equals(Material.ICE) && !block.getType().equals(Material.PACKED_ICE) && !block.getType().equals(Material.BLUE_ICE)) {
            updateMeta(meta -> meta.removeAttributeModifier(SPEED, this.speedModifier));
            updateMeta(meta -> meta.removeAttributeModifier(STEP, this.stepModifier));
        }
    }
}
