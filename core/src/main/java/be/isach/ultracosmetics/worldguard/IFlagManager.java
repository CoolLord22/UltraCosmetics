package be.isach.ultracosmetics.worldguard;

import be.isach.ultracosmetics.cosmetics.Category;
import org.bukkit.entity.Player;

import java.util.Set;

public interface IFlagManager {
    public void register();

    public void registerPhase2();

    public boolean flagCheck(UCFlag flag, Player player);

    public Set<Category> categoryFlagCheck(Player player);
}
