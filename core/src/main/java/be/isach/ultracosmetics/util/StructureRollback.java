package be.isach.ultracosmetics.util;

import be.isach.ultracosmetics.UltraCosmeticsData;
import com.cryptomorin.xseries.XBlock;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.*;

public class StructureRollback implements Listener {
    private static final Set<StructureRollback> INSTANCES = new HashSet<>();

    public static boolean isBlockRollingBack(Block block) {
        for (StructureRollback rollback : INSTANCES) {
            if (rollback.containsBlock(block)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if any block in the area is being rolled back
     * OR if any rollback overlaps with the area.
     *
     * @param area The area to check.
     * @return true if any rollback overlaps with the area.
     */
    public static boolean isBlockRollingBackInArea(Area area) {
        for (StructureRollback rollback : INSTANCES) {
            if (rollback.protectionArea != null) {
                return rollback.protectionArea.overlapsWith(area);
            }
            for (Block block : rollback.states.keySet()) {
                if (area.contains(block)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    private final HashMap<Block, BlockState> states = new LinkedHashMap<>();
    private Area protectionArea;

    public StructureRollback() {
        this(null);
    }

    public StructureRollback(Area protectionArea) {
        this.protectionArea = protectionArea;
        Bukkit.getPluginManager().registerEvents(this, UltraCosmeticsData.get().getPlugin());
        INSTANCES.add(this);
    }

    public boolean containsBlock(Block block) {
        return states.containsKey(block);
    }

    public void setToRestore(Block block, XMaterial newType) {
        setToRestore(block, newType, true);
    }

    public void setToRestore(Block block, Material newType) {
        setToRestore(block, newType, true);
    }

    public void setToRestore(Block block, XMaterial newType, boolean applyPhysics) {
        states.put(block, block.getState());
        XBlock.setType(block, newType, applyPhysics);
    }

    public void setToRestore(Block block, Material newType, boolean applyPhysics) {
        states.put(block, block.getState());
        block.setType(newType, applyPhysics);
    }

    public Area getProtectionArea() {
        return protectionArea;
    }

    public void updateProtectionArea(Area area) {
        this.protectionArea = area;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (states.containsKey(event.getBlock())) {
            event.setCancelled(true);
        }
    }

    // Prevents blocks falling into chest area
    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (protectionArea != null && protectionArea.contains(event.getBlock())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        event.blockList().removeAll(states.keySet());
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().removeAll(states.keySet());
    }

    public void rollback() {
        ListIterator<BlockState> iter = new ArrayList<>(states.values()).listIterator(states.size());
        while (iter.hasPrevious()) {
            iter.previous().update(true);
        }
        states.clear();
    }

    public void cleanup() {
        rollback();
        HandlerList.unregisterAll(this);
        INSTANCES.remove(this);
    }
}
