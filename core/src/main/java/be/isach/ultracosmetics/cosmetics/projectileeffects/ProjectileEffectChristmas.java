package be.isach.ultracosmetics.cosmetics.projectileeffects;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.ProjectileEffectType;
import be.isach.ultracosmetics.player.UltraPlayer;
import com.cryptomorin.xseries.particles.ParticleDisplay;
import com.cryptomorin.xseries.particles.XParticle;
import org.bukkit.Location;
import org.bukkit.entity.Projectile;

import java.awt.*;

public class ProjectileEffectChristmas extends ProjectileEffectHelix {
    private final ParticleDisplay cloud = ParticleDisplay.of(XParticle.CLOUD);
    private final ParticleDisplay red = ParticleDisplay.of(XParticle.DUST).withColor(new Color(211, 47, 47));
    private final ParticleDisplay green = ParticleDisplay.of(XParticle.DUST).withColor(new Color(52, 168, 83));

    public ProjectileEffectChristmas(UltraPlayer owner, ProjectileEffectType type, UltraCosmetics ultraCosmetics) {
        super(owner, type, ultraCosmetics);
    }

    @Override
    public void showHelix(Projectile projectile, Location a, Location b) {
        cloud.spawn(projectile.getLocation());
        red.spawn(a);
        green.spawn(b);
    }
}
