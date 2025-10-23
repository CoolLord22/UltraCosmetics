package be.isach.ultracosmetics.cosmetics;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.UltraCosmeticsData;
import be.isach.ultracosmetics.cosmetics.type.CosmeticType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.SmartLogger;
import org.bukkit.Bukkit;

public abstract class CommandCosmetic<T extends CosmeticType<?>> extends Cosmetic<T> {
    protected String command;

    public CommandCosmetic(UltraPlayer owner, T type, UltraCosmetics ultraCosmetics) {
        super(owner, type, ultraCosmetics);
    }

    protected abstract String getResetCommand();

    @Override
    public void onEquip() {
        runCommand(command);
    }

    @Override
    public void onClear() {
        runCommand(getResetCommand());
    }

    protected void runCommand(String command) {
        try {
            if(command.contains("%player%") || command.contains("%uuid%"))
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%uuid%", getPlayer().getUniqueId().toString()).replace("%player%", getPlayer().getName()));
            else
                Bukkit.dispatchCommand(getPlayer(), command);
        } catch (Exception ex) {
            UltraCosmeticsData.get().getPlugin().getSmartLogger().write(SmartLogger.LogLevel.WARNING, "Error dispatching command " + command, ex);
        }
    }
}
