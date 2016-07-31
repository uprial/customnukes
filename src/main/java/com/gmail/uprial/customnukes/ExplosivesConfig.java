package com.gmail.uprial.customnukes;

import com.gmail.uprial.customnukes.common.CustomLogger;
import com.gmail.uprial.customnukes.schema.EItem;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ExplosivesConfig {
    private static Material defaultMaterial = Material.SPONGE;

    private List<EItem> explosives;
    private Set<Material> materials;
    private Map<String,Integer> names;
    private Map<String,Integer> keys;

    private ExplosivesConfig(List<EItem> explosives, Set<Material> materials, Map<String,Integer> names, Map<String,Integer> keys) {
        this.explosives = explosives;
        this.materials = materials;
        this.names = names;
        this.keys = keys;
    }

    public List<EItem> getExplosives() {
        return explosives;
    }

    public EItem searchExplosiveByItemStack(ItemStack itemStack) {
        if(isRegisteredMaterial(itemStack.getType())) {
            String displayName = itemStack.getItemMeta().getDisplayName();
            if (null != displayName)
                return searchExplosiveByName(displayName);
            else
                return null;
        } else
            return null;
    }

    public EItem searchExplosiveByName(String name) {
        Integer idx = names.get(name.toLowerCase());
        if (null != idx)
            return explosives.get(idx);
        else
            return null;
    }

    public EItem searchExplosiveByKey(String key) {
        Integer idx = keys.get(key.toLowerCase());
        if (null != idx)
            return explosives.get(idx);
        else
            return null;
    }

    public boolean isRegisteredMaterial(Material material) {
        return materials.contains(material);
    }

    public static boolean isDebugMode(FileConfiguration config, CustomLogger customLogger) {
        return ConfigReader.getBoolean(config, customLogger, "debug", "value flag", "debug", false);
    }

    public static ExplosivesConfig getFromConfig(FileConfiguration config, CustomLogger customLogger) {
        Material material = ConfigReader.getMaterial(config, customLogger, "service-material", "Default service material", defaultMaterial);

        List<EItem> explosives = new ArrayList<EItem>();
        Set<Material> materials = new HashSet<Material>();
        Map<String,Integer> names = new HashMap<String,Integer>();
        Map<String,Integer> keys = new HashMap<String,Integer>();

        List<?> explosivesConfig = config.getList("enabled-explosives");
        if((null == explosivesConfig) || (explosivesConfig.size() <= 0)) {
            customLogger.error("Empty 'enabled-explosives' list");
            return null;
        }

        boolean checkPermissions = ConfigReader.getBoolean(config, customLogger, "check-permissions", "value flag", "check-permissions", false);

        for(int i = 0; i < explosivesConfig.size(); i++) {
            Object item = explosivesConfig.get(i);
            if(null == item) {
                customLogger.error(String.format("Null key in 'enabled-explosives' at pos %d", i));
                continue;
            }
            String key = item.toString();
            if(key.length() < 1) {
                customLogger.error(String.format("Empty key in 'enabled-explosives' at pos %d", i));
                continue;
            }
            if(keys.containsKey(key.toLowerCase())) {
                customLogger.error(String.format("key '%s' in 'enabled-explosives' is not unique", key));
                continue;
            }

            if(null == config.getConfigurationSection(key)) {
                customLogger.error(String.format("Null definition of explosive-key '%s' from pos %d", key, i));
                continue;
            }

            EItem explosive = EItem.getFromConfig(material, config, customLogger, key, checkPermissions);
            if(null == explosive)
                continue;

            if(names.containsKey(explosive.getName().toLowerCase())) {
                customLogger.error(String.format("Name '%s' of explosive-key '%s' is not unique", explosive.getName(), key));
                continue;
            }

            explosives.add(explosive);
            materials.add(explosive.getMaterial());
            int idx = explosives.size() - 1;

            names.put(explosive.getName().toLowerCase(), idx);
            keys.put(key.toLowerCase(), idx);
        }

        if(explosives.size() < 1) {
            customLogger.error("There are no valid explosives definitions");
            return null;
        }

        return new ExplosivesConfig(explosives, materials, names, keys);
    }
}
