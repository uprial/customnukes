package com.gmail.uprial.customnukes.schema;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import com.gmail.uprial.customnukes.CustomNukes;
import com.gmail.uprial.customnukes.common.CustomLogger;

public final class EScenarioAction {
    private static final int TYPE_EXPLOSION = 1;
    private static final int TYPE_EFFECT = 2;
    private static final int TYPE_REPEATER = 3;
    private static final int TYPE_SEISMIC = 4;

    private final I_EScenarioActionSubAction subAction;

    private EScenarioAction(I_EScenarioActionSubAction subAction) {
        this.subAction = subAction;
    }

    public int execute(CustomNukes plugin, Location location, int delay) {
        return subAction.execute(plugin, location, delay);
    }

    @SuppressWarnings("BooleanParameter")
    public static EScenarioAction getFromConfig(FileConfiguration config, CustomLogger customLogger, String key, String name, boolean isRepeaterAllowed) {
        int type = getTypeFromConfig(config, customLogger, key, name, isRepeaterAllowed);
        if(type == 0) {
            return null;
        }

        if(config.getConfigurationSection(key + ".parameters") == null) {
            customLogger.error(String.format("Null definition of parameters of action '%s'", name));
            return null;
        }

        I_EScenarioActionSubAction subAction;
        //noinspection IfStatementWithTooManyBranches
        if(type == TYPE_EXPLOSION) {
            subAction = new EScenarioActionExplosion(name);
        } else if(type == TYPE_EFFECT) {
            subAction = new EScenarioActionEffect(name);
        } else if(type == TYPE_REPEATER) {
            subAction = new EScenarioActionRepeater(name);
        } else if(type == TYPE_SEISMIC) {
            subAction = new EScenarioActionSeismic(name);
        } else {
            return null;
        }

        if(!subAction.isLoadedFromConfig(config, customLogger, key + ".parameters", name)) {
            return null;
        }

        return new EScenarioAction(subAction);
    }

    private static int getTypeFromConfig(FileConfiguration config, CustomLogger customLogger, String key, String name, boolean isRepeaterAllowed) {
        String strType = config.getString(key + ".type");
        if(strType == null) {
            customLogger.error(String.format("Null type of action '%s'", name));
            return 0;
        }
        if(strType.length() < 1) {
            customLogger.error(String.format("Empty type of action '%s'", name));
            return 0;
        }

        int resType = 0;
        //noinspection IfStatementWithTooManyBranches
        if(strType.equalsIgnoreCase("explosion")) {
            resType = TYPE_EXPLOSION;
        } else if(strType.equalsIgnoreCase("effect")) {
            resType = TYPE_EFFECT;
        } else if(strType.equalsIgnoreCase("repeater") && isRepeaterAllowed) {
            resType = TYPE_REPEATER;
        } else if(strType.equalsIgnoreCase("seismic")) {
            resType = TYPE_SEISMIC;
        } else {
            customLogger.error(String.format("Invalid type '%s' of action '%s'", strType, name));
        }

        return resType;
    }
}
