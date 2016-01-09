package com.gmail.uprial.customnukes;

import java.util.List;
import java.util.Random;

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

import com.gmail.uprial.customnukes.common.CustomLogger;
import com.gmail.uprial.customnukes.schema.EItem;

public class ExplosivesBlocksListener implements Listener {
	public static String blockMetaKey = "explosive";

	private final CustomNukes plugin;
	private final Random random;
	private final CustomLogger customLogger;
	
	public ExplosivesBlocksListener(CustomNukes plugin, CustomLogger customLogger) {
		this.plugin = plugin;
		this.customLogger = customLogger;
		
		random = new Random();
		
		scheduleCleaning();
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPlace(BlockPlaceEvent event) {
		if(!event.isCancelled()) {
			EItem explosive = plugin.getExplosivesConfig().searchExplosiveByItemStack(event.getItemInHand());
			if(null != explosive) {
				Player player = event.getPlayer();
				if (!explosive.hasPermission(player)) {
					event.setCancelled(true);
					customLogger.userError(player, "You don't have permissions to place this type of block.");
				} else {
					Block block = event.getBlock();
					customLogger.debug(String.format("Place '%s' at %s:%d:%d:%d",
	                                                 explosive.getName(), block.getWorld().getName(), block.getX(), block.getY(), block.getZ()));
					setExplosive(block, explosive);
				}
			} else
				deleteExplosive(event.getBlock());
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockBreak(BlockBreakEvent event) {
		if(!event.isCancelled()) {
			Block block = event.getBlock();
			EItem explosive = searchExplosiveByBlock(block);
			if(null != explosive) {
				Player player = event.getPlayer();
				if (!explosive.hasPermission(player)) {
					event.setCancelled(true);
					customLogger.userError(player, "You don't have permissions to break this type of block.");
				} else {
					customLogger.debug(String.format("Break '%s' at %s:%d:%d:%d",
	                                                 explosive.getName(), block.getWorld().getName(), block.getX(), block.getY(), block.getZ()));
					deleteExplosive(block);
	
					event.setCancelled(true);
					block.setType(Material.AIR);
					if(event.getPlayer().getGameMode() != GameMode.CREATIVE)
						block.getWorld().dropItemNaturally(block.getLocation(), explosive.getDroppedItemStack());
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPistonExtend(BlockPistonExtendEvent event) {
		if(!event.isCancelled()) {
			List<Block> blocks = event.getBlocks();
			for(int i = 0; i < blocks.size(); i++) {
				Block block = blocks.get(i);
				maybeMoveBlock(block, event.getDirection());
			}
		}
	}
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPistonRetract(BlockPistonRetractEvent event) {
		if(!event.isCancelled() && event.isSticky()) {
			List<Block> blocks = event.getBlocks();
			for(int i = 0; i < blocks.size(); i++) {
				Block block = blocks.get(i);
				maybeMoveBlock(block, event.getDirection());
			}
		}
	}
	
	private void onTaskMetaClean() {
		List<Block> blocks = plugin.getBlockMetaStorage().getAllBlocks();
		for(int i = 0; i < blocks.size(); i++) {
			Block block = blocks.get(i);
			if(!plugin.getExplosivesConfig().isRegisteredMaterial(block.getType())) {
				customLogger.info(String.format("Block '%s' at x=%d y=%d z=%d is not from the registered material. Meta will be deleted.",
												block.getType().toString(), block.getX(), block.getY(), block.getZ()));
				deleteExplosive(block);
			}
		}
	}

	private void setExplosive(Block block, EItem explosive) {
		plugin.getBlockMetaStorage().set(block, blockMetaKey, explosive.getName());
		maybeScheduleCleaning();
	}

	private void deleteExplosive(Block block) {
		plugin.getBlockMetaStorage().delete(block, blockMetaKey);
	}

	private EItem searchExplosiveByBlock(Block block) {
		if(plugin.getExplosivesConfig().isRegisteredMaterial(block.getType())) {
			String name = plugin.getBlockMetaStorage().get(block, blockMetaKey);
			if (null != name)
				return plugin.getExplosivesConfig().searchExplosiveByName(name);
			else
				return null;
		}
		else
			return null;
	}
	
	private void maybeMoveBlock(Block block, BlockFace direction) {
		EItem explosive = searchExplosiveByBlock(block);
		if(null != explosive) {
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

	private Block getBlockInDirection(Block block, BlockFace direction) {
		return block.getWorld().getBlockAt(block.getX() + direction.getModX(),
											block.getY() + direction.getModY(),
											block.getZ() + direction.getModZ());
	}
	
	private void maybeScheduleCleaning() {
		if(0 == random.nextInt(10))
			scheduleCleaning();
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
