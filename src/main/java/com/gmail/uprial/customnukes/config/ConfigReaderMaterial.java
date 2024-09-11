package com.gmail.uprial.customnukes.config;

import com.gmail.uprial.customnukes.common.CustomLogger;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigReaderMaterial {

    public static Material getMaterial(FileConfiguration config, CustomLogger customLogger, String key, String title, Material defaultMaterial) throws InvalidConfigException {
        String strMaterial = config.getString(key);
        if(strMaterial == null) {
            if (defaultMaterial == null) {
                throw new InvalidConfigException(String.format("Empty %s", title));
            } else {
                customLogger.debug(String.format("Empty %s, use default '%s'", title, defaultMaterial));
                return defaultMaterial;
            }
        } else {
            Material tmpMaterial = Material.getMaterial(strMaterial);
            //noinspection IfStatementWithTooManyBranches
            if(tmpMaterial == null) {
                customLogger.error(String.format("Unknown %s '%s', use default '%s'", title, strMaterial, defaultMaterial));
                return defaultMaterial;
            } else if(!tmpMaterial.isBlock()) {
                customLogger.error(String.format("%s '%s' is not block, use default '%s'", title, tmpMaterial, defaultMaterial));
                return defaultMaterial;
            } else if(tmpMaterial.hasGravity()) {
                customLogger.error(String.format("%s '%s' has gravity, use default '%s'", title, tmpMaterial, defaultMaterial));
                return defaultMaterial;
            } else if(!tmpMaterial.isSolid()) {
                customLogger.error(String.format("%s '%s' is not solid, use default '%s'", title, tmpMaterial, defaultMaterial));
                return defaultMaterial;
            } else if(tmpMaterial.isInteractable()) {
                customLogger.error(String.format("%s '%s' is not interactable, use default '%s'", title, tmpMaterial, defaultMaterial));
                return defaultMaterial;
            } else {
                return tmpMaterial;
            }
        }
    }
}
