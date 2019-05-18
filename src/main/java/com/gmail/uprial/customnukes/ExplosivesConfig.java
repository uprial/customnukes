package com.gmail.uprial.customnukes;

import com.gmail.uprial.customnukes.common.CustomLogger;
import com.gmail.uprial.customnukes.schema.EItem;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public final class ExplosivesConfig {
    private static final Material DEFAULT_MATERIAL = Material.SPONGE;

    private final List<EItem> explosives;
    private final Set<Material> materials;
    private final Map<String,Integer> names;
    private final Map<String,Integer> keys;

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
            return (displayName != null) ? searchExplosiveByName(displayName) : null;
        } else {
            return null;
        }
    }

    public EItem searchExplosiveByName(String name) {
        Integer idx = names.get(name.toLowerCase(Locale.getDefault()));
        return (idx != null) ? explosives.get(idx) : null;
    }

    public EItem searchExplosiveByKey(String key) {
        Integer idx = keys.get(key.toLowerCase(Locale.getDefault()));
        return (idx != null) ? explosives.get(idx) : null;
    }

    public boolean isRegisteredMaterial(Material material) {
        return materials.contains(material);
    }

    @SuppressWarnings("StaticMethodOnlyUsedInOneClass")
    public static boolean isDebugMode(FileConfiguration config, CustomLogger customLogger) {
        return ConfigReader.getBoolean(config, customLogger, "debug", "value flag", "debug", false);
    }

    public static ExplosivesConfig getFromConfig(CustomNukes plugin, FileConfiguration config, CustomLogger customLogger) {
        Material material = ConfigReader.getMaterial(config, customLogger, "service-material", "Default service material", DEFAULT_MATERIAL);

        List<EItem> explosives = new ArrayList<>();
        Set<Material> materials = new HashSet<>();
        Map<String,Integer> names = new HashMap<>();
        Map<String,Integer> keys = new HashMap<>();

        List<?> explosivesConfig = config.getList("enabled-explosives");
        if((explosivesConfig == null) || (explosivesConfig.size() <= 0)) {
            customLogger.error("Empty 'enabled-explosives' list");
            return null;
        }

        boolean checkPermissions = ConfigReader.getBoolean(config, customLogger, "check-permissions", "value flag", "check-permissions", false);

        int explosivesConfigSize = explosivesConfig.size();
        for(int i = 0; i < explosivesConfigSize; i++) {
            Object item = explosivesConfig.get(i);
            if(item == null) {
                customLogger.error(String.format("Null key in 'enabled-explosives' at pos %d", i));
                continue;
            }
            String key = item.toString();
            if(key.length() < 1) {
                customLogger.error(String.format("Empty key in 'enabled-explosives' at pos %d", i));
                continue;
            }
            String keyLC = key.toLowerCase(Locale.getDefault());
            if(keys.containsKey(keyLC)) {
                customLogger.error(String.format("key '%s' in 'enabled-explosives' is not unique", key));
                continue;
            }

            if(config.getConfigurationSection(key) == null) {
                customLogger.error(String.format("Null definition of explosive-key '%s' from pos %d", key, i));
                continue;
            }

            EItem explosive = EItem.getFromConfig(material, plugin, config, customLogger, key, checkPermissions);
            if(explosive == null) {
                continue;
            }

            String nameLC = explosive.getName().toLowerCase(Locale.getDefault());
            if(names.containsKey(nameLC)) {
                customLogger.error(String.format("Name '%s' of explosive-key '%s' is not unique", explosive.getName(), key));
                continue;
            }

            explosives.add(explosive);
            materials.add(explosive.getMaterial());
            int idx = explosives.size() - 1;

            names.put(nameLC, idx);
            keys.put(keyLC, idx);
        }

        if(explosives.size() < 1) {
            customLogger.error("There are no valid explosives definitions");
            return null;
        }

        return new ExplosivesConfig(explosives, materials, names, keys);
    }
}
