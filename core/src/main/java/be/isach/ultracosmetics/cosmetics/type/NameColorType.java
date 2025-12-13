package be.isach.ultracosmetics.cosmetics.type;

import be.isach.ultracosmetics.config.CustomConfiguration;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.custom.NameColor;
import com.cryptomorin.xseries.XMaterial;

import java.util.Arrays;

public class NameColorType extends CosmeticType<NameColor> {
    private static final String[] colors = new String[] {"black", "dark_blue", "dark_green", "dark_aqua", "dark_red", "dark_purple",
            "gold", "dark_gray", "blue", "green", "aqua", "red", "light_purple", "yellow", "gray", "white"};
    private final String command;

    public NameColorType(String configName) {
        super(Category.NAME_COLOR, configName, XMaterial.PLAYER_HEAD, NameColor.class);
        this.command = "customcosmetic namecolor %uuid% " + configName;
        if (GENERATE_MISSING_MESSAGES) {
            MessageManager.addMessage(getConfigPath() + ".name", "<blue>" + configName + " Name Color");
            MessageManager.addMessage(getConfigPath() + ".description", "<white>Only visible when chatting.");
        }
    }

    public String getCommand() {
        return command;
    }

    public static void register() {
        Arrays.stream(colors).forEach(NameColorType::new);
    }

    @Override
    public void setupConfig(CustomConfiguration config, String path) {
        config.addDefault(path + ".Enabled", true);
        config.addDefault(path + ".Show-Description", true, "Whether to show description when hovering in GUI");
        config.addDefault(path + ".Treasure-Chest-Weight", 0, "The higher the weight, the better the chance of", "finding this cosmetic when this category is picked.", "Fractional values are not allowed.", "Set to 0 to disable finding in chests.");
        config.addDefault(path + ".Purchase-Price", 0, "Price to buy individually in GUI", "Only works if No-Permission.Allow-Purchase is true and this setting > 0");
    }
}
