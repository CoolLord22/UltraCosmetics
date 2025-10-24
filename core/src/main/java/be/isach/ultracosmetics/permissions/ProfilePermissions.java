package be.isach.ultracosmetics.permissions;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.type.CosmeticType;
import be.isach.ultracosmetics.player.UltraPlayerManager;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class ProfilePermissions implements CosmeticPermissionGetter, CosmeticPermissionSetter {
    private final UltraPlayerManager pm;

    public ProfilePermissions(UltraCosmetics ultraCosmetics) {
        this.pm = ultraCosmetics.getPlayerManager();
    }

    @Override
    public void setPermissions(Player player, Set<CosmeticType<?>> types) {
        pm.getUltraPlayer(player).getProfile().setUnlocked(types);
    }

    @Override
    public void unsetPermissions(Player player, Set<CosmeticType<?>> types) {
        pm.getUltraPlayer(player).getProfile().setLocked(types);
    }

    @Override
    public GrantSource getGrantSource(Player player, CosmeticType<?> type) {
        return pm.getUltraPlayer(player).getProfile().hasUnlocked(type) ? GrantSource.PROFILE : null;
    }

    @Override
    public Set<CosmeticType<?>> getEnabledUnlocked(Player player) {
        return pm.getUltraPlayer(player).getProfile().getAllUnlocked();
    }

    @Override
    public Set<CosmeticType<?>> getEnabledUnlocked(Player player, Category cat) {
        Set<CosmeticType<?>> types = new HashSet<>();
        pm.getUltraPlayer(player).getProfile().getAllUnlocked().stream().filter(t -> t.getCategory() == cat && t.isEnabled()).forEach(types::add);
        return types;
    }

    @Override
    public boolean isUnsetSupported() {
        return true;
    }
}
