package be.isach.ultracosmetics.command.subcommands;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.command.SubCommand;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.cosmetics.type.CosmeticType;
import be.isach.ultracosmetics.permissions.GrantSource;
import be.isach.ultracosmetics.permissions.PermissionManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SubCommandWhyDoIHave extends SubCommand {
    public SubCommandWhyDoIHave(UltraCosmetics ultraCosmetics) {
        super("whydoihave", "Check what is granting you or another player access to a given cosmetic",
                "<category> <cosmetic> [player]", ultraCosmetics);
    }

    @Override
    protected void onExePlayer(Player sender, String[] args) {
        Player target;
        if (args.length == 3) {
            target = sender;
        } else if (args.length == 4) {
            target = Bukkit.getPlayer(args[3]);
            if (target == null) {
                error(sender, "Invalid player");
                return;
            }
        } else {
            badUsage(sender);
            return;
        }

        if (!SettingsManager.isAllowedWorld(target.getWorld())) {
            sender.sendMessage("No cosmetics granted because world is disabled");
            return;
        }

        if (ultraCosmetics.getWorldGuardManager().isInShowroom(sender)) {
            sender.sendMessage("All cosmetics granted by showroom region");
            return;
        }

        Category cat = Category.fromString(args[1]);
        if (cat == null) {
            error(sender, "Invalid category: " + args[1]);
            return;
        }

        CosmeticType<?> type = cat.valueOfType(args[2]);
        if (type == null) {
            error(sender, "Invalid cosmetic: " + args[2]);
            return;
        }

        PermissionManager pm = ultraCosmetics.getPermissionManager();
        GrantSource source = pm.getGrantSource(target, type);
        if (source == null) {
            sender.sendMessage("Cosmetic is locked");
        } else if (source == GrantSource.PROFILE) {
            String message = "Cosmetic is granted by UC persistent data (";
            if (UltraCosmeticsData.get().usingFileStorage()) {
                message += "plugins/UltraCosmetics/data/";
            } else {
                message += "MySQL database";
            }
            message += ")";
            sender.sendMessage(message);
        } else if (source == GrantSource.PERMISSION) {
            sender.sendMessage("Cosmetic is granted by permissions (" + type.getPermission().getName() + ")");
        }
    }

    @Override
    protected void onExeAnyone(CommandSender sender, String[] args) {
        notAllowed(sender);
    }

    @Override
    protected void tabComplete(CommandSender sender, String[] args, List<String> options) {
        if (args.length == 2) {
            addCategories(options);
        } else if (args.length == 3) {
            Category cat = Category.fromString(args[1]);

            if (cat == null || !cat.isEnabled()) return;

            for (CosmeticType<?> cosm : cat.getEnabled()) {
                options.add(cosm.toString());
            }
        } else if (args.length == 4) {
            addPlayers(options);
        }
    }
}
