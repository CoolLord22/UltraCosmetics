package be.isach.ultracosmetics.cosmetics.pets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.PetType;
import be.isach.ultracosmetics.player.UltraPlayer;

public class PetSniffer extends Pet {
    public PetSniffer(UltraPlayer owner, PetType petType, UltraCosmetics ultraCosmetics) {
        super(owner, petType, ultraCosmetics);
    }

    @Override
    protected void setupEntity() {
        setMovementSpeed(0.2);
    }
}
