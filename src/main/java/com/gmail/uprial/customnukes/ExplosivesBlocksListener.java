package com.gmail.uprial.customnukes;

import java.util.List;
import java.util.Random;

import org.bukkit.GameMode;
import org.bukkit.Location;
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
				} else
					setExplosive(event.getBlock(), explosive);
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
			for(int i = 0; i < event.getBlocks().size(); i++)
				maybeMoveBlock(event.getBlocks().get(i), event.getDirection(), true);
		}
	}
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPistonRetract(BlockPistonRetractEvent event) {
		if(!event.isCancelled() && event.isSticky()) {
			Location location = event.getRetractLocation();
			Block block = event.getBlock().getWorld().getBlockAt(location.getBlockX(), location.getBlockY(), location.getBlockZ());
			maybeMoveBlock(block, event.getDirection(), false);
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
		
			return plugin.getExplosivesConfig().searchExplosiveByName(name);
		}
		else
			return null;
	}
	
	private void maybeMoveBlock(Block block, BlockFace direction, boolean forward) {
		EItem explosive = searchExplosiveByBlock(block);
		if(null != explosive) {
			deleteExplosive(block);
			setExplosive(getBlockInDirection(block, direction, forward), explosive);
		}
		else
			deleteExplosive(getBlockInDirection(block, direction, forward));
	}

	private Block getBlockInDirection(Block block, BlockFace direction, boolean forward) {
		int sign;
		if(forward)
			sign = +1;
		else
			sign = -1;

		return block.getWorld().getBlockAt(block.getX() + sign * direction.getModX(),
											block.getY() + sign * direction.getModY(),
											block.getZ() + sign * direction.getModZ());
	}
	
	private void maybeScheduleCleaning() {
		if(0 == random.nextInt(1))
			scheduleCleaning();
	}
	
	private void scheduleCleaning() {
		plugin.scheduleDelayed(new Runnable() {
			@Override
			public void run() {
				onTaskMetaClean();
			}
		}, 2);
	}
}
