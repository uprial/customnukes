package com.gmail.uprial.customnukes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import com.gmail.uprial.customnukes.common.CustomLogger;
import com.gmail.uprial.customnukes.schema.EItem;

public class ExplosivesConfig {
	private static Material defaultMaterial = Material.SPONGE;

	private List<EItem> explosives;
	private Set<Material> materials;
	private Map<String,Integer> names;
	private Map<String,Integer> keys;
	private Material material;
	
    public ExplosivesConfig(FileConfiguration config, CustomLogger customLogger) {
    	readConfig(config, customLogger);
    }
    
    public List<EItem> getExplosives() {
    	return explosives;
    }
    
    public EItem searchExplosiveByItemStack(ItemStack itemStack) {
		if(isRegisteredMaterial(itemStack.getType()))
			return searchExplosiveByName(itemStack.getItemMeta().getDisplayName());
		else
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

	private void readConfig(FileConfiguration config, CustomLogger customLogger) {
		this.material = ConfigReader.getMaterial(config, customLogger, "service-material", "Default service material", defaultMaterial); 
		
		explosives = new ArrayList<EItem>();
		materials = new HashSet<Material>();
		names = new HashMap<String,Integer>();
		keys = new HashMap<String,Integer>();
		
		List<?> explosivesConfig = config.getList("enabled-explosives");
		if((null == explosivesConfig) || (explosivesConfig.size() < 0)) {
			customLogger.error("Empty 'enabled-explosives' list");
			return;
		}
		
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
			if(null == config.getConfigurationSection(key)) {
				customLogger.error(String.format("Null definition of explosive-key '%s' from pos %d", key, i));
				continue;
			}
			
			EItem explosive = EItem.getFromConfig(material, config, customLogger, key);
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
		
		if(explosives.size() < 1)
			customLogger.error("There are no valid explosives definitions");
	}
}
