package com.gmail.uprial.customnukes.schema;

import com.gmail.uprial.customnukes.config.ConfigReaderSimple;
import com.gmail.uprial.customnukes.config.InvalidConfigException;
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
    public static EScenarioAction getFromConfig(FileConfiguration config, CustomLogger customLogger, String key, String title, boolean isRepeaterAllowed) throws InvalidConfigException {
        if(config.getConfigurationSection(key + ".parameters") == null) {
            throw new InvalidConfigException(String.format("Null definition of parameters of %s", title));
        }

        int type = getTypeFromConfig(config, key, title, isRepeaterAllowed);

        I_EScenarioActionSubAction subAction;
        //noinspection IfStatementWithTooManyBranches
        if(type == TYPE_EXPLOSION) {
            subAction = new EScenarioActionExplosion(key);
        } else if(type == TYPE_EFFECT) {
            subAction = new EScenarioActionEffect(key);
        } else if(type == TYPE_REPEATER) {
            subAction = new EScenarioActionRepeater(key);
        } else if(type == TYPE_SEISMIC) {
            subAction = new EScenarioActionSeismic(key);
        } else {
            throw new InvalidConfigException(String.format("Impossible type %d of %s", type, title));
        }

        subAction.loadFromConfig(config, customLogger, key + ".parameters", title);

        return new EScenarioAction(subAction);
    }

    private static int getTypeFromConfig(FileConfiguration config, String key, String title, boolean isRepeaterAllowed) throws InvalidConfigException {
        String strType = ConfigReaderSimple.getString(config,key + ".type", String.format("type of %s", title));

        //noinspection IfStatementWithTooManyBranches
        if(strType.equalsIgnoreCase("explosion")) {
            return TYPE_EXPLOSION;
        } else if(strType.equalsIgnoreCase("effect")) {
            return TYPE_EFFECT;
        } else if(strType.equalsIgnoreCase("repeater") && isRepeaterAllowed) {
            return TYPE_REPEATER;
        } else if(strType.equalsIgnoreCase("seismic")) {
            return TYPE_SEISMIC;
        } else {
            throw new InvalidConfigException(String.format("Invalid type '%s' of %s", strType, title));
        }
    }
}
