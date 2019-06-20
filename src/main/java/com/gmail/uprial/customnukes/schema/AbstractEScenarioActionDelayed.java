package com.gmail.uprial.customnukes.schema;

import com.gmail.uprial.customnukes.ConfigReader;
import com.gmail.uprial.customnukes.CustomNukes;
import com.gmail.uprial.customnukes.common.CustomLogger;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Random;

public abstract class AbstractEScenarioActionDelayed implements I_EScenarioActionSubAction {
    @SuppressWarnings("SameReturnValue")
    abstract int defaultMinDelay();
    abstract int defaultMaxDelay();
    @SuppressWarnings("SameReturnValue")
    abstract int minDelayValue();
    abstract int maxDelayValue();

    public abstract void explode(CustomNukes plugin, Location location);

    private final String actionId;

    private final Random random;

    private int minDelay = 0;
    private int maxDelay = 0;

    AbstractEScenarioActionDelayed(String actionId) {
        this.actionId = actionId;
        random = new Random();
    }

    @Override
    public int execute(CustomNukes plugin, Location location, int delay) {
        int currentDelay = minDelay + random.nextInt((maxDelay - minDelay) + 1);
        int globalDelay = delay + currentDelay;

        plugin.scheduleDelayed(new TaskEScenarioActionDelayedExplode(this, plugin, location), globalDelay);

        return globalDelay;
    }


    @Override
    public boolean isLoadedFromConfig(FileConfiguration config, CustomLogger customLogger, String key, String name) {
        int minDelay = getDelayFromConfig(config, customLogger, key + ".min-delay", name, "minimum delay", defaultMinDelay());
        int maxDelay = getDelayFromConfig(config, customLogger, key + ".max-delay", name, "maximum delay", defaultMaxDelay());
        if(minDelay > maxDelay) {
            customLogger.error(String.format("Value of minimum delay of action '%s' should be lower or equal to maximum delay. Use default values: %d, %d",
                    name, minDelayValue(), maxDelayValue()));
            minDelay = minDelayValue();
            maxDelay = maxDelayValue();
        }
        this.minDelay = minDelay;
        this.maxDelay = maxDelay;

        return true;
    }

    String getActionId() {
        return actionId;
    }

    private int getDelayFromConfig(FileConfiguration config, CustomLogger customLogger, String key, String name, String valueName, int defaultValue) {
        return ConfigReader.getInt(config, customLogger, key, String.format("%s of action '%s'", valueName, name), minDelayValue(), maxDelayValue(), defaultValue);
    }
}
