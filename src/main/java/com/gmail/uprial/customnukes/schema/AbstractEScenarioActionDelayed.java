package com.gmail.uprial.customnukes.schema;

import com.gmail.uprial.customnukes.CustomNukes;
import com.gmail.uprial.customnukes.common.CustomLogger;
import com.gmail.uprial.customnukes.config.ConfigReaderNumbers;
import com.gmail.uprial.customnukes.config.InvalidConfigException;
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
    public void loadFromConfig(FileConfiguration config, CustomLogger customLogger, String key, String title) throws InvalidConfigException {
        minDelay = getDelayFromConfig(config, customLogger, key + ".min-delay", String.format("minimum delay of %s", title), defaultMinDelay());
        maxDelay = getDelayFromConfig(config, customLogger, key + ".max-delay", String.format("maximum delay of %s", title), defaultMaxDelay());
        if(minDelay > maxDelay) {
            throw new InvalidConfigException(String.format("Value of minimum delay of %s should be lower or equal to maximum delay.", title));
        }
    }

    String getActionId() {
        return actionId;
    }

    private int getDelayFromConfig(FileConfiguration config, CustomLogger customLogger, String key, String title, int defaultValue) throws InvalidConfigException {
        return ConfigReaderNumbers.getInt(config, customLogger, key, title, minDelayValue(), maxDelayValue(), defaultValue);
    }
}
