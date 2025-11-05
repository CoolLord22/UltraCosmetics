package be.isach.ultracosmetics.permissions;

import be.isach.ultracosmetics.cosmetics.type.CosmeticType;
import org.bukkit.entity.Player;

import java.util.Set;

public interface CosmeticPermissionSetter {
    public void setPermissions(Player player, Set<CosmeticType<?>> types);

    public boolean isUnsetSupported();

    public void unsetPermissions(Player player, Set<CosmeticType<?>> types);
}
