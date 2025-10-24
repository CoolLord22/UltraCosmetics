package be.isach.ultracosmetics.run;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.PlayerAffectingCosmetic;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.player.UltraPlayerManager;
import be.isach.ultracosmetics.task.UltraTask;
import org.bukkit.entity.Player;

/**
 * Checks for vanished players that still have cosmetics equipped.
 */
public class VanishChecker extends UltraTask {
    private final UltraCosmetics ultraCosmetics;
    private final UltraPlayerManager pm;

    public VanishChecker(UltraCosmetics ultraCosmetics) {
        this.ultraCosmetics = ultraCosmetics;
        this.pm = ultraCosmetics.getPlayerManager();
    }

    @Override
    public void run() {
        for (UltraPlayer ultraPlayer : pm.getUltraPlayers()) {
            Player player = ultraPlayer.getBukkitPlayer();
            if (player == null) continue;
            if (PlayerAffectingCosmetic.isVanished(player) && ultraPlayer.hasCosmeticsEquipped()) {
                MessageManager.send(player, "Not-Allowed-In-Vanish");
                ultraCosmetics.getScheduler().runAtEntity(player, t -> {
                    ultraPlayer.clear(false);
                });
            }
        }
    }

    @Override
    public void schedule() {
        task = getScheduler().runTimerAsync(this::run, 100, 100);
    }
}
