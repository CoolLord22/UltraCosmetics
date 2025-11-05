package be.isach.ultracosmetics.cosmetics.projectileeffects;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.ProjectileEffectType;
import be.isach.ultracosmetics.player.UltraPlayer;

import java.awt.*;

public class ProjectileEffectRedstoneHelix extends ProjectileEffectHelix {
    public ProjectileEffectRedstoneHelix(UltraPlayer owner, ProjectileEffectType type, UltraCosmetics ultraCosmetics) {
        super(owner, type, ultraCosmetics);
        display.withColor(Color.RED);
    }
}
