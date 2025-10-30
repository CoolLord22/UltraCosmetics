package be.isach.ultracosmetics.cosmetics.commands;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.CommandCosmetic;
import be.isach.ultracosmetics.cosmetics.type.NameColorType;
import be.isach.ultracosmetics.player.UltraPlayer;

public class NameColor extends CommandCosmetic<NameColorType> {
    public NameColor(UltraPlayer owner, NameColorType type, UltraCosmetics ultraCosmetics) {
        super(owner, type, ultraCosmetics);
        command = type.getCommand();
    }

    @Override
    protected String getResetCommand() {
        return "customcosmetic namecolor %uuid% reset";
    }
}
