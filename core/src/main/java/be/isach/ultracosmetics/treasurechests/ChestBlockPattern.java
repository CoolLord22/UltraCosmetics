package be.isach.ultracosmetics.treasurechests;

// x(true) and o(false) constants

import org.bukkit.Location;

import java.util.function.Consumer;

import static be.isach.ultracosmetics.cosmetics.particleeffects.ParticleEffectAngelWings.o;
import static be.isach.ultracosmetics.cosmetics.particleeffects.ParticleEffectAngelWings.x;

public enum ChestBlockPattern {
    CENTER_BLOCK(new boolean[][]{
            {x}
    }),
    AROUND_CENTER(new boolean[][]{
            {x, x, x},
            {x, o, x},
            {x, x, x}
    }),
    CORNERS(new boolean[][]{
            {x, x, o, x, x},
            {x, o, o, o, x},
            {o, o, o, o, o},
            {x, o, o, o, x},
            {x, x, o, x, x}
    }),
    BELOW_CHEST(new boolean[][]{
            {o, o, x, o, o},
            {o, o, o, o, o},
            {x, o, o, o, x},
            {o, o, o, o, o},
            {o, o, x, o, o}
    }),

    LARGE_AROUND_AROUND(new boolean[][]{
            {x, x, x, x, x},
            {x, o, o, o, x},
            {x, o, o, o, x},
            {x, o, o, o, x},
            {x, x, x, x, x}
    }),
    LARGE_CORNERS(new boolean[][]{
            {x, x, o, o, o, x, x},
            {x, o, o, o, o, o, x},
            {o, o, o, o, o, o, o},
            {o, o, o, o, o, o, o},
            {o, o, o, o, o, o, o},
            {x, o, o, o, o, o, x},
            {x, x, o, o, o, x, x}
    }),
    LARGE_BELOW_CHEST(new boolean[][]{
            {o, o, x, x, x, o, o},
            {o, o, o, o, o, o, o},
            {x, o, o, o, o, o, x},
            {x, o, o, o, o, o, x},
            {x, o, o, o, o, o, x},
            {o, o, o, o, o, o, o},
            {o, o, x, x, x, o, o}
    })
    ;

    private final boolean[][] positions;
    private final int middle;
    private ChestBlockPattern(boolean[][] positions) {
        this.positions = positions;
        this.middle = positions.length / 2;
    }

    public void loop(Location center, Consumer<Location> func) {
        for (int i = 0; i < positions.length; i++) {
            for (int j = 0; j < positions.length; j++) {
                if (positions[i][j]) {
                    func.accept(center.clone().add(i - middle, 0, j - middle));
                }
            }
        }
    }
}
