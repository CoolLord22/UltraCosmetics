package be.isach.ultracosmetics.paper;

import be.isach.ultracosmetics.config.MessageManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Nameable;
import org.bukkit.entity.Player;

public interface PaperSupport {
    boolean hasParticlesDisabled(Player player);

    void setCustomName(Nameable nameable, String serializedComponent);

    default void setCustomName(Nameable nameable, Component component) {
        setCustomName(nameable, MessageManager.getMiniMessage().serialize(component));
    }
}
