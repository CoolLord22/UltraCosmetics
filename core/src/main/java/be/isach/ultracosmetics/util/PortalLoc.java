package be.isach.ultracosmetics.util;

import com.cryptomorin.xseries.particles.ParticleDisplay;
import com.cryptomorin.xseries.particles.XParticle;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;

import java.awt.Color;

public class PortalLoc {
    private final ParticleDisplay particle;
    private Location location;
    private BlockFace face;

    public PortalLoc(int red, int green, int blue) {
        this.particle = ParticleDisplay.of(XParticle.DUST).withColor(new Color(red, green, blue));
    }

    public Location getLocation() {
        return location;
    }

    public BlockFace getFace() {
        return face;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setFace(BlockFace face) {
        this.face = face;
    }

    public boolean isValid() {
        return location != null && face != null;
    }

    public void clear() {
        location = null;
        face = null;
    }

    public ParticleDisplay getParticle() {
        return particle.copy();
    }
}
