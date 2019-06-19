package com.gmail.uprial.customnukes.schema;

import com.gmail.uprial.customnukes.CustomNukes;
import com.gmail.uprial.customnukes.common.CustomLogger;
import com.gmail.uprial.customnukes.common.CustomStorage;
import com.gmail.uprial.customnukes.storage.StorageUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.*;
import java.util.Map.Entry;

public class RepeaterTaskStorage {
    private static final Character KEY_DELIMITER = ':';

    private final CustomNukes plugin;
    private final CustomStorage storage;
    private final CustomLogger customLogger;

    private Map<Integer,BukkitTask> tasks;

    public RepeaterTaskStorage(CustomNukes plugin, File dataFolder, CustomLogger customLogger) {
        this.plugin = plugin;
        storage = new CustomStorage(dataFolder, "repeater-task.txt", customLogger);
        this.customLogger = customLogger;

        storage.load();
        initData();
    }

    public void save() {
        storage.save();
    }

    public void clear() {
        for (Entry<Integer,BukkitTask> entry : tasks.entrySet()) {
            entry.getValue().cancel();
        }
        storage.clear();
        initData();

        save();
    }

    public void insert(Location location, String actionId, BukkitTask task, int runsCount) {
        tasks.put(task.getTaskId(), task);
        set(location, actionId, task.getTaskId(), runsCount);
    }

    public void update(Location location, String actionId, int taskId, int runsCount) {
        set(location, actionId, taskId, runsCount);
    }

    public void delete(Location location, String actionId, int taskId) {
        tasks.remove(taskId);
        set(location, actionId, taskId, -1);
    }

    public void restore() {
        List<EScenarioActionRepeater> actions = new ArrayList<>();
        List<Location> locations = new ArrayList<>();
        List<Integer> runsCounts = new ArrayList<>();

        for (Entry<String,String> entry : storage.entrySet()) {
            String key = entry.getKey();
            boolean isValid = true;

            World world = null;
            Location location = null;
            EScenarioActionRepeater action = null;
            double x = 0;
            double y = 0;
            double z = 0;

            String[] items = StorageUtils.split(key, KEY_DELIMITER);
            if(items.length != 6) {
                customLogger.error(String.format("Key '%s' is invalid", key));
                isValid = false;
            }

            if(isValid) {
                world = plugin.getServer().getWorld(items[0]);
                if(world == null) {
                    customLogger.error(String.format("Key '%s' does not links to proper world", key));
                    isValid = false;
                }
            }
            if(isValid) {
                try {
                    x = Double.valueOf(items[1]);
                    y = Double.valueOf(items[2]);
                    z = Double.valueOf(items[3]);
                } catch (NumberFormatException e) {
                    customLogger.error(e.toString());
                    isValid = false;
                }
            }

            if(isValid) {
                location = new Location(world, x, y, z);

                action = EScenarioActionRepeaterMap.INSTANCE.get(items[4]);
                if(action == null) {
                    customLogger.warning(String.format("Key '%s' does not links to proper action", key));
                    isValid = false;
                }
            }
            if(isValid) {
                int runsCount = Integer.valueOf(entry.getValue());
                if(runsCount >= 0) {
                    actions.add(action);
                    locations.add(location);
                    runsCounts.add(runsCount);
                }
            }
        }
        storage.clear();
        int locationsSize = locations.size();
        for(int i = 0; i < locationsSize; i++) {
            actions.get(i).explodeEx(plugin, locations.get(i), runsCounts.get(i));
        }

    }

    private void initData() {
        tasks = new HashMap<>();
    }

    private void set(Location location, String actionId, int taskId, int runsCount) {
        String mapKey = getMapKey(location, actionId, taskId);

        if(runsCount >= 0) {
            storage.set(mapKey, String.valueOf(runsCount));
        } else {
            storage.delete(mapKey);
        }
    }

    private static String getMapKey(Location location, String actionId, int taskId) {
        String[] items = new String[6];
        items[0] = location.getWorld().getName();
        items[1] = String.format(Locale.US, "%.2f", location.getX());
        items[2] = String.format(Locale.US, "%.2f", location.getY());
        items[3] = String.format(Locale.US, "%.2f", location.getZ());
        items[4] = actionId;
        items[5] = String.valueOf(taskId);

        return StorageUtils.join(items, KEY_DELIMITER);
    }

}
