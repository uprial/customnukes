package com.gmail.uprial.customnukes;

import org.bukkit.scheduler.BukkitRunnable;

class TaskPeriodicSave extends BukkitRunnable {
    private final CustomNukes plugin;

    TaskPeriodicSave(CustomNukes plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        plugin.saveData();
    }

}
