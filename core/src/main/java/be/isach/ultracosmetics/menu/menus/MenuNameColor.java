package be.isach.ultracosmetics.menu.menus;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.type.NameColorType;
import be.isach.ultracosmetics.menu.CosmeticMenu;

/**
 * Namecolor {@link be.isach.ultracosmetics.menu.Menu Menu}.
 *
 * @author iSach
 * @since 08-23-2016
 */
public class MenuNameColor extends CosmeticMenu<NameColorType> {

    public MenuNameColor(UltraCosmetics ultraCosmetics) {
        super(ultraCosmetics, Category.NAME_COLOR);
    }
}
