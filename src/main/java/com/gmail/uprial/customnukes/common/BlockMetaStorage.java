package com.gmail.uprial.customnukes.common;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

public class BlockMetaStorage {
	private static Character keyDelimiter = ':';

	private final JavaPlugin plugin;
	private final CustomStorage storage;
	private final CustomLogger customLogger;
	
	public BlockMetaStorage(JavaPlugin plugin, File dataFolder, CustomLogger customLogger) {
		this.plugin = plugin;
		this.storage = new CustomStorage(dataFolder, "block-meta.txt", customLogger);
		this.customLogger = customLogger;
		
		storage.load();
	}
	
	public void save() {
		storage.save();
	}
	
	public void set(Block block, String key, String value) {
		setToBlock(block, key, value);
		storage.set(getMapKey(block.getLocation(), key), value);
	}
	
	public void delete(Block block, String key) {
		deleteFromBlock(block, key);
		storage.delete(getMapKey(block.getLocation(), key));
	}
	
	public String get(Block block, String key) {
		String value = getFromBlock(block, key);
		if(null == value) {
			value = storage.get(getMapKey(block.getLocation(), key));
			if(null != value)
				setToBlock(block, key, value);
		}
		
		return value;
	}
	
    public List<Block> getAllBlocks() {
    	List<Block> blocks = new ArrayList<Block>();
    	List<String> errorKeys = new ArrayList<String>();
    	
		for (Map.Entry<String,String> entry : storage.entrySet()) {
			String key = entry.getKey().toString();
			boolean error = false;
			World world = null;
			Block block = null;
			
			String[] items = EUtils.split(key, keyDelimiter);
			if(!error && (items.length != 5))
				error = true;
			
			if(!error) {
				world = plugin.getServer().getWorld(items[0]);
				if(null == world)
					error =  true;
			}
			if(!error) {
				block = world.getBlockAt(Integer.valueOf(items[1]), Integer.valueOf(items[2]), Integer.valueOf(items[3]));
				if(null == block)
					error = true;
			}
			
			if(error) {
				customLogger.info(String.format("Key '%s' does not links to proper block and will be removed", key));
				errorKeys.add(key);
			}
			else
				blocks.add(block);
		}
		for(int i = 0; i < errorKeys.size(); i++)
			storage.delete(errorKeys.get(i));
		
		return blocks;
    }
   
	private void setToBlock(Block block, String key, String value) {
		block.setMetadata(key, new FixedMetadataValue(plugin, value));
	}

	private String getFromBlock(Block block, String key) {
		List<MetadataValue> metadataValue = block.getMetadata(key);
		if(metadataValue.size() > 0)
			return metadataValue.get(0).asString();
		else
			return null;
	}

	private void deleteFromBlock(Block block, String key) {
		block.removeMetadata(key, plugin);
	}
    
	private String getMapKey(Location location, String key) {
		String[] items = new String[5];
		items[0] = location.getWorld().getName();
		items[1] = String.valueOf(location.getBlockX());
		items[2] = String.valueOf(location.getBlockY());
		items[3] = String.valueOf(location.getBlockZ());
		items[4] = key;
		
		return EUtils.join(items, keyDelimiter);
	}
	
}