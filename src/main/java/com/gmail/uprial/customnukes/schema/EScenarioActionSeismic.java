package com.gmail.uprial.customnukes.schema;

import com.gmail.uprial.customnukes.ConfigReader;
import com.gmail.uprial.customnukes.ConfigReaderResult;
import com.gmail.uprial.customnukes.CustomNukes;
import com.gmail.uprial.customnukes.common.CustomLogger;
import com.gmail.uprial.customnukes.common.Utils;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;

import java.util.List;

@SuppressWarnings("ClassWithTooManyMethods")
public class EScenarioActionSeismic extends AbstractEScenarioActionDelayed {
    private static final int ATTENUATION_NO = 1;
    private static final int ATTENUATION_LINE = 2;
    @SuppressWarnings("FieldCanBeLocal")
    private static final int ATTENUATION_EXP = 3;

    @Override
    protected int defaultMinDelay() { return 2; }
    @Override
    protected int defaultMaxDelay() { return 8; }
    @Override
    protected int minDelayValue() { return 2; }
    @Override
    protected int maxDelayValue() { return 1000; }

    @SuppressWarnings("SameReturnValue")
    private static float minMinRadius() { return 0; }
    @SuppressWarnings("SameReturnValue")
    private static float maxMinRadius() { return 5000; }
    @SuppressWarnings("SameReturnValue")
    private static float minMaxRadius() { return 1; }
    @SuppressWarnings("SameReturnValue")
    private static float maxMaxRadius() { return 5000; }

    @SuppressWarnings("SameReturnValue")
    private static float minEpicenterExplosionPower() { return 1; }
    @SuppressWarnings("SameReturnValue")
    private static float maxEpicenterExplosionPower() { return 320; }

    @SuppressWarnings({"SameReturnValue", "BooleanMethodNameMustStartWithQuestion"})
    private static boolean defaultPlayersOnly() { return true; }

    private float minRadius = 0.0F;
    private float maxRadius = 0.0F;
    private float epicenterExplosionPower = 0.0F;
    private boolean playersOnly = false;
    private int attenuation = 0;

    public EScenarioActionSeismic(String actionId) {
        super(actionId);
    }

    @Override
    public void explode(CustomNukes plugin, Location location) {
        List<?> entities;
        entities = playersOnly ? location.getWorld().getPlayers() : location.getWorld().getLivingEntities();

        int entitiesSize = entities.size();
        //noinspection ForLoopReplaceableByForEach
        for(int pid = 0; pid < entitiesSize; pid++) {
            LivingEntity entity = (LivingEntity)entities.get(pid);
            if(Utils.isInRange(location, entity.getLocation(), maxRadius)
                && ((minRadius <= 0.01) || !Utils.isInRange(location, entity.getLocation(), minRadius))) {
                explodeEntity(location, entity);
            }
        }
    }

    private void explodeEntity(Location location, LivingEntity entity) {
        double explosionPower;
        if(attenuation == ATTENUATION_NO) {
            explosionPower = epicenterExplosionPower;
        } else {
            double distance = location.distance(entity.getLocation());
            if (attenuation == ATTENUATION_LINE) {
                explosionPower = (epicenterExplosionPower * (maxRadius - distance)) / maxRadius;
            } else { //if (attenuation == attenuationExp) {
                double base = expBase(maxRadius, epicenterExplosionPower);
                explosionPower = Math.pow(base, maxRadius - distance);
            }
        }
        Location entityLocation = entity.getEyeLocation();
        double x = newCoordinate(location.getX(), entityLocation.getX());
        double y = newCoordinate(location.getY(), entityLocation.getY());
        double z = newCoordinate(location.getZ(), entityLocation.getZ());

        location.getWorld().createExplosion(x, y, z, (float)explosionPower, false, false);
    }

    private static double newCoordinate(double center, double target) {
        double dX = center - target;
        return target + (Math.min(0.25, Math.abs(dX)) * Math.signum(dX));
    }

    @Override
    public boolean isLoadedFromConfig(FileConfiguration config, CustomLogger customLogger, String key, String name) {
        if(!super.isLoadedFromConfig(config, customLogger, key, name)) {
            return false;
        }

        if(!isLoadedMinRadiusFromConfig(config, customLogger, key, name)) {
            return false;
        }

        if(!isLoadedMaxRadiusFromConfig(config, customLogger, key, name)) {
            return false;
        }

        if(!isLoadedEpicenterExplosionPowerFromConfig(config, customLogger, key, name)) {
            return false;
        }

        playersOnly = ConfigReader.getBoolean(config, customLogger, key + ".players-only", String.format("'players-only' value of action '%s'", name), defaultPlayersOnly());

        return isLoadedAttenuationFromConfig(config, customLogger, key, name);

    }

    private boolean isLoadedMinRadiusFromConfig(FileConfiguration config, CustomLogger customLogger, String key, String name) {
        ConfigReaderResult result = ConfigReader.getFloatComplex(config, customLogger, key + ".min-radius", String.format("Minimum radius of action '%s'", name), minMinRadius(), maxMinRadius());
        if(result.isError()) {
            return false;
        } else {
            minRadius = result.getFloatValue();
            return true;
        }
    }

    private boolean isLoadedMaxRadiusFromConfig(FileConfiguration config, CustomLogger customLogger, String key, String name) {
        ConfigReaderResult result = ConfigReader.getFloatComplex(config, customLogger, key + ".max-radius", String.format("Maximum radius of action '%s'", name), minMaxRadius(), maxMaxRadius());
        if(result.isError()) {
            return false;
        } else {
            maxRadius = result.getFloatValue();
            return true;
        }
    }

    private boolean isLoadedEpicenterExplosionPowerFromConfig(FileConfiguration config, CustomLogger customLogger, String key, String name) {
        ConfigReaderResult result = ConfigReader.getFloatComplex(config, customLogger, key + ".epicenter-explosion-power",
                String.format("Epicenter explosion power of action '%s'", name), minEpicenterExplosionPower(), maxEpicenterExplosionPower());
        if(result.isError()) {
            return false;
        } else {
            epicenterExplosionPower = result.getFloatValue();
            return true;
        }
    }

    private boolean isLoadedAttenuationFromConfig(FileConfiguration config, CustomLogger customLogger, String key, String name) {
        String strAttenuation = config.getString(key + ".attenuation");
        if(strAttenuation == null) {
            customLogger.error(String.format("Null attenuation of action '%s'", name));
            return false;
        }
        if(strAttenuation.length() < 1) {
            customLogger.error(String.format("Empty attenuation of action '%s'", name));
            return false;
        }

        int resAttenuation;
        //noinspection IfStatementWithTooManyBranches
        if(strAttenuation.equalsIgnoreCase("no") || strAttenuation.equalsIgnoreCase("false")) {
            resAttenuation = ATTENUATION_NO;
        } else if(strAttenuation.equalsIgnoreCase("line")) {
            resAttenuation = ATTENUATION_LINE;
        } else if(strAttenuation.equalsIgnoreCase("exp")) {
            resAttenuation = ATTENUATION_EXP;
        } else {
            customLogger.error(String.format("Invalid attenuation '%s' of action '%s'", strAttenuation, name));
            return false;
        }
        attenuation = resAttenuation;

        return true;
    }

    private static double expBase(double degree, double result) {
        return Math.exp(Math.log(result) / degree);
    }
}
