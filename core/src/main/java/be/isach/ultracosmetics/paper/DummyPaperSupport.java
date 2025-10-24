package be.isach.ultracosmetics.paper;

import be.isach.ultracosmetics.config.MessageManager;
import org.bukkit.Nameable;
import org.bukkit.entity.Player;

public class DummyPaperSupport implements PaperSupport {
    @Override
    public boolean hasParticlesDisabled(Player player) {
        return false;
    }

    @Override
    public void setCustomName(Nameable nameable, String serializedComponent) {
        nameable.setCustomName(MessageManager.toLegacy(MessageManager.getMiniMessage().deserialize(serializedComponent)));
    }
}
