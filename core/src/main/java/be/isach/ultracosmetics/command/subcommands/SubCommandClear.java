package be.isach.ultracosmetics.command.subcommands;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.command.SubCommand;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.player.UltraPlayer;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Clear {@link be.isach.ultracosmetics.command.SubCommand SubCommand}.
 *
 * @author iSach
 * @since 12-22-2015
 */
public class SubCommandClear extends SubCommand {

    public SubCommandClear(UltraCosmetics ultraCosmetics) {
        super("clear", "Clears a Cosmetic.", "<player> [type]", ultraCosmetics, true);
    }

    @Override
    protected void onExeAnyone(CommandSender sender, String[] args) {
        Player target;
        if (args.length < 2) {
            if (sender instanceof Player) {
                target = (Player) sender;
            } else {
                error(sender, "You must specify a player.");
                return;
            }
        } else {
            target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                error(sender, "Player " + args[1] + " not found!");
                return;
            }
        }

        if (target != sender && !sender.hasPermission(getPermission() + ".others")) {
            error(sender, "You do not have permission to clear others.");
            return;
        }

        if (args.length < 3) {
            ultraCosmetics.getPlayerManager().getUltraPlayer(target).clear(false);
            return;
        }

        UltraPlayer up = ultraCosmetics.getPlayerManager().getUltraPlayer(target);

        Category cat = Category.fromString(args[2]);
        if (cat == null) {
            error(sender, "Invalid cosmetic type.");
            return;
        }
        up.removeCosmetic(cat);
    }

    @Override
    protected void tabComplete(CommandSender sender, String[] args, List<String> options) {
        if (args.length == 2) {
            addPlayers(options);
        } else if (args.length == 3) {
            addCategories(options);
        }
    }
}
