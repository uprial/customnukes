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
import java.util.Random;

public class ExplosivesBlocksListener implements Listener {
    public static final String BLOCK_META_KEY = "explosive";

    private final CustomNukes plugin;
    private final Random random;
    private final CustomLogger customLogger;

    public ExplosivesBlocksListener(CustomNukes plugin, CustomLogger customLogger) {
        this.plugin = plugin;
        this.customLogger = customLogger;

        random = new Random();

        scheduleCleaning();
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
                    customLogger.debug(String.format("Place '%s' at %s:%d:%d:%d",
                            explosive.getName(), block.getWorld().getName(), block.getX(), block.getY(), block.getZ()));
                    setExplosive(block, explosive);
                } else {
                    event.setCancelled(true);
                    CustomLogger userLogger = new CustomLogger(plugin.getLogger(), player);
                    userLogger.error("You don't have permissions to place this type of block.");
                }
            } else {
                deleteExplosive(event.getBlock());
            }
        }
    }

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockBreak(BlockBreakEvent event) {
        if(!event.isCancelled()) {
            Block block = event.getBlock();
            EItem explosive = searchExplosiveByBlock(block);
            if(explosive != null) {
                Player player = event.getPlayer();
                if (explosive.hasPermission(player)) {
                    customLogger.debug(String.format("Break '%s' at %s:%d:%d:%d",
                            explosive.getName(), block.getWorld().getName(), block.getX(), block.getY(), block.getZ()));
                    deleteExplosive(block);

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

    private void onTaskMetaClean() {
        List<Block> blocks = plugin.getBlockMetaStorage().getAllBlocks();
        for(Block block : blocks) {
            if(!plugin.getExplosivesConfig().isRegisteredMaterial(block.getType())) {
                customLogger.info(String.format("Block '%s' at x=%d y=%d z=%d is not from the registered material. Meta will be deleted.",
                                                block.getType().toString(), block.getX(), block.getY(), block.getZ()));
                deleteExplosive(block);
            }
        }
    }

    private void setExplosive(Block block, EItem explosive) {
        plugin.getBlockMetaStorage().set(block, BLOCK_META_KEY, explosive.getName());
        maybeScheduleCleaning();
    }

    private void deleteExplosive(Block block) {
        plugin.getBlockMetaStorage().delete(block, BLOCK_META_KEY);
    }

    private EItem searchExplosiveByBlock(Block block) {
        if(plugin.getExplosivesConfig().isRegisteredMaterial(block.getType())) {
            String name = plugin.getBlockMetaStorage().get(block, BLOCK_META_KEY);
            return (name != null) ? plugin.getExplosivesConfig().searchExplosiveByName(name) : null;
        }
        else {
            return null;
        }
    }

    private void maybeMoveBlock(Block block, BlockFace direction) {
        EItem explosive = searchExplosiveByBlock(block);
        if(explosive != null) {
            Block blockInDirection = getBlockInDirection(block, direction);
            customLogger.debug(String.format("Move '%s' from %s:%d:%d:%d to %s:%d:%d:%d",
                    explosive.getName(),
                    block.getWorld().getName(), block.getX(), block.getY(), block.getZ(),
                    blockInDirection.getWorld().getName(), blockInDirection.getX(), blockInDirection.getY(), blockInDirection.getZ()));

            deleteExplosive(block);
            setExplosive(blockInDirection, explosive);
        }
        else {
            deleteExplosive(getBlockInDirection(block, direction));
        }
    }

    private static Block getBlockInDirection(Block block, BlockFace direction) {
        return block.getWorld().getBlockAt(block.getX() + direction.getModX(),
                                            block.getY() + direction.getModY(),
                                            block.getZ() + direction.getModZ());
    }

    private void maybeScheduleCleaning() {
        if(random.nextInt(10) == 0) {
            scheduleCleaning();
        }
    }

    private void scheduleCleaning() {
        plugin.scheduleDelayed(new Runnable() {
            @Override
            public void run() {
                onTaskMetaClean();
            }
        }, 100);
    }
}
