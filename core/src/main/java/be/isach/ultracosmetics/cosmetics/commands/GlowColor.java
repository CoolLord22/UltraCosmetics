package be.isach.ultracosmetics.cosmetics.commands;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.CommandCosmetic;
import be.isach.ultracosmetics.cosmetics.type.GlowColorType;
import be.isach.ultracosmetics.player.UltraPlayer;

public class GlowColor extends CommandCosmetic<GlowColorType> {
    public GlowColor(UltraPlayer owner, GlowColorType type, UltraCosmetics ultraCosmetics) {
        super(owner, type, ultraCosmetics);
        command = type.getCommand();
    }

    @Override
    protected String getResetCommand() {
        return "mythrendcore glow reset";
    }
}
