package com.gmail.uprial.customnukes;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.gmail.uprial.customnukes.common.CustomLogger;
import com.gmail.uprial.customnukes.schema.EItem;

public class ExplosivesActivateListener implements Listener {
	private final CustomNukes plugin;
	private final CustomLogger customLogger;
	
	public ExplosivesActivateListener(CustomNukes plugin, CustomLogger customLogger) {
		this.plugin = plugin;
		this.customLogger = customLogger;
	}

	@EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event) {
		if(!event.isCancelled()) {
		    if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
		    	if (event.getMaterial() == Material.FLINT_AND_STEEL) {
	    			if(try_activate(event.getClickedBlock()))
	    				event.setCancelled(true);
		    	}
		    }
		}
    }
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockRedstone(BlockRedstoneEvent event) {
		Block block = event.getBlock();
		if(null != block) {
			if(block.getType() == Material.REDSTONE_WIRE)
				try_activate(block.getRelative(0, -1, 0));
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityExplode(EntityExplodeEvent event) {
		if(!event.isCancelled()) {
			List<Block> blocks = event.blockList();
			for(int i = 0; i < blocks.size(); i++)
				try_activate(blocks.get(i));
		}
	}
	
	private boolean try_activate(Block block) {
		if(null == block)
			return false;
		if(!plugin.getExplosivesConfig().isRegisteredMaterial(block.getType()))
			return false;
		
		String name = plugin.getBlockMetaStorage().get(block, ExplosivesBlocksListener.blockMetaKey);
		if(null == name)
			return false;
		EItem explosive = plugin.getExplosivesConfig().searchExplosiveByName(name);
		if(null == explosive)
			return false;

		block.setType(Material.AIR);
		plugin.getBlockMetaStorage().delete(block, ExplosivesBlocksListener.blockMetaKey);
		Location location = new Location(block.getWorld(), block.getX() + 0.5, block.getY() + 0.5, block.getZ() + 0.5);
		customLogger.debug(String.format("Explode '%s' at %s:%d:%d:%d",
				                         explosive.getName(), block.getWorld().getName(), block.getX(), block.getY(), block.getZ()));
		explosive.explode(plugin, location);
		return true;
	}
}
