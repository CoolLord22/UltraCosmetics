package be.isach.ultracosmetics.cosmetics.projectileeffects;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.ProjectileEffectType;
import be.isach.ultracosmetics.player.UltraPlayer;

import java.awt.Color;

public class ProjectileEffectRedstone extends ProjectileEffectBasicTrail {
    public ProjectileEffectRedstone(UltraPlayer owner, ProjectileEffectType type, UltraCosmetics ultraCosmetics) {
        super(owner, type, ultraCosmetics);
        display.withColor(Color.RED);
    }
}
