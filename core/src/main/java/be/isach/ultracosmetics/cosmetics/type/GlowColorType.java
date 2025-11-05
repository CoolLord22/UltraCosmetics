package be.isach.ultracosmetics.cosmetics.type;

import be.isach.ultracosmetics.config.CustomConfiguration;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.commands.GlowColor;
import be.isach.ultracosmetics.util.ItemFactory;
import com.cryptomorin.xseries.XMaterial;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class GlowColorType extends CosmeticType<GlowColor> {
    private static final String[] colors = new String[] {"black", "dark_blue", "dark_green", "dark_aqua", "dark_red", "dark_purple",
            "gold", "dark_gray", "blue", "green", "aqua", "red", "light_purple", "yellow", "gray", "white"};
    private final String command;
    private final ItemStack chestplate;

    public GlowColorType(String configName) {
        super(Category.GLOW_COLOR, configName, XMaterial.LEATHER_CHESTPLATE, GlowColor.class);
        NamedTextColor color = NamedTextColor.NAMES.value(configName);
        this.command = "customcosmetic glowcolor %uuid% " + configName;
        this.chestplate = color != null ? ItemFactory.createColouredLeather(Material.LEATHER_CHESTPLATE, color.red(), color.green(), color.blue()) : XMaterial.LEATHER_CHESTPLATE.parseItem();
        if (GENERATE_MISSING_MESSAGES) {
            MessageManager.addMessage(getConfigPath() + ".name", "<red>" + configName + " Glow Color");
            MessageManager.addMessage(getConfigPath() + ".description", "<white>Visible in hubs and lobbies.");
        }
    }

    @Override
    public ItemStack getItemStack() {
        return chestplate.clone();
    }

    public String getCommand() {
        return command;
    }

    public static void register() {
        Arrays.stream(colors).forEach(GlowColorType::new);
    }

    @Override
    public void setupConfig(CustomConfiguration config, String path) {
        config.addDefault(path + ".Enabled", true);
        config.addDefault(path + ".Show-Description", true, "Whether to show description when hovering in GUI");
        config.addDefault(path + ".Treasure-Chest-Weight", 0, "The higher the weight, the better the chance of", "finding this cosmetic when this category is picked.", "Fractional values are not allowed.", "Set to 0 to disable finding in chests.");
        config.addDefault(path + ".Purchase-Price", 0, "Price to buy individually in GUI", "Only works if No-Permission.Allow-Purchase is true and this setting > 0");
    }
}
