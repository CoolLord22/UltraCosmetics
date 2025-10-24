package be.isach.ultracosmetics.permissions;

import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.type.CosmeticType;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public interface CosmeticPermissionGetter {
    public GrantSource getGrantSource(Player player, CosmeticType<?> type);

    public default boolean hasPermission(Player player, CosmeticType<?> type) {
        return getGrantSource(player, type) != null;
    }

    public default Set<CosmeticType<?>> getEnabledUnlocked(Player player) {
        Set<CosmeticType<?>> types = new HashSet<>();
        for (Category cat : Category.enabled()) {
            types.addAll(getEnabledUnlocked(player, cat));
        }
        return types;
    }

    public default Set<CosmeticType<?>> getEnabledUnlocked(Player player, Category cat) {
        Set<CosmeticType<?>> types = new HashSet<>();
        for (CosmeticType<?> type : cat.getEnabled()) {
            if (hasPermission(player, type)) {
                types.add(type);
            }
        }
        return types;
    }
}
