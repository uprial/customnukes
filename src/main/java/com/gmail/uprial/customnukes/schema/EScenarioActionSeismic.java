package com.gmail.uprial.customnukes.schema;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;

import com.gmail.uprial.customnukes.ConfigReader;
import com.gmail.uprial.customnukes.ConfigReaderResult;
import com.gmail.uprial.customnukes.CustomNukes;
import com.gmail.uprial.customnukes.common.CustomLogger;
import com.gmail.uprial.customnukes.common.EUtils;

public class EScenarioActionSeismic extends AbstractEScenarioActionDelayed {
	private static int attenuationNo = 1;
	private static int attenuationLine = 2;
	private static int attenuationExp = 3;

	protected int defaultMinDelay() { return 2; }
	protected int defaultMaxDelay() { return 8; }
	protected int minDelayValue() { return 2; } 
	protected int maxDelayValue() { return 1000; } 

	protected float minMinRadius() { return 0; }
	protected float maxMinRadius() { return 5000; }
	protected float minMaxRadius() { return 1; }
	protected float maxMaxRadius() { return 5000; }

	protected float minEpicenterExplosionPower() { return 1; }
	protected float maxEpicenterExplosionPower() { return 320; }
	
	protected boolean defaultPlayersOnly() { return true; }

	protected float minRadius;
	protected float maxRadius;
	private float epicenterExplosionPower;
	private boolean playersOnly;
	private int attenuation;
	
	public EScenarioActionSeismic(String actionId) {
		super(actionId);
	}
	
	public void explode(CustomNukes plugin, Location location) {
		List<?> entities;
		if(playersOnly)
			entities = location.getWorld().getPlayers();
		else
			entities = location.getWorld().getLivingEntities();

		for(int pid = 0; pid < entities.size(); pid++) {
			LivingEntity entity = (LivingEntity)entities.get(pid);
			if(EUtils.isInRange(location, entity.getLocation(), maxRadius)
				&& ((minRadius <= 0.01) || !EUtils.isInRange(location, entity.getLocation(), minRadius))) {
				explodeEntity(plugin, location, entity);
			}
		}
	}
	
	private void explodeEntity(CustomNukes plugin, Location location, LivingEntity entity) {
		double explosionPower;
		if(attenuation == attenuationNo) {
			explosionPower = epicenterExplosionPower;
		} else {
			double distance = location.distance(entity.getLocation());
			if (attenuation == attenuationLine) {
				explosionPower = epicenterExplosionPower * (maxRadius - distance) / maxRadius;
			} else { //if (attenuation == attenuationExp) {
				double base = EUtils.expBase(maxRadius, epicenterExplosionPower);
				explosionPower = Math.pow(base, maxRadius - distance);
			}
		}
		Location entityLocation = entity.getEyeLocation();
		double x = newCoord(location.getX(), entityLocation.getX());
		double y = newCoord(location.getY(), entityLocation.getY());
		double z = newCoord(location.getZ(), entityLocation.getZ());
		
		location.getWorld().createExplosion(x, y, z, (float)explosionPower, false, false);
	}
	
	private static double newCoord(double center, double target) {
		double dX = center - target;
		return target + Math.min(0.25, Math.abs(dX)) * Math.signum(dX);
	}
	
	public void setMinRadius(float minRadius) {
		this.minRadius = minRadius;
	}

	public void setMaxRadius(float maxRadius) {
		this.maxRadius = maxRadius;
	}

	public void setEpicenterExplosionPower(float epicenterExplosionPower) {
		this.epicenterExplosionPower = epicenterExplosionPower;
	}

	public void setPlayersOnly(boolean playersOnly) {
		this.playersOnly = playersOnly;
	}

	public void setAttenuation(int attenuation) {
		this.attenuation = attenuation;
	}

	public boolean isLoadedFromConfig(FileConfiguration config, CustomLogger customLogger, String key, String name) {
		if(!super.isLoadedFromConfig(config, customLogger, key, name))
			return false;
		
		if(!this.isLoadedMinRadiusFromConfig(config, customLogger, key, name))
			return false;

		if(!this.isLoadedMaxRadiusFromConfig(config, customLogger, key, name))
			return false;
		
		if(!this.isLoadedEpicenterExplosionPowerFromConfig(config, customLogger, key, name))
			return false;
		
		setPlayersOnly(ConfigReader.getBoolean(config, customLogger, key + ".players-only", "'players-only' value of action", name, defaultPlayersOnly()));

		if(!this.isLoadedAttenuationFromConfig(config, customLogger, key, name))
			return false;

		return true;
	}
	
	private boolean isLoadedMinRadiusFromConfig(FileConfiguration config, CustomLogger customLogger, String key, String name) {
		ConfigReaderResult result = ConfigReader.getFloatComplex(config, customLogger, key + ".min-radius", "Minimum radius of action", name, minMinRadius(), maxMinRadius());
		if(result.isError())
			return false;
		else {
			setMinRadius(result.getFloat());
			return true;
		}
	}

	private boolean isLoadedMaxRadiusFromConfig(FileConfiguration config, CustomLogger customLogger, String key, String name) {
		ConfigReaderResult result = ConfigReader.getFloatComplex(config, customLogger, key + ".max-radius", "Maximum radius of action", name, minMaxRadius(), maxMaxRadius());
		if(result.isError())
			return false;
		else {
			setMaxRadius(result.getFloat());
			return true;
		}
	}

	private boolean isLoadedEpicenterExplosionPowerFromConfig(FileConfiguration config, CustomLogger customLogger, String key, String name) {
		ConfigReaderResult result = ConfigReader.getFloatComplex(config, customLogger, key + ".epicenter-explosion-power",
																	"Epicenter explosion power of action", name, minEpicenterExplosionPower(), maxEpicenterExplosionPower());
		if(result.isError())
			return false;
		else {
			setEpicenterExplosionPower(result.getFloat());
			return true;
		}
	}
	
	private boolean isLoadedAttenuationFromConfig(FileConfiguration config, CustomLogger customLogger, String key, String name) {
		String strAttenuation = config.getString(key + ".attenuation");
		if(null == strAttenuation) {
			customLogger.error(String.format("Null attenuation of action '%s'", name));
			return false;
		}
		if(strAttenuation.length() < 1) {
			customLogger.error(String.format("Empty attenuation of action '%s'", name));
			return false;
		}

		int resAttenuation = 0;
		if(strAttenuation.equalsIgnoreCase("no") || strAttenuation.equalsIgnoreCase("false"))
			resAttenuation = attenuationNo;
		else if(strAttenuation.equalsIgnoreCase("line"))
			resAttenuation = attenuationLine;
		else if(strAttenuation.equalsIgnoreCase("exp"))
			resAttenuation = attenuationExp;
		else {
			customLogger.error(String.format("Invalid attenuation '%s' of action '%s'", strAttenuation, name));
			return false;
		}
		setAttenuation(resAttenuation);
		
		return true;
	}	
}
