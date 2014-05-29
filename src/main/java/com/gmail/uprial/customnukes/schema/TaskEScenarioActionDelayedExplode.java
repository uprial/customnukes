package com.gmail.uprial.customnukes.schema;

import org.bukkit.Location;

import com.gmail.uprial.customnukes.CustomNukes;

public class TaskEScenarioActionDelayedExplode implements Runnable {
	private final AbstractEScenarioActionDelayed parent;
	private final CustomNukes plugin;
	private final Location location;
	
	public TaskEScenarioActionDelayedExplode(AbstractEScenarioActionDelayed parent, CustomNukes plugin, Location location) {
		this.parent = parent;
		this.plugin = plugin;
		this.location = location;
	}
	public void run() {
		parent.explode(plugin, location);
	}
}