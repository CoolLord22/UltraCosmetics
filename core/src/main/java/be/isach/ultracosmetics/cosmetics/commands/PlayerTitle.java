package be.isach.ultracosmetics.cosmetics.commands;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.CommandCosmetic;
import be.isach.ultracosmetics.cosmetics.type.PlayerTitleType;
import be.isach.ultracosmetics.player.UltraPlayer;

public class PlayerTitle extends CommandCosmetic<PlayerTitleType> {
    public PlayerTitle(UltraPlayer owner, PlayerTitleType type, UltraCosmetics ultraCosmetics) {
        super(owner, type, ultraCosmetics);
        command = type.getCommand();
    }

    @Override
    protected String getResetCommand() {
        return "playertitle reset";
    }
}
