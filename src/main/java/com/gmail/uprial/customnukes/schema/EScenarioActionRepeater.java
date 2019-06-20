package com.gmail.uprial.customnukes.schema;

import com.gmail.uprial.customnukes.common.Utils;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitTask;

import com.gmail.uprial.customnukes.ConfigReader;
import com.gmail.uprial.customnukes.ConfigReaderResult;
import com.gmail.uprial.customnukes.CustomNukes;
import com.gmail.uprial.customnukes.common.CustomLogger;

public class EScenarioActionRepeater extends AbstractEScenarioActionDelayed {

    @Override
    protected int defaultMinDelay() { return 2; }
    @Override
    protected int defaultMaxDelay() { return 60; }
    @Override
    protected int minDelayValue() { return 2; }
    @Override
    protected int maxDelayValue() { return 10000; }

    @SuppressWarnings("SameReturnValue")
    private static int minDuration() { return 1; }
    @SuppressWarnings("SameReturnValue")
    private static int maxDuration() { return 864000; }

    @SuppressWarnings("SameReturnValue")
    private static int minInterval() { return 20; }
    @SuppressWarnings("SameReturnValue")
    private static int maxInterval() { return 6000; }
    @SuppressWarnings("SameReturnValue")
    private static int defaultInterval() { return 40; }

    private int duration = 0;
    private int interval = 0;
    private EScenario scenario = null;

    public EScenarioActionRepeater(String actionId) {
        super(actionId);
        EScenarioActionRepeaterMap.INSTANCE.set(actionId, this);
    }

    @Override
    public void explode(CustomNukes plugin, Location location) {
        explodeEx(plugin, location, Utils.seconds2ticks(duration) / interval);
    }

    public void explodeEx(CustomNukes plugin, Location location, int runsCount) {
        BukkitTask task = new TaskEScenarioActionRepeaterAction(this, plugin, location, runsCount).runTaskTimer(plugin, 0, interval);
        plugin.getRepeaterTaskStorage().insert(location, getActionId(), task, runsCount);
    }

    public void executeAction(CustomNukes plugin, Location location, int taskId, int runsCount) {
        scenario.execute(plugin, location);
        plugin.getRepeaterTaskStorage().update(location, getActionId(), taskId, runsCount);
    }

    public void finishAction(CustomNukes plugin, Location location, int taskId) {
        plugin.getRepeaterTaskStorage().delete(location, getActionId(), taskId);
    }

    @Override
    public boolean isLoadedFromConfig(FileConfiguration config, CustomLogger customLogger, String key, String title) {
        if(!super.isLoadedFromConfig(config, customLogger, key, title)) {
            return false;
        }

        if(!isLoadedDurationFromConfig(config, customLogger, key, title)) {
            return false;
        }

        interval = ConfigReader.getInt(config, customLogger, key + ".interval", String.format("interval of %s", title), minInterval(), maxInterval(), defaultInterval());

        EScenario scenario = EScenario.getFromConfig(config, customLogger, key, String.format("scenario of %s", title), false);
        if(scenario == null) {
            return false;
        }
        this.scenario = scenario;

        return true;
    }

    private boolean isLoadedDurationFromConfig(FileConfiguration config, CustomLogger customLogger, String key, String title) {
        ConfigReaderResult result = ConfigReader.getIntComplex(config, customLogger, key + ".duration", String.format("duration of %s", title), minDuration(), maxDuration());
        if(result.isError()) {
            return false;
        } else {
            duration = result.getIntValue();
            return true;
        }
    }
}
