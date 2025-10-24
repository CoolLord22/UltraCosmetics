package be.isach.ultracosmetics.cosmetics.suits;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.Updatable;
import be.isach.ultracosmetics.cosmetics.type.SuitType;
import be.isach.ultracosmetics.player.UltraPlayer;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SuitSlime extends Suit implements Updatable {
    private final boolean jumpBoost = SettingsManager.getConfig().getBoolean(getOptionPath("Jump-Boost"), true);
    private final PotionEffect effect = new PotionEffect(PotionEffectType.JUMP_BOOST, 60, 1);

    public SuitSlime(UltraPlayer ultraPlayer, SuitType suitType, UltraCosmetics ultraCosmetics) {
        super(ultraPlayer, suitType, ultraCosmetics);
    }

    @Override
    public void onUpdate() {
        if (jumpBoost && isFullSuit()) {
            getPlayer().addPotionEffect(effect);
        }
    }
}
