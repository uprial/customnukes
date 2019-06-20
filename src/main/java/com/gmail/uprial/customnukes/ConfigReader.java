package com.gmail.uprial.customnukes;

import com.gmail.uprial.customnukes.common.CustomLogger;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public final class ConfigReader {
    public static int getInt(FileConfiguration config, CustomLogger customLogger, String key, String title, int min, int max, int defaultValue) {
        int value = defaultValue;

        if(config.getString(key) == null) {
            customLogger.debug(String.format("Empty %s. Use default value %d", title, defaultValue));
        } else {
            int intValue = config.getInt(key);
            if(min > intValue) {
                customLogger.error(String.format("%s should be at least %d. Use default value %d", title, min, defaultValue));
            } else if(max < intValue) {
                customLogger.error(String.format("%s should be at most %d. Use default value %d", title, max, defaultValue));
            } else {
                value = intValue;
            }
        }

        return value;
    }

    @SuppressWarnings({"StaticMethodOnlyUsedInOneClass", "SameParameterValue"})
    public static String getString(FileConfiguration config, CustomLogger customLogger, String key, String title) {
        String name = config.getString(key);

        if(name == null) {
            customLogger.error(String.format("Null/Empty %s", title));
            return null;
        }

        return name;
    }

    @SuppressWarnings({"StaticMethodOnlyUsedInOneClass", "SameParameterValue"})
    public static List<String> getStringList(FileConfiguration config, CustomLogger customLogger, String key, String title) {
        List<?> lines = config.getList(key);
        if(lines != null) {
            List<String> description = new ArrayList<>();
            for(Object line : lines) {
                description.add(line.toString());
            }

            return description;
        } else {
            customLogger.warning(String.format("Empty %s", title));
            return null;
        }
    }

    @SuppressWarnings({"BooleanParameter", "BooleanMethodNameMustStartWithQuestion"})
    public static boolean getBoolean(FileConfiguration config, CustomLogger customLogger, String key, String title, String name, boolean defaultValue) {
        boolean value = defaultValue;
        String strValue = config.getString(key);

        if(strValue == null) {
            customLogger.debug(String.format("Empty %s '%s'. Use default value %b", title, name, defaultValue));
        } else if(strValue.equalsIgnoreCase("true")) {
            value = true;
        } else if(strValue.equalsIgnoreCase("false")) {
            value = false;
        } else {
            customLogger.error(String.format("Invalid %s '%s'. Use default value %b", title, name, defaultValue));
        }

        return value;
    }

    public static ConfigReaderResult getFloatComplex(FileConfiguration config, CustomLogger customLogger, String key, String title, String name, float min, float max) {
        if(config.getString(key) == null) {
            customLogger.error(String.format("Null %s '%s", title, name));
            return ConfigReaderResult.errorResult();
        }

        float value = (float)config.getDouble(key);
        if(min > value) {
            customLogger.error(String.format("%s '%s' should be at least %.2f", title, name, min));
            return ConfigReaderResult.errorResult();
        }
        else if(max < value) {
            customLogger.error(String.format("%s '%s' should be at most %.2f", title, name, max));
            return ConfigReaderResult.errorResult();
        }

        return ConfigReaderResult.floatResult(value);
    }

    @SuppressWarnings("SameParameterValue")
    public static ConfigReaderResult getIntComplex(FileConfiguration config, CustomLogger customLogger, String key, String title, String name, int min, int max) {
        if(config.getString(key) == null) {
            customLogger.error(String.format("Null %s '%s", title, name));
            return ConfigReaderResult.errorResult();
        }

        int value = config.getInt(key);
        if(min > value) {
            customLogger.error(String.format("%s '%s' should be at least %d", title, name, min));
            return ConfigReaderResult.errorResult();
        }
        else if(max < value) {
            customLogger.error(String.format("%s '%s' should be at most %d", title, name, max));
            return ConfigReaderResult.errorResult();
        }

        return ConfigReaderResult.intResult(value);
    }

    public static Material getMaterial(FileConfiguration config, CustomLogger customLogger, String key, String title, Material defaultMaterial) {
        Material resMaterial = defaultMaterial;

        String strMaterial = config.getString(key);
        if(strMaterial == null) {
            customLogger.debug(String.format("Empty %s, use default '%s'", title, defaultMaterial));
        } else {
            Material tmpMaterial = Material.getMaterial(strMaterial);
            //noinspection IfStatementWithTooManyBranches
            if(tmpMaterial == null) {
                customLogger.error(String.format("Unknown %s '%s', use default '%s'", title, strMaterial, defaultMaterial));
            } else if(!tmpMaterial.isBlock()) {
                customLogger.error(String.format("%s '%s' is not block, use default '%s'", title, tmpMaterial, defaultMaterial));
            } else if(tmpMaterial.hasGravity()) {
                customLogger.error(String.format("%s '%s' has gravity, use default '%s'", title, tmpMaterial, defaultMaterial));
            } else if(!tmpMaterial.isSolid()) {
                customLogger.error(String.format("%s '%s' is not solid, use default '%s'", title, tmpMaterial, defaultMaterial));
            } else if(tmpMaterial.isInteractable()) {
                customLogger.error(String.format("%s '%s' is not interactable, use default '%s'", title, tmpMaterial, defaultMaterial));
            } else {
                resMaterial = tmpMaterial;
            }
        }

        return resMaterial;
    }
}
