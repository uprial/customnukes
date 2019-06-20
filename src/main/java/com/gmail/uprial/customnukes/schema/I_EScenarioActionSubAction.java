package com.gmail.uprial.customnukes.schema;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import com.gmail.uprial.customnukes.CustomNukes;
import com.gmail.uprial.customnukes.common.CustomLogger;

interface I_EScenarioActionSubAction {

    int execute(CustomNukes plugin, Location location, int delay);

    boolean isLoadedFromConfig(FileConfiguration config, CustomLogger customLogger, String key, String title);

}
