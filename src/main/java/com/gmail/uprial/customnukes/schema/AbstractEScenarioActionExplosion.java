package com.gmail.uprial.customnukes.schema;

import org.bukkit.configuration.file.FileConfiguration;

import com.gmail.uprial.customnukes.ConfigReader;
import com.gmail.uprial.customnukes.ConfigReaderResult;
import com.gmail.uprial.customnukes.common.CustomLogger;

public abstract class AbstractEScenarioActionExplosion extends AbstractEScenarioActionDelayed {
    abstract float minRadius();
    abstract float maxRadius();

    float radius = 0.0F;

    AbstractEScenarioActionExplosion(String actionId) {
        super(actionId);
    }

    @Override
    public boolean isLoadedFromConfig(FileConfiguration config, CustomLogger customLogger, String key, String name) {
        return super.isLoadedFromConfig(config, customLogger, key, name) && isLoadedRadiusFromConfig(config, customLogger, key, name);

    }

    private boolean isLoadedRadiusFromConfig(FileConfiguration config, CustomLogger customLogger, String key, String name) {
        ConfigReaderResult result = ConfigReader.getFloatComplex(config, customLogger, key + ".radius", String.format("Radius of action '%s'", name), minRadius(), maxRadius());
        if(result.isError()) {
            return false;
        } else {
            radius = result.getFloatValue();
            return true;
        }
    }

}
