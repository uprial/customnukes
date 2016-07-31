package com.gmail.uprial.customnukes.schema;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import com.gmail.uprial.customnukes.ConfigReader;
import com.gmail.uprial.customnukes.CustomNukes;
import com.gmail.uprial.customnukes.common.CustomLogger;

abstract public class AbstractEScenarioActionDelayed implements I_EScenarioActionSubAction {
    abstract int defaultMinDelay();
    abstract int defaultMaxDelay();
    abstract int minDelayValue();
    abstract int maxDelayValue();

    abstract public void explode(CustomNukes plugin, Location location);

    private String actionId;

    private final Random random;

    private int minDelay;
    private int maxDelay;

    public AbstractEScenarioActionDelayed(String actionId) {
        this.actionId = actionId;
        random = new Random();
    }

    public int execute(CustomNukes plugin, Location location, int delay) {
        int currentDelay = minDelay + random.nextInt(maxDelay - minDelay + 1);
        int globalDelay = delay + currentDelay;

        plugin.scheduleDelayed(new TaskEScenarioActionDelayedExplode(this, plugin, location), globalDelay);

        return globalDelay;
    }


    public void setMinDelay(int minDelay) {
        this.minDelay = minDelay;
    }

    public void setMaxDelay(int maxDelay) {
        this.maxDelay = maxDelay;
    }

    public boolean isLoadedFromConfig(FileConfiguration config, CustomLogger customLogger, String key, String name) {
        int minDelay = getDelayFromConfig(config, customLogger, key + ".min-delay", name, "minimum delay", defaultMinDelay());
        int maxDelay = getDelayFromConfig(config, customLogger, key + ".max-delay", name, "maximum delay", defaultMaxDelay());
        if(minDelay > maxDelay) {
            customLogger.error(String.format("Value of minimum delay of action '%s' should be lower or equal to maximum delay. Use default values: %d, %d",
                    name, minDelayValue(), maxDelayValue()));
            minDelay = minDelayValue();
            maxDelay = maxDelayValue();
        }
        setMinDelay(minDelay);
        setMaxDelay(maxDelay);

        return true;
    }

    protected String getActionId() {
        return actionId;
    }

    private int getDelayFromConfig(FileConfiguration config, CustomLogger customLogger, String key, String name, String valueName, int defaultValue) {
        return ConfigReader.getInt(config, customLogger, key, String.format("%s of action", valueName), name, minDelayValue(), maxDelayValue(), defaultValue);
    }
}
