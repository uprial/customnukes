package com.gmail.uprial.customnukes.schema;

import com.gmail.uprial.customnukes.common.CustomLogger;
import com.gmail.uprial.customnukes.config.ConfigReaderNumbers;
import com.gmail.uprial.customnukes.config.InvalidConfigException;
import org.bukkit.configuration.file.FileConfiguration;

public abstract class AbstractEScenarioActionExplosion extends AbstractEScenarioActionDelayed {
    abstract double minRadius();
    abstract double maxRadius();

    double radius = 0.0F;

    AbstractEScenarioActionExplosion(String actionId) {
        super(actionId);
    }

    @Override
    public void loadFromConfig(FileConfiguration config, CustomLogger customLogger, String key, String title) throws InvalidConfigException {
        super.loadFromConfig(config, customLogger, key, title);
        radius = ConfigReaderNumbers.getDouble(config, customLogger, key + ".radius", String.format("radius of %s", title), minRadius(), maxRadius());
    }
}
