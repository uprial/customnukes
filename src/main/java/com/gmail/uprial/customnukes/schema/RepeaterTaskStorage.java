package com.gmail.uprial.customnukes.schema;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.World;

import com.gmail.uprial.customnukes.CustomNukes;
import com.gmail.uprial.customnukes.common.CustomLogger;
import com.gmail.uprial.customnukes.common.CustomStorage;
import com.gmail.uprial.customnukes.common.EUtils;

public class RepeaterTaskStorage {
	private static Character keyDelimiter = ':';

	private final CustomNukes plugin;
	private final CustomStorage storage;
	private final CustomLogger customLogger;
	
	public RepeaterTaskStorage(CustomNukes plugin, File dataFolder, CustomLogger customLogger) {
		this.plugin = plugin;
		this.storage = new CustomStorage(dataFolder, "repeater-task.txt", customLogger);
		this.customLogger = customLogger;
		
		storage.load();
	}
	
	public void save() {
		storage.save();
	}
	
	public void set(Location location, String actionId,int taskId,  int runsCount) {
		if(runsCount >= 0)
			storage.set(getMapKey(location, actionId, taskId), String.valueOf(runsCount));
		else
			storage.delete(getMapKey(location, actionId, taskId));
	}
	
	public void restore() {
		List<EScenarioActionRepeater> actions = new ArrayList<EScenarioActionRepeater>();
		List<Location> locations = new ArrayList<Location>();
		List<Integer> runsCounts = new ArrayList<Integer>();
		
		for (Map.Entry<String,String> entry : storage.entrySet()) {
			String key = entry.getKey().toString();
			boolean error = false;
			
			World world = null;
			Location location = null;
			EScenarioActionRepeater action = null;
			double x = 0;
			double y = 0;
			double z = 0;
			
			String[] items = EUtils.split(key, keyDelimiter);
			if(!error && (items.length != 6)) {
				customLogger.error(String.format("Key '%s' is invalid", key));
				error = true;
			}
			
			if(!error) {
				world = plugin.getServer().getWorld(items[0]);
				if(null == world) {
					customLogger.error(String.format("Key '%s' does not links to proper world", key));
					error =  true;
				}
			}
			if(!error) {
				try {
					x = Double.valueOf(items[1]);
					y = Double.valueOf(items[2]);
					z = Double.valueOf(items[3]);
				} catch (NumberFormatException e) {
					customLogger.error(e.toString());
					error = true;
				}
			}
				
			if(!error) {
				location = new Location(world, x, y, z);

				action = EScenarioActionRepeaterMap.INSTANCE.get(items[4]);
				if(null == action) {
					customLogger.warning(String.format("Key '%s' does not links to proper action", key));
					error = true;
				}
			}
			if(!error) {
				int runsCount = Integer.valueOf(entry.getValue().toString());
				if(runsCount >= 0) {
					actions.add(action);
					locations.add(location);
					runsCounts.add(runsCount);
				}
			}
		}
		storage.clear();
		for(int i = 0; i < locations.size(); i++) 
			actions.get(i).explodeEx(plugin, locations.get(i), runsCounts.get(i));
		
	}
	
	private String getMapKey(Location location, String actionId, int taskId) {
		String[] items = new String[6];
		items[0] = location.getWorld().getName();
		items[1] = String.format(Locale.US, "%.2f", location.getX());
		items[2] = String.format(Locale.US, "%.2f", location.getY());
		items[3] = String.format(Locale.US, "%.2f", location.getZ());
		items[4] = actionId;
		items[5] = String.valueOf(taskId);
		
		return EUtils.join(items, keyDelimiter);
	}

}