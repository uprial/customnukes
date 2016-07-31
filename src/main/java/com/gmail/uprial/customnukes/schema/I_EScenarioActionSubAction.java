package com.gmail.uprial.customnukes.schema;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import com.gmail.uprial.customnukes.CustomNukes;
import com.gmail.uprial.customnukes.common.CustomLogger;

public interface I_EScenarioActionSubAction {
    
    public int execute(CustomNukes plugin, Location location, int delay);
    
    public boolean isLoadedFromConfig(FileConfiguration config, CustomLogger customLogger, String key, String name);
    
}
