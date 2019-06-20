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
    public boolean isLoadedFromConfig(FileConfiguration config, CustomLogger customLogger, String key, String title) {
        return super.isLoadedFromConfig(config, customLogger, key, title) && isLoadedRadiusFromConfig(config, customLogger, key, title);

    }

    private boolean isLoadedRadiusFromConfig(FileConfiguration config, CustomLogger customLogger, String key, String title) {
        ConfigReaderResult result = ConfigReader.getFloatComplex(config, customLogger, key + ".radius", String.format("radius of %s", title), minRadius(), maxRadius());
        if(result.isError()) {
            return false;
        } else {
            radius = result.getFloatValue();
            return true;
        }
    }

}
