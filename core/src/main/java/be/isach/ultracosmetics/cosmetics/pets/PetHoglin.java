package be.isach.ultracosmetics.cosmetics.pets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import org.bukkit.entity.Hoglin;

/**
 * Represents an instance of a hoglin pet summoned by a player.
 *
 * @author Chris6ix
 * @since 24-09-2022
 */
public class PetHoglin extends Pet {
    public PetHoglin(UltraPlayer owner, PetType type, UltraCosmetics ultraCosmetics) {
        super(owner, type, ultraCosmetics);
    }

    @Override
    public void setupEntity() {
        ((Hoglin) entity).setImmuneToZombification(true);
    }
}
