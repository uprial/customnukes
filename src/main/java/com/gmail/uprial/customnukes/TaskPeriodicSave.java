package com.gmail.uprial.customnukes;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import static com.gmail.uprial.customnukes.common.Utils.SERVER_TICKS_IN_SECOND;

class TaskPeriodicSave extends BukkitRunnable {
    private static final int INTERVAL = SERVER_TICKS_IN_SECOND * 300;

    private final CustomNukes plugin;

    TaskPeriodicSave(CustomNukes plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        plugin.saveData();
    }

    public BukkitTask runTaskTimer() {
        return runTaskTimer(plugin, INTERVAL, INTERVAL);
    }

}
