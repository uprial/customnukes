package com.gmail.uprial.customnukes;

import org.bukkit.scheduler.BukkitRunnable;

public class TaskPeriodicSave extends BukkitRunnable {
	private final CustomNukes plugin;
	
	public TaskPeriodicSave(CustomNukes plugin) {
		this.plugin = plugin;
	}
	
	public void run() {
		plugin.saveData();
	}

}
