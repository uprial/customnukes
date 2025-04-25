package com.gmail.uprial.customnukes.storage;

import com.gmail.uprial.customnukes.common.CustomLogger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import static com.gmail.uprial.customnukes.common.Formatter.format;

class BlockMetaStorage {
    private static final Character KEY_DELIMITER = ':';

    private final JavaPlugin plugin;
    private final CustomStorage storage;
    private final CustomLogger customLogger;

    BlockMetaStorage(JavaPlugin plugin, File dataFolder, CustomLogger customLogger) {
        this.plugin = plugin;
        storage = new CustomStorage(dataFolder, "block-meta.txt", customLogger);
        this.customLogger = customLogger;

        storage.load();
    }

    void save() {
        storage.save();
    }

    void clear() {
        for (Entry<String,String> entry : storage.entrySet()) {
            String key = entry.getKey();
            Block block = getBlockByKey(key);
            if(block != null) {
                customLogger.debug(String.format("Removed %s", format(block)));
                deleteFromBlock(block, getMetadataKeyByKey(key));
                block.setType(Material.AIR);
            }
        }
        storage.clear();
        save();
    }

    @SuppressWarnings("SameParameterValue")
    void set(Block block, String metadataKey, String value) {
        setToBlock(block, metadataKey, value);
        storage.set(getMapKey(block.getLocation(), metadataKey), value);
    }

    @SuppressWarnings("SameParameterValue")
    void delete(Block block, String metadataKey) {
        deleteFromBlock(block, metadataKey);
        storage.delete(getMapKey(block.getLocation(), metadataKey));
    }

    @SuppressWarnings("SameParameterValue")
    String get(Block block, String metadataKey) {
        String value = getFromBlock(block, metadataKey);
        if(value == null) {
            value = storage.get(getMapKey(block.getLocation(), metadataKey));
            if(value != null) {
                setToBlock(block, metadataKey, value);
            }
        }

        return value;
    }

    List<Block> getAllBlocks() {
        List<Block> blocks = new ArrayList<>();
        List<String> errorKeys = new ArrayList<>();

        for (Entry<String,String> entry : storage.entrySet()) {
            String key = entry.getKey();
            Block block = getBlockByKey(key);
            if (block == null) {
                customLogger.info(String.format("Key '%s' does not links to proper block and will be removed", key));
                errorKeys.add(key);
            }
            else {
                blocks.add(block);
            }
        }

        for(String key : errorKeys) {
            storage.delete(key);
        }

        return blocks;
    }

    private Block getBlockByKey(String key) {
        String[] items = StorageUtils.split(key, KEY_DELIMITER);
        if(items.length != 5) {
            return null;
        }

        World world = plugin.getServer().getWorld(items[0]);
        if(world == null) {
            return null;
        }

        return world.getBlockAt(Integer.valueOf(items[1]), Integer.valueOf(items[2]), Integer.valueOf(items[3]));
       }

       private static String getMetadataKeyByKey(String key) {
        String[] items = StorageUtils.split(key, KEY_DELIMITER);
        return items[4];
       }

    private void setToBlock(Block block, String metadataKey, String value) {
        block.setMetadata(metadataKey, new FixedMetadataValue(plugin, value));
    }

    private static String getFromBlock(Block block, String metadataKey) {
        List<MetadataValue> metadataValue = block.getMetadata(metadataKey);
        return !metadataValue.isEmpty() ? metadataValue.get(0).asString() : null;
    }

    private void deleteFromBlock(Block block, String metadataKey) {
        block.removeMetadata(metadataKey, plugin);
    }

    private static String getMapKey(Location location, String metadataKey) {
        String[] items = new String[5];
        items[0] = location.getWorld().getName();
        items[1] = String.valueOf(location.getBlockX());
        items[2] = String.valueOf(location.getBlockY());
        items[3] = String.valueOf(location.getBlockZ());
        items[4] = metadataKey;

        return StorageUtils.join(items, KEY_DELIMITER);
    }

}
