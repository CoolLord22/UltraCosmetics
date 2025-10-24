package be.isach.ultracosmetics.cosmetics.suits;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.Updatable;
import be.isach.ultracosmetics.cosmetics.type.SuitType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import com.cryptomorin.xseries.XAttribute;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;

public class SuitAstronaut extends Suit implements Updatable {
    private static final Attribute GRAVITY = XAttribute.GRAVITY.get();
    private final boolean antiGravity = GRAVITY != null && SettingsManager.getConfig().getBoolean(getOptionPath("Antigravity"), true);
    private final AttributeModifier modifier;

    public SuitAstronaut(UltraPlayer ultraPlayer, SuitType suitType, UltraCosmetics ultraCosmetics) {
        super(ultraPlayer, suitType, ultraCosmetics);
        // -0.6 = 60% gravity reduction = 40% normal gravity
        this.modifier = ItemFactory.createAttributeModifier("antigravity", -0.6, AttributeModifier.Operation.MULTIPLY_SCALAR_1, EquipmentSlot.CHEST);
    }

    @Override
    protected void chestplateOnFullSuitEquipped() {
        if (!antiGravity) return;
        updateMeta(meta -> meta.addAttributeModifier(GRAVITY, this.modifier));
    }

    @Override
    protected void chestplateOnFullSuitUnequipped() {
        if (!antiGravity) return;
        updateMeta(meta -> meta.removeAttributeModifier(GRAVITY, this.modifier));
    }

    @Override
    public void onUpdate() {
        // We don't need to do anything special here, we're just Updatable for the above methods
    }
}
