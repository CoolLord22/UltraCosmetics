package be.isach.ultracosmetics.cosmetics.pets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import org.bukkit.entity.Tadpole;

/**
 * Represents an instance of a tadpole pet summoned by a player.
 *
 * @author Chris6ix
 * @since 07-09-2022
 */
public class PetTadpole extends Pet {
    public PetTadpole(UltraPlayer owner, PetType type, UltraCosmetics ultraCosmetics) {
        super(owner, type, ultraCosmetics);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        ((Tadpole) entity).setAge(0);
    }
}
