package com.gmail.uprial.customnukes.schema;

import org.bukkit.configuration.file.FileConfiguration;

import com.gmail.uprial.customnukes.ConfigReader;
import com.gmail.uprial.customnukes.ConfigReaderResult;
import com.gmail.uprial.customnukes.common.CustomLogger;

abstract public class AbstractEScenarioActionExplosion extends AbstractEScenarioActionDelayed {
    abstract float minRadius();
    abstract float maxRadius();
        
    protected float radius;
    
    public AbstractEScenarioActionExplosion(String actionId) {
        super(actionId);
    }
    
    public void setRadius(float radius) {
        this.radius = radius;
    }

    public boolean isLoadedFromConfig(FileConfiguration config, CustomLogger customLogger, String key, String name) {
        if(!super.isLoadedFromConfig(config, customLogger, key, name))
            return false;

        if(!this.isLoadedRadiusFromConfig(config, customLogger, key, name))
            return false;

        return true;
    }
    
    private boolean isLoadedRadiusFromConfig(FileConfiguration config, CustomLogger customLogger, String key, String name) {
        ConfigReaderResult result = ConfigReader.getFloatComplex(config, customLogger, key + ".radius", "Radius of action", name, minRadius(), maxRadius());
        if(result.isError())
            return false;
        else {
            setRadius(result.getFloat());
            return true;
        }
    }

}
