package com.gmail.uprial.customnukes.listeners;

import com.gmail.uprial.customnukes.CustomNukes;
import com.gmail.uprial.customnukes.common.CustomLogger;
import com.gmail.uprial.customnukes.schema.EItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class ExplosivesActivateListener implements Listener {
    private final CustomNukes plugin;
    private final CustomLogger customLogger;

    public ExplosivesActivateListener(CustomNukes plugin, CustomLogger customLogger) {
        this.plugin = plugin;
        this.customLogger = customLogger;
    }

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(!event.useItemInHand().equals(Event.Result.DENY) && !event.useInteractedBlock().equals(Event.Result.DENY)) {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (event.getMaterial() == Material.FLINT_AND_STEEL) {
                    if(try_activate(event.getClickedBlock())) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockRedstone(BlockRedstoneEvent event) {
        Block block = event.getBlock();
        if(block != null) {
            if(block.getType() == Material.REDSTONE_WIRE) {
                try_activate(block.getRelative(0, -1, 0));
            }
        }
    }

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityExplode(EntityExplodeEvent event) {
        if(!event.isCancelled()) {
            for (Block block : event.blockList()) {
                try_activate(block);
            }
        }
    }

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockExplodeEvent(BlockExplodeEvent event) {
        if(!event.isCancelled()) {
            for (Block block : event.blockList()) {
                try_activate(block);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockHitByFireEvent(ProjectileHitEvent event) {
        if(event.getEntity().getFireTicks() > 0) {
            Block block = event.getHitBlock();
            if (block != null) {
                try_activate(block);
            }
        }
    }

    @SuppressWarnings("BooleanMethodNameMustStartWithQuestion")
    private boolean try_activate(Block block) {
        if(block == null) {
            return false;
        }
        if(!plugin.getExplosivesConfig().isRegisteredMaterial(block.getType())) {
            return false;
        }

        String name = plugin.getBlockMetaStorage().get(block, ExplosivesBlocksListener.BLOCK_META_KEY);
        if(name == null) {
            return false;
        }
        EItem explosive = plugin.getExplosivesConfig().searchExplosiveByName(name);
        if(explosive == null) {
            return false;
        }

        block.setType(Material.AIR);
        plugin.getBlockMetaStorage().delete(block, ExplosivesBlocksListener.BLOCK_META_KEY);
        Location location = new Location(block.getWorld(), block.getX() + 0.5, block.getY() + 0.5, block.getZ() + 0.5);
        customLogger.debug(String.format("Explode '%s' at %s:%d:%d:%d",
                                         explosive.getName(), block.getWorld().getName(), block.getX(), block.getY(), block.getZ()));
        explosive.explode(plugin, location);
        return true;
    }
}
