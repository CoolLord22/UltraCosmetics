package be.isach.ultracosmetics.menu.buttons.togglecosmetic;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.type.EmoteType;
import be.isach.ultracosmetics.cosmetics.type.NameColorType;
import be.isach.ultracosmetics.player.UltraPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ToggleNameColorCosmeticButton extends ToggleCosmeticButton {
    public ToggleNameColorCosmeticButton(UltraCosmetics ultraCosmetics, NameColorType cosmeticType) {
        super(ultraCosmetics, cosmeticType);
    }

    @Override
    protected void modifyLore(List<String> lore, UltraPlayer ultraPlayer) {
        NameColorType nameColorType = (NameColorType) cosmeticType;
        String text = "<gray>Preview: <%s>%s".formatted(nameColorType.getConfigName(), ultraPlayer.getBukkitPlayer().getName());
        lore.add(MessageManager.toLegacy(MessageManager.getMiniMessage().deserialize(text)));
    }
}
