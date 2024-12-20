package com.gmail.uprial.customnukes.schema;

import com.gmail.uprial.customnukes.config.ConfigReaderNumbers;
import com.gmail.uprial.customnukes.config.ConfigReaderSimple;
import com.gmail.uprial.customnukes.CustomNukes;
import com.gmail.uprial.customnukes.common.CustomLogger;
import com.gmail.uprial.customnukes.common.Utils;
import com.gmail.uprial.customnukes.config.InvalidConfigException;
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
    private static double minMinRadius() { return 0; }
    @SuppressWarnings("SameReturnValue")
    private static double maxMinRadius() { return 5000; }
    @SuppressWarnings("SameReturnValue")
    private static double minMaxRadius() { return 1; }
    @SuppressWarnings("SameReturnValue")
    private static double maxMaxRadius() { return 5000; }

    @SuppressWarnings("SameReturnValue")
    private static double minEpicenterExplosionPower() { return 1; }
    @SuppressWarnings("SameReturnValue")
    private static double maxEpicenterExplosionPower() { return 16; }

    @SuppressWarnings({"SameReturnValue", "BooleanMethodNameMustStartWithQuestion"})
    private static boolean defaultPlayersOnly() { return true; }

    private double minRadius = 0.0F;
    private double maxRadius = 0.0F;
    private double epicenterExplosionPower = 0.0F;
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
    public void loadFromConfig(FileConfiguration config, CustomLogger customLogger, String key, String title) throws InvalidConfigException {
        super.loadFromConfig(config, customLogger, key, title);

        minRadius = ConfigReaderNumbers.getDouble(config, customLogger, key + ".min-radius",
                String.format("minimum radius of %s", title), minMinRadius(), maxMinRadius());
        maxRadius = ConfigReaderNumbers.getDouble(config, customLogger, key + ".max-radius",
                String.format("maximum radius of %s", title), minMaxRadius(), maxMaxRadius());
        epicenterExplosionPower = ConfigReaderNumbers.getDouble(config, customLogger, key + ".epicenter-explosion-power",
                String.format("Epicenter explosion power of %s", title), minEpicenterExplosionPower(), maxEpicenterExplosionPower());
        playersOnly = ConfigReaderSimple.getBoolean(config, customLogger, key + ".players-only",
                String.format("'players-only' value of %s", title), defaultPlayersOnly());
        attenuation = getAttenuationFromConfig(config, customLogger, key, title);

    }

    private int getAttenuationFromConfig(FileConfiguration config, CustomLogger customLogger, String key, String title) throws InvalidConfigException {
        String strAttenuation = ConfigReaderSimple.getString(config,key + ".attenuation", String.format("attenuation of %s", title));

        //noinspection IfStatementWithTooManyBranches
        if(strAttenuation.equalsIgnoreCase("no") || strAttenuation.equalsIgnoreCase("false")) {
            return ATTENUATION_NO;
        } else if(strAttenuation.equalsIgnoreCase("line")) {
            return ATTENUATION_LINE;
        } else if(strAttenuation.equalsIgnoreCase("exp")) {
            return ATTENUATION_EXP;
        } else {
            throw new InvalidConfigException(String.format("Invalid attenuation '%s' of %s", strAttenuation, title));
        }
    }

    private static double expBase(double degree, double result) {
        return Math.exp(Math.log(result) / degree);
    }
}
