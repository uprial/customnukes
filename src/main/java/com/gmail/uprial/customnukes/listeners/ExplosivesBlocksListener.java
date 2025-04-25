package com.gmail.uprial.customnukes.listeners;

import com.gmail.uprial.customnukes.CustomNukes;
import com.gmail.uprial.customnukes.common.CustomLogger;
import com.gmail.uprial.customnukes.schema.EItem;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.List;

import static com.gmail.uprial.customnukes.common.Formatter.format;

public class ExplosivesBlocksListener implements Listener {
    private final CustomNukes plugin;
    private final CustomLogger customLogger;

    public ExplosivesBlocksListener(CustomNukes plugin, CustomLogger customLogger) {
        this.plugin = plugin;
        this.customLogger = customLogger;
    }

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPlace(BlockPlaceEvent event) {
        if(!event.isCancelled()) {
            EItem explosive = plugin.getExplosivesConfig().searchExplosiveByItemStack(event.getItemInHand());
            if(explosive != null) {
                Player player = event.getPlayer();
                if (explosive.hasPermission(player)) {
                    Block block = event.getBlock();
                    customLogger.debug(String.format("Place '%s' at %s", explosive.getName(), format(block)));
                    plugin.getExplosiveBlockStorage().setExplosive(block, explosive);
                } else {
                    event.setCancelled(true);
                    CustomLogger userLogger = new CustomLogger(plugin.getLogger(), player);
                    userLogger.error("You don't have permissions to place this type of block.");
                }
            } else {
                plugin.getExplosiveBlockStorage().deleteExplosive(event.getBlock());
            }
        }
    }

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockBreak(BlockBreakEvent event) {
        if(!event.isCancelled()) {
            Block block = event.getBlock();
            EItem explosive = plugin.getExplosiveBlockStorage().searchExplosiveByBlock(block);
            if(explosive != null) {
                Player player = event.getPlayer();
                if (explosive.hasPermission(player)) {
                    customLogger.debug(String.format("Break '%s' at %s", explosive.getName(), format(block)));
                    plugin.getExplosiveBlockStorage().deleteExplosive(block);

                    event.setCancelled(true);
                    block.setType(Material.AIR);
                    if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
                        block.getWorld().dropItemNaturally(block.getLocation(), explosive.getDroppedItemStack());
                    }
                } else {
                    event.setCancelled(true);
                    CustomLogger userLogger = new CustomLogger(plugin.getLogger(), player);
                    userLogger.error("You don't have permissions to break this type of block.");
                }
            }
        }
    }

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        if(!event.isCancelled()) {
            List<Block> blocks = event.getBlocks();
            for(Block block : blocks) {
                maybeMoveBlock(block, event.getDirection());
            }
        }
    }
    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        if(!event.isCancelled() && event.isSticky()) {
            List<Block> blocks = event.getBlocks();
            for(Block block : blocks) {
                maybeMoveBlock(block, event.getDirection());
            }
        }
    }

    private void maybeMoveBlock(Block block, BlockFace direction) {
        EItem explosive = plugin.getExplosiveBlockStorage().searchExplosiveByBlock(block);
        if(explosive != null) {
            Block blockInDirection = getBlockInDirection(block, direction);
            customLogger.debug(String.format("Move '%s' from %s to %s",
                    explosive.getName(), format(block), format(blockInDirection)));

            plugin.getExplosiveBlockStorage().deleteExplosive(block);
            plugin.getExplosiveBlockStorage().setExplosive(blockInDirection, explosive);
        }
        else {
            plugin.getExplosiveBlockStorage().deleteExplosive(getBlockInDirection(block, direction));
        }
    }

    private static Block getBlockInDirection(Block block, BlockFace direction) {
        return block.getWorld().getBlockAt(block.getX() + direction.getModX(),
                                            block.getY() + direction.getModY(),
                                            block.getZ() + direction.getModZ());
    }
}
