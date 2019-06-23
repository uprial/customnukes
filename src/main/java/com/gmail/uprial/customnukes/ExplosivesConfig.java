package com.gmail.uprial.customnukes;

import com.gmail.uprial.customnukes.common.CustomLogger;
import com.gmail.uprial.customnukes.config.ConfigReaderMaterial;
import com.gmail.uprial.customnukes.config.ConfigReaderSimple;
import com.gmail.uprial.customnukes.config.InvalidConfigException;
import com.gmail.uprial.customnukes.schema.EItem;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static com.gmail.uprial.customnukes.config.ConfigReaderSimple.getKey;

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
    public static boolean isDebugMode(FileConfiguration config, CustomLogger customLogger) throws InvalidConfigException {
        return ConfigReaderSimple.getBoolean(config, customLogger, "debug", "'debug' flag",false);
    }

    public static ExplosivesConfig getFromConfig(CustomNukes plugin, FileConfiguration config, CustomLogger customLogger) throws InvalidConfigException {
        Material material = ConfigReaderMaterial.getMaterial(config, customLogger, "service-material", "Default service material", DEFAULT_MATERIAL);

        List<EItem> explosives = new ArrayList<>();
        Set<Material> materials = new HashSet<>();
        Map<String,Integer> names = new HashMap<>();
        Map<String,Integer> keys = new HashMap<>();

        List<?> explosivesConfig = config.getList("enabled-explosives");
        if((explosivesConfig == null) || (explosivesConfig.size() <= 0)) {
            throw new InvalidConfigException("Empty 'enabled-explosives' list");
        }

        boolean checkPermissions = ConfigReaderSimple.getBoolean(config, customLogger, "check-permissions", "'check-permissions' flag", false);

        int explosivesConfigSize = explosivesConfig.size();
        for(int i = 0; i < explosivesConfigSize; i++) {
            String key = getKey(explosivesConfig.get(i), "'handlers'", i);
            String keyLC = key.toLowerCase(Locale.getDefault());
            if(keys.containsKey(keyLC)) {
                throw new InvalidConfigException(String.format("key '%s' in 'enabled-explosives' is not unique", key));
            }
            if(config.getConfigurationSection(key) == null) {
                throw new InvalidConfigException(String.format("Null definition of explosive-key '%s' from pos %d", key, i));
            }

            try {
                EItem explosive = EItem.getFromConfig(material, plugin, config, customLogger, key, checkPermissions);

                String nameLC = explosive.getName().toLowerCase(Locale.getDefault());
                if(names.containsKey(nameLC)) {
                    throw new InvalidConfigException(String.format("Name '%s' of explosive-key '%s' is not unique", explosive.getName(), key));
                }

                explosives.add(explosive);
                materials.add(explosive.getMaterial());
                int idx = explosives.size() - 1;

                names.put(nameLC, idx);
                keys.put(keyLC, idx);
            } catch (InvalidConfigException e) {
                customLogger.error(e.getMessage());
            }
        }

        if(explosives.size() < 1) {
            throw new InvalidConfigException("There are no valid explosives definitions");
        }

        return new ExplosivesConfig(explosives, materials, names, keys);
    }
}
