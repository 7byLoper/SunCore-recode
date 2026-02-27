package ru.loper.suncore.listeners;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import ru.loper.suncore.config.CoreConfigManager;

@RequiredArgsConstructor
public class PhysicsListener implements Listener {
    private final CoreConfigManager configManager;

    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent event) {
        World world = event.getBlock().getWorld();

        if (!configManager.isPhysicsDisabled(world)) {
            return;
        }

        Material changedType = event.getChangedType();
        if (isGravityBlock(changedType)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) {
        World world = event.getBlock().getWorld();

        if (!configManager.isPhysicsDisabled(world)) {
            return;
        }

        Block block = event.getBlock();
        if (block.isLiquid() || block.getType() == Material.DRAGON_EGG) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent event) {
        World world = event.getBlock().getWorld();

        if (configManager.isPhysicsDisabled(world)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockSpread(BlockSpreadEvent event) {
        World world = event.getBlock().getWorld();

        if (configManager.isPhysicsDisabled(world)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        World world = event.getBlock().getWorld();

        if (!configManager.isPhysicsDisabled(world)) {
            return;
        }

        switch (event.getEntity().getType()) {
            case FALLING_BLOCK:
            case ENDERMAN:
                event.setCancelled(true);
                break;
            default:
                break;

        }
    }

    private boolean isGravityBlock(Material material) {
        return switch (material) {
            case SAND, RED_SAND, GRAVEL, ANVIL, CHIPPED_ANVIL, DAMAGED_ANVIL, DRAGON_EGG -> true;
            default -> false;
        };
    }
}