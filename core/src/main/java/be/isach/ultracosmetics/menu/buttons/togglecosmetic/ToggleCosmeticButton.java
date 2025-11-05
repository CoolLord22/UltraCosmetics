package be.isach.ultracosmetics.menu.buttons.togglecosmetic;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.type.CosmeticType;
import be.isach.ultracosmetics.menu.buttons.CosmeticButton;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.ItemFactory;
import be.isach.ultracosmetics.util.LazyTag;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ToggleCosmeticButton extends CosmeticButton {
    private final boolean showPermissionInLore = SettingsManager.getConfig().getBoolean("No-Permission.Show-In-Lore");

    public ToggleCosmeticButton(UltraCosmetics ultraCosmetics, CosmeticType<?> cosmeticType) {
        super(ultraCosmetics, cosmeticType, false);
    }

    @Override
    protected ItemStack getBaseItem(UltraPlayer ultraPlayer) {
        Component toggle = cosmeticType.getCategory().getActivateTooltip();

        boolean deactivate = ultraPlayer.hasCosmetic(cosmeticType.getCategory()) && ultraPlayer.getCosmetic(cosmeticType.getCategory()).getType() == cosmeticType;

        if (deactivate) {
            toggle = cosmeticType.getCategory().getDeactivateTooltip();
        }
        TagResolver.Single tooltipResolver = TagResolver.resolver("tooltip", Tag.inserting(toggle));

        Component name = cosmeticType.getName();
        name = modifyName(name, ultraPlayer);
        ItemStack stack = ItemFactory.rename(cosmeticType.getItemStack(), name);
        if (deactivate) {
            ItemFactory.addGlow(stack);
        }

        ItemMeta meta = stack.getItemMeta();
        List<String> loreList = new ArrayList<>();
        if (cosmeticType.showsDescription()) {
            loreList.add("");
            loreList.addAll(cosmeticType.getDescription());
        }
        modifyLore(loreList, ultraPlayer);

        if (showPermissionInLore) {
            loreList.add("");
            Component permissionLore;
            if (ultraCosmetics.getPermissionManager().hasPermission(ultraPlayer, cosmeticType)) {
                permissionLore = MessageManager.getMessage("Permission-Lore.Permission-Yes", tooltipResolver);
            } else if (ultraCosmetics.getWorldGuardManager().isInShowroom(ultraPlayer.getBukkitPlayer())) {
                permissionLore = MessageManager.getMessage("Permission-Lore.Showroom", tooltipResolver);
            } else {
                permissionLore = MessageManager.getMessage("Permission-Lore.Permission-No", tooltipResolver);
            }
            loreList.add(MessageManager.toLegacy(permissionLore));
        }
        meta.setLore(loreList);
        meta = modifyMeta(meta, ultraPlayer);
        stack.setItemMeta(meta);
        ItemFactory.applyTooltipMarker(stack, PlainTextComponentSerializer.plainText().serialize(toggle));
        stack.setAmount(getAmount(ultraPlayer));
        return stack;
    }

    /**
     * Change the display name being applied to the item, i.e. append to it
     *
     * @param base the current item name
     * @return the new item name
     */
    protected Component modifyName(Component base, UltraPlayer ultraPlayer) {
        return base;
    }

    /**
     * Change the lore that's going to be applied to the item.
     * Changes should be made directly to the list.
     *
     * @param lore the current lore
     */
    protected void modifyLore(List<String> lore, UltraPlayer ultraPlayer) {
    }

    /**
     * Change anything else about the item meta, OTHER THAN name or lore.
     * Changes should be made directly to the ItemMeta given.
     *
     * @param meta the current meta
     */
    protected ItemMeta modifyMeta(ItemMeta meta, UltraPlayer ultraPlayer) {
        return meta;
    }

    /**
     * Changes the amount the item should appear as
     *
     * @return the new amount
     */
    protected int getAmount(UltraPlayer ultraPlayer) {
        return 1;
    }
}
