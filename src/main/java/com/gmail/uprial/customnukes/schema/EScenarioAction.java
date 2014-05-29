package com.gmail.uprial.customnukes.schema;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import com.gmail.uprial.customnukes.CustomNukes;
import com.gmail.uprial.customnukes.common.CustomLogger;

public class EScenarioAction {
	private static int typeExplosion = 1;
	private static int typeEffect = 2;
	private static int typeRepeater = 3;
	private static int typeSeismic = 4;
	
	private final I_EScenarioActionSubAction subAction;
	
	public EScenarioAction(I_EScenarioActionSubAction subAction) {
		this.subAction = subAction;
	}

	public int execute(CustomNukes plugin, Location location, int delay) {
		return subAction.execute(plugin, location, delay);
	}
	
	public static EScenarioAction getFromConfig(FileConfiguration config, CustomLogger customLogger, String key, String name, boolean isRepeaterAllowed) {
		int type = getTypeFromConfig(config, customLogger, key, name, isRepeaterAllowed);
		if(0 == type)
			return null;
		
		if(null == config.getConfigurationSection(key + ".parameters")) {
			customLogger.error(String.format("Null definition of parameters of action '%s'", name));
			return null;
		}

		I_EScenarioActionSubAction subAction;
		if(type == typeExplosion)
			subAction = new EScenarioActionExplosion(name);
		else if(type == typeEffect)
			subAction = new EScenarioActionEffect(name);
		else if(type == typeRepeater)
			subAction = new EScenarioActionRepeater(name);
		else if(type == typeSeismic)
			subAction = new EScenarioActionSeismic(name);
		else
			subAction = null;
			
		if(!subAction.isLoadedFromConfig(config, customLogger, key + ".parameters", name))
			return null;
		
		return new EScenarioAction(subAction);
	}
	
	private static int getTypeFromConfig(FileConfiguration config, CustomLogger customLogger, String key, String name, boolean isRepeaterAllowed) {
		String strType = config.getString(key + ".type");
		if(null == strType) {
			customLogger.error(String.format("Null type of action '%s'", name));
			return 0;
		}
		if(strType.length() < 1) {
			customLogger.error(String.format("Empty type of action '%s'", name));
			return 0;
		}

		int resType = 0;
		if(strType.equalsIgnoreCase("explosion"))
			resType = typeExplosion;
		else if(strType.equalsIgnoreCase("effect"))
			resType = typeEffect;
		else if(strType.equalsIgnoreCase("repeater") && isRepeaterAllowed)
			resType = typeRepeater;
		else if(strType.equalsIgnoreCase("seismic"))
			resType = typeSeismic;
		else
			customLogger.error(String.format("Invalid type '%s' of action '%s'", strType, name));
		
		return resType;
	}
}