package be.isach.ultracosmetics.v1_20_R4.customentities;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.level.Level;

public class CustomMinecart extends Minecart {
    public CustomMinecart(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Override
    public float maxUpStep() {
        return 1.0F;
    }
}
