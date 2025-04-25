package com.gmail.uprial.customnukes.storage;

import com.gmail.uprial.customnukes.CustomNukes;
import com.gmail.uprial.customnukes.common.CustomLogger;
import com.gmail.uprial.customnukes.schema.EItem;
import org.bukkit.block.Block;

import java.io.File;
import java.util.List;
import java.util.Random;

public class ExplosiveBlockStorage {
    private static final String BLOCK_META_KEY = "explosive";

    private final CustomNukes plugin;
    private final CustomLogger customLogger;

    private final BlockMetaStorage blockMetaStorage;
    private final Random random = new Random();

    public ExplosiveBlockStorage(CustomNukes plugin, File dataFolder, CustomLogger customLogger) {
        this.plugin = plugin;
        this.customLogger = customLogger;

        blockMetaStorage = new BlockMetaStorage(plugin, dataFolder, customLogger);

        scheduleCleaning();
    }

    public void save() {
        blockMetaStorage.save();
    }

    public void clear() {
        blockMetaStorage.clear();
    }

    private void onTaskMetaClean() {
        List<Block> blocks = blockMetaStorage.getAllBlocks();
        for(Block block : blocks) {
            if(!plugin.getExplosivesConfig().isRegisteredMaterial(block.getType())) {
                customLogger.info(String.format("Block '%s' at x=%d y=%d z=%d is not from the registered material. Meta will be deleted.",
                        block.getType().toString(), block.getX(), block.getY(), block.getZ()));
                deleteExplosive(block);
            }
        }
    }

    public void setExplosive(Block block, EItem explosive) {
        blockMetaStorage.set(block, BLOCK_META_KEY, explosive.getName());
        maybeScheduleCleaning();
    }

    public void deleteExplosive(Block block) {
        blockMetaStorage.delete(block, BLOCK_META_KEY);
    }

    public EItem searchExplosiveByBlock(Block block) {
        if(plugin.getExplosivesConfig().isRegisteredMaterial(block.getType())) {
            String name = blockMetaStorage.get(block, BLOCK_META_KEY);
            return (name != null) ? plugin.getExplosivesConfig().searchExplosiveByName(name) : null;
        }
        else {
            return null;
        }
    }

    private void maybeScheduleCleaning() {
        if(random.nextInt(10) == 0) {
            scheduleCleaning();
        }
    }

    private void scheduleCleaning() {
        plugin.scheduleDelayed(new Runnable() {
            @Override
            public void run() {
                onTaskMetaClean();
            }
        }, 100);
    }
}
