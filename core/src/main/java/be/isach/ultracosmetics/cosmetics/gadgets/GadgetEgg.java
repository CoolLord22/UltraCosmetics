package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an instance of a snowball gadget summoned by a player.
 *
 * @author iSach
 * @since 12-15-2015
 */
public class GadgetEgg extends Gadget {

    private List<Egg> eggs = new ArrayList<>();

    public GadgetEgg(UltraPlayer owner, GadgetType type, UltraCosmetics ultraCosmetics) {
        super(owner, type, ultraCosmetics);
    }

    @Override
    protected void onRightClick() {
        Egg egg = getPlayer().launchProjectile(Egg.class);
        getPlayer().playSound(getPlayer(), Sound.ENTITY_CHICKEN_EGG, SoundCategory.NEUTRAL, 1.0f, 1.0f);
        egg.setVelocity(getPlayer().getEyeLocation().getDirection().multiply(1.4d));
        egg.setMetadata("EGG_NO_DAMAGE", new FixedMetadataValue(getUltraCosmetics(), ""));
    }

    @Override
    public void onClear() {
        for (Egg egg : eggs) {
            egg.remove();
        }
        eggs.clear();
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if(event.getEntity() instanceof Egg egg) {
            if (event.getEntity().hasMetadata("EGG_NO_DAMAGE")) {
                if(egg.getShooter() instanceof Player shooter) {
                    if(event.getHitEntity() != null) {
                        shooter.playSound(event.getHitEntity(), Sound.ENTITY_TURTLE_EGG_CRACK, SoundCategory.NEUTRAL, 2.0f, 2.0f);
                    }
                }
            }
        }
    }
}
