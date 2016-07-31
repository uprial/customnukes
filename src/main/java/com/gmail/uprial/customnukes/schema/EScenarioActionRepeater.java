package com.gmail.uprial.customnukes.schema;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitTask;

import com.gmail.uprial.customnukes.ConfigReader;
import com.gmail.uprial.customnukes.ConfigReaderResult;
import com.gmail.uprial.customnukes.CustomNukes;
import com.gmail.uprial.customnukes.common.CustomLogger;
import com.gmail.uprial.customnukes.common.EUtils;

public class EScenarioActionRepeater extends AbstractEScenarioActionDelayed {

    protected int defaultMinDelay() { return 2; }
    protected int defaultMaxDelay() { return 60; }
    protected int minDelayValue() { return 2; }
    protected int maxDelayValue() { return 10000; }

    protected int minDuration() { return 1; }
    protected int maxDuration() { return 864000; }

    protected int minInterval() { return 20; }
    protected int maxInterval() { return 6000; }
    protected int defaultInterval() { return 40; }

    private int duration;
    private int interval;
    private EScenario scenario;

    public EScenarioActionRepeater(String actionId) {
        super(actionId);
        EScenarioActionRepeaterMap.INSTANCE.set(actionId, this);
    }

    public void explode(CustomNukes plugin, Location location) {
        explodeEx(plugin, location, EUtils.seconds2ticks(duration) / interval);
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

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public void setScenario(EScenario scenario) {
        this.scenario = scenario;
    }

    public boolean isLoadedFromConfig(FileConfiguration config, CustomLogger customLogger, String key, String name) {
        if(!super.isLoadedFromConfig(config, customLogger, key, name))
            return false;

        if(!isLoadedDurationFromConfig(config, customLogger, key, name))
            return false;

        setInterval(ConfigReader.getInt(config, customLogger, key + ".interval", "Interval of action", name, minInterval(), maxInterval(), defaultInterval()));

        EScenario scenario = EScenario.getFromConfig(config, customLogger, key, name, false);
        if(null == scenario)
            return false;
        setScenario(scenario);

        return true;
    }

    private boolean isLoadedDurationFromConfig(FileConfiguration config, CustomLogger customLogger, String key, String name) {
        ConfigReaderResult result = ConfigReader.getIntComplex(config, customLogger, key + ".duration", "Duration of action", name, minDuration(), maxDuration());
        if(result.isError())
            return false;
        else {
            setDuration(result.getInt());
            return true;
        }
    }
}
