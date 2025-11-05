package be.isach.ultracosmetics.cosmetics.pets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import org.bukkit.entity.PiglinBrute;

/**
 * Represents an instance of a piglin brute pet summoned by a player.
 *
 * @author Chris6ix
 * @since 25-09-2022
 */
public class PetPiglinBrute extends Pet {
    public PetPiglinBrute(UltraPlayer owner, PetType type, UltraCosmetics ultraCosmetics) {
        super(owner, type, ultraCosmetics);
    }

    @Override
    public void setupEntity() {
        ((PiglinBrute) entity).setImmuneToZombification(true);
    }
}
