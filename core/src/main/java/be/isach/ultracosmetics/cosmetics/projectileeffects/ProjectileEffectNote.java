package be.isach.ultracosmetics.cosmetics.projectileeffects;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.ProjectileEffectType;
import be.isach.ultracosmetics.player.UltraPlayer;
import com.cryptomorin.xseries.XSound;
import org.bukkit.Sound;
import org.bukkit.entity.Projectile;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProjectileEffectNote extends ProjectileEffectBasicTrail {
    private static final Sound SOUND = XSound.BLOCK_NOTE_BLOCK_HARP.get();
    private final Map<UUID, Integer> colors = new HashMap<>();

    public ProjectileEffectNote(UltraPlayer owner, ProjectileEffectType type, UltraCosmetics ultraCosmetics) {
        super(owner, type, ultraCosmetics);
        display.withExtra(1);
    }

    @Override
    public void showParticles(Projectile projectile) {
        int color = colors.getOrDefault(projectile.getUniqueId(), 0);
        display.withNoteColor(color).spawn(projectile.getLocation());
        // Magic function source: https://minecraft.fandom.com/wiki/Note_Block#Notes
        // I know there is an API for playing note block sounds,
        // but there doesn't seem to be a great way to play them all sequentially.
        float pitch = (float) Math.pow(2, (float) (color - 12) / 12);
        getPlayer().playSound(projectile.getLocation(), SOUND, 1, pitch);
        if (++color > 24) {
            color = 0;
        }
        colors.put(projectile.getUniqueId(), color);
    }

    @Override
    public void projectileLanded(Projectile projectile) {
        colors.remove(projectile.getUniqueId());
    }
}
