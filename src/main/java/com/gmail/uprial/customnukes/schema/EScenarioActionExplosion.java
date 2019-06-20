package com.gmail.uprial.customnukes.schema;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import com.gmail.uprial.customnukes.ConfigReader;
import com.gmail.uprial.customnukes.CustomNukes;
import com.gmail.uprial.customnukes.common.CustomLogger;

public class EScenarioActionExplosion extends AbstractEScenarioActionExplosion {
    @Override
    protected int defaultMinDelay() { return 2; }
    @Override
    protected int defaultMaxDelay() { return 8; }
    @Override
    protected int minDelayValue() { return 2; }
    @Override
    protected int maxDelayValue() { return 1000; }

    @Override
    protected float minRadius() { return 0; }
    @Override
    protected float maxRadius() { return 320; }

    @SuppressWarnings({"SameReturnValue", "BooleanMethodNameMustStartWithQuestion"})
    private static boolean defaultDestroyBlocks() { return true; }

    private boolean destroyBlocks = false;

    public EScenarioActionExplosion(String actionId) {
        super(actionId);
    }

    @Override
    public void explode(CustomNukes plugin, Location location) {
        location.getWorld().createExplosion(location.getX(), location.getY(), location.getZ(), radius, false, destroyBlocks);
    }

    @Override
    public boolean isLoadedFromConfig(FileConfiguration config, CustomLogger customLogger, String key, String name) {
        if(!super.isLoadedFromConfig(config, customLogger, key, name)) {
            return false;
        }

        destroyBlocks = ConfigReader.getBoolean(config, customLogger, key + ".destroy-blocks", String.format("'destroy-blocks' value of action '%s'", name), defaultDestroyBlocks());

        return true;
    }

}
