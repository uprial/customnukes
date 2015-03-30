package com.gmail.uprial.customnukes;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import com.gmail.uprial.customnukes.common.CustomLogger;
import com.gmail.uprial.customnukes.common.EUtils;

public class ConfigReader {
	public static int getInt(FileConfiguration config, CustomLogger customLogger, String key, String title, String name, int min, int max, int defaultValue) {
		int value = defaultValue;
		
		if(null == config.getString(key)) {
			customLogger.debug(String.format("Empty %s '%s'. Use default value %d", EUtils.lcFirst(title), name, defaultValue));
		} else {
			int intValue = config.getInt(key);
			if(min > intValue)
				customLogger.error(String.format("%s '%s' should be at least %d. Use default value %d", title, name, min, defaultValue));
			else if(max < intValue)
				customLogger.error(String.format("%s '%s' should be at most %d. Use default value %d", title, name, max, defaultValue));
			else
				value = intValue;
		}
		
		return value;
	}
	
	public static String getString(FileConfiguration config, CustomLogger customLogger, String key, String title) {
		String name = config.getString(key);
		
		if(null == name) {
			customLogger.error(String.format("Null/Empty %s '%s'", title, key));
			return null;
		}
		
		return name;
	}
	
	public static List<String> getStringList(FileConfiguration config, CustomLogger customLogger, String key, String title, String name) {
		List<?> lines = config.getList(key);
		if(null != lines) {
			List<String> description = new ArrayList<String>();
			for(int i = 0; i < lines.size(); i++)
				description.add(lines.get(i).toString());
			
			return description;
		} else {
			customLogger.warning(String.format("Empty %s '%s'", title, name));
			return null;
		}
	}
	
	public static boolean getBoolean(FileConfiguration config, CustomLogger customLogger, String key, String title, String name, boolean defaultValue) {
		boolean value = defaultValue;
		
		if(null == config.getString(key)) {
			customLogger.debug(String.format("Empty %s '%s'. Use default value %b", title, name, defaultValue));
		} else {
			String strValue = config.getString(key);
			if(strValue.equalsIgnoreCase("true"))
				value = true;
			else if(strValue.equalsIgnoreCase("false"))
				value = false;
			else
				customLogger.error(String.format("Invalid %s '%s'. Use default value %b", title, name, defaultValue));
		}

		return value;
	}
	
	public static ConfigReaderResult getFloatComplex(FileConfiguration config, CustomLogger customLogger, String key, String title, String name, float min, float max) {
		if(null == config.getString(key)) {
			customLogger.error(String.format("Null %s '%s", EUtils.lcFirst(title), name));
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

	public static ConfigReaderResult getIntComplex(FileConfiguration config, CustomLogger customLogger, String key, String title, String name, int min, int max) {
		if(null == config.getString(key)) {
			customLogger.error(String.format("Null %s '%s", EUtils.lcFirst(title), name));
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
		if(null == strMaterial)
			customLogger.debug(String.format("Empty %s, use default '%s'", EUtils.lcFirst(title), defaultMaterial));
		else {
			Material tmpMaterial = Material.getMaterial(strMaterial);
			if(null == tmpMaterial)
				customLogger.error(String.format("Unknown %s '%s', use default '%s'", EUtils.lcFirst(title), tmpMaterial, defaultMaterial));
			else if(!tmpMaterial.isBlock())
				customLogger.error(String.format("%s '%s' is not block, use default '%s'", title, tmpMaterial, defaultMaterial));
			else if(tmpMaterial.hasGravity())
				customLogger.error(String.format("%s '%s' has gravity, use default '%s'", title, tmpMaterial, defaultMaterial));
			else if(!tmpMaterial.isSolid())
				customLogger.error(String.format("%s '%s' is not solid, use default '%s'", title, tmpMaterial, defaultMaterial));
			else if(tmpMaterial.isTransparent())
				customLogger.error(String.format("%s '%s' is transparent, use default '%s'", title, tmpMaterial, defaultMaterial));
			else
				resMaterial = tmpMaterial;
		}
		
		return resMaterial;
	}	
}
