package com.gmail.uprial.customnukes.schema;

import com.gmail.uprial.customnukes.config.InvalidConfigException;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import com.gmail.uprial.customnukes.CustomNukes;
import com.gmail.uprial.customnukes.common.CustomLogger;

interface I_EScenarioActionSubAction {

    int execute(CustomNukes plugin, Location location, int delay);

    void loadFromConfig(FileConfiguration config, CustomLogger customLogger, String key, String title) throws InvalidConfigException;
}
