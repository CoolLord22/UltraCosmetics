package be.isach.ultracosmetics.cosmetics.suits;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.SuitType;
import be.isach.ultracosmetics.player.UltraPlayer;
import com.cryptomorin.xseries.XMaterial;

import java.util.List;

public class SuitSanta extends TrailSuit {
    public SuitSanta(UltraPlayer ultraPlayer, SuitType suitType, UltraCosmetics ultraCosmetics) {
        super(ultraPlayer, suitType, ultraCosmetics);
    }

    @Override
    protected List<XMaterial> getTrailBlocks() {
        return List.of(XMaterial.RED_WOOL, XMaterial.LIME_WOOL);
    }
}
