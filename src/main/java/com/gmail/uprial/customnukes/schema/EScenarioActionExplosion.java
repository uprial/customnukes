package com.gmail.uprial.customnukes.schema;

import com.gmail.uprial.customnukes.config.ConfigReaderSimple;
import com.gmail.uprial.customnukes.config.InvalidConfigException;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

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
    protected double minRadius() { return 0; }
    @Override
    protected double maxRadius() { return 16; }

    @SuppressWarnings({"SameReturnValue", "BooleanMethodNameMustStartWithQuestion"})
    private static boolean defaultDestroyBlocks() { return true; }

    private boolean destroyBlocks = false;

    public EScenarioActionExplosion(String actionId) {
        super(actionId);
    }

    @Override
    public void explode(CustomNukes plugin, Location location) {
        location.getWorld().createExplosion(location.getX(), location.getY(), location.getZ(), (float)radius, false, destroyBlocks);
    }

    @Override
    public void loadFromConfig(FileConfiguration config, CustomLogger customLogger, String key, String title) throws InvalidConfigException {
        super.loadFromConfig(config, customLogger, key, title);

        destroyBlocks = ConfigReaderSimple.getBoolean(config, customLogger, key + ".destroy-blocks",
                String.format("'destroy-blocks' value of %s", title), defaultDestroyBlocks());
    }

}
