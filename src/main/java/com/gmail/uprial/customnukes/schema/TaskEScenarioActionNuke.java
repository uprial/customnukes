package com.gmail.uprial.customnukes.schema;

import com.gmail.uprial.customnukes.CustomNukes;
import org.bukkit.Location;

class TaskEScenarioActionNuke implements Runnable {
    private final EScenarioActionNuke parent;
    private final CustomNukes plugin;
    private final Location location;
    private final double r;

    TaskEScenarioActionNuke(EScenarioActionNuke parent, CustomNukes plugin, Location location, double r) {
        this.parent = parent;
        this.plugin = plugin;
        this.location = location;
        this.r = r;
    }
    @Override
    public void run() {
        parent.explode(plugin, location, r);
    }
}
