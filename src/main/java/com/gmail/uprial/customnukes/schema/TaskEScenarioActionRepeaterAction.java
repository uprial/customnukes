package com.gmail.uprial.customnukes.schema;

import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.uprial.customnukes.CustomNukes;

public class TaskEScenarioActionRepeaterAction extends BukkitRunnable  {
    private final EScenarioActionRepeater executor;
    private final CustomNukes plugin;
    private final Location location;
    private int runsCount;

    public TaskEScenarioActionRepeaterAction(EScenarioActionRepeater executor, CustomNukes plugin, Location location, int runsCount) {
        this.executor = executor;
        this.plugin = plugin;
        this.location = location;
        this.runsCount = runsCount;
    }

    public void run() {
        if(runsCount >= 0) {
            runsCount--;
            executor.executeAction(plugin, location, getTaskId(), runsCount);
        } else {
            cancel();
            executor.finishAction(plugin, location, getTaskId());
        }
    }
}
