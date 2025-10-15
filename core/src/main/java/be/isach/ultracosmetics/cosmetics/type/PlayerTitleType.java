package be.isach.ultracosmetics.cosmetics.type;

import be.isach.ultracosmetics.config.CustomConfiguration;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.commands.PlayerTitle;
import com.cryptomorin.xseries.XMaterial;

import java.util.Arrays;

public class PlayerTitleType extends CosmeticType<PlayerTitle> {
    private static final String keys =
            "bs_milestone_100blocksfound,bs_milestone_level10,bs_milestone_10wins," + // Common
            "bs_milestone_500blocksfound,bs_milestone_level25,bs_milestone_50wins," + // Uncommon
            "bs_milestone_1000blocksfound,bs_milestone_level50,bs_milestone_150wins," + // Rare
            "bs_milestone_2500blocksfound,bs_milestone_level100,bs_milestone_300wins," + // Epic
            "bs_milestone_5000blocksfound,bs_milestone_level250,bs_milestone_600wins," + // Legendary
            "bs_milestone_10000blocksfound,bs_milestone_level500,bs_milestone_1000wins," + // Mythic
            "betatester"; // Extra
    private static final String[] titlename = keys.split(",");
    private final String command;

    public PlayerTitleType(String configName) {
        super(Category.PLAYER_TITLE, configName, XMaterial.NAME_TAG, PlayerTitle.class);
        this.command = "playertitle " + configName;
        if (GENERATE_MISSING_MESSAGES) {
            MessageManager.addMessage(getConfigPath() + ".name", "<gray>[]");
            MessageManager.addMessage(getConfigPath() + ".description", "\"<white>500 Blocks Found \\n<dark_gray>ᴜɴᴄᴏᴍᴍᴏɴ\"");
        }
    }

    public String getCommand() {
        return command;
    }

    public static void register() {
        Arrays.stream(titlename).forEach(PlayerTitleType::new);
    }

    @Override
    public void setupConfig(CustomConfiguration config, String path) {
        config.addDefault(path + ".Enabled", true);
        config.addDefault(path + ".Show-Description", true, "Whether to show description when hovering in GUI");
        config.addDefault(path + ".Treasure-Chest-Weight", 0, "The higher the weight, the better the chance of", "finding this cosmetic when this category is picked.", "Fractional values are not allowed.", "Set to 0 to disable finding in chests.");
        config.addDefault(path + ".Purchase-Price", 0, "Price to buy individually in GUI", "Only works if No-Permission.Allow-Purchase is true and this setting > 0");
    }
}
