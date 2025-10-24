package be.isach.ultracosmetics.cosmetics.suits;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.type.SuitType;
import be.isach.ultracosmetics.player.UltraPlayer;
import com.cryptomorin.xseries.XMaterial;

import java.util.List;

public class SuitDiamond extends TrailSuit {
    public SuitDiamond(UltraPlayer ultraPlayer, SuitType suitType, UltraCosmetics ultraCosmetics) {
        super(ultraPlayer, suitType, ultraCosmetics);
    }

    @Override
    protected List<XMaterial> getTrailBlocks() {
        return List.of(XMaterial.DIAMOND_BLOCK);
    }
}
