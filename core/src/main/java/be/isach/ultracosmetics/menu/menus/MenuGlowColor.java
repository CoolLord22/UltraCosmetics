package be.isach.ultracosmetics.menu.menus;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.type.GlowColorType;
import be.isach.ultracosmetics.menu.CosmeticMenu;

/**
 * GlowColor {@link be.isach.ultracosmetics.menu.Menu Menu}.
 *
 * @author iSach
 * @since 08-23-2016
 */
public class MenuGlowColor extends CosmeticMenu<GlowColorType> {

    public MenuGlowColor(UltraCosmetics ultraCosmetics) {
        super(ultraCosmetics, Category.GLOW_COLOR);
    }
}
