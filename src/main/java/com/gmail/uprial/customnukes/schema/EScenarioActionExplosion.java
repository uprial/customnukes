package com.gmail.uprial.customnukes.schema;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import com.gmail.uprial.customnukes.ConfigReader;
import com.gmail.uprial.customnukes.CustomNukes;
import com.gmail.uprial.customnukes.common.CustomLogger;

public class EScenarioActionExplosion extends AbstractEScenarioActionExplosion {
    protected int defaultMinDelay() { return 2; }
    protected int defaultMaxDelay() { return 8; }
    protected int minDelayValue() { return 2; }
    protected int maxDelayValue() { return 1000; }

    protected float minRadius() { return 0; }
    protected float maxRadius() { return 320; }

    protected boolean defaultDestroyBlocks() { return true; }

    private boolean destroyBlocks;

    public EScenarioActionExplosion(String actionId) {
        super(actionId);
    }

    public void explode(CustomNukes plugin, Location location) {
        location.getWorld().createExplosion(location.getX(), location.getY(), location.getZ(), radius, false, destroyBlocks);
    }

    public void setDestroyBlocks(boolean destroyBlocks) {
        this.destroyBlocks = destroyBlocks;
    }

    public boolean isLoadedFromConfig(FileConfiguration config, CustomLogger customLogger, String key, String name) {
        if(!super.isLoadedFromConfig(config, customLogger, key, name))
            return false;

        setDestroyBlocks(ConfigReader.getBoolean(config, customLogger, key + ".destroy-blocks", "'destroy-blocks' value of action", name, defaultDestroyBlocks()));

        return true;
    }

}
