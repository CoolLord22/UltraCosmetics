package be.isach.ultracosmetics.cosmetics.pets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import com.cryptomorin.xseries.XAttribute;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Turtle;

public class PetTurtle extends Pet {
    private static final Attribute SCALE = XAttribute.SCALE.get();

    public PetTurtle(UltraPlayer owner, PetType petType, UltraCosmetics ultraCosmetics) {
        super(owner, petType, ultraCosmetics);
    }

    @Override
    public void setupEntity() {
        if (SettingsManager.getConfig().getBoolean(getOptionPath("Bigger-Babies"))
                && !((Turtle) entity).isAdult() && SCALE != null) {
            AttributeModifier mod = ItemFactory.createAttributeModifier("turtle", 1, AttributeModifier.Operation.ADD_NUMBER, null);
            entity.getAttribute(SCALE).addModifier(mod);
        }
    }
}
