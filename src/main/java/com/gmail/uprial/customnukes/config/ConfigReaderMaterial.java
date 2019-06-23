package com.gmail.uprial.customnukes.config;

import com.gmail.uprial.customnukes.common.CustomLogger;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigReaderMaterial {

    public static Material getMaterial(FileConfiguration config, CustomLogger customLogger, String key, String title, Material defaultMaterial) throws InvalidConfigException {
        String strMaterial = config.getString(key);
        if(strMaterial == null) {
            customLogger.debug(String.format("Empty %s, use default '%s'", title, defaultMaterial));
            return defaultMaterial;
        } else {
            Material tmpMaterial = Material.getMaterial(strMaterial);
            //noinspection IfStatementWithTooManyBranches
            if(tmpMaterial == null) {
                throw new InvalidConfigException(String.format("Unknown %s '%s', use default '%s'", title, strMaterial, defaultMaterial));
            } else if(!tmpMaterial.isBlock()) {
                throw new InvalidConfigException(String.format("%s '%s' is not block, use default '%s'", title, tmpMaterial, defaultMaterial));
            } else if(tmpMaterial.hasGravity()) {
                throw new InvalidConfigException(String.format("%s '%s' has gravity, use default '%s'", title, tmpMaterial, defaultMaterial));
            } else if(!tmpMaterial.isSolid()) {
                throw new InvalidConfigException(String.format("%s '%s' is not solid, use default '%s'", title, tmpMaterial, defaultMaterial));
            } else if(tmpMaterial.isInteractable()) {
                throw new InvalidConfigException(String.format("%s '%s' is not interactable, use default '%s'", title, tmpMaterial, defaultMaterial));
            } else {
                return tmpMaterial;
            }
        }
    }
}
