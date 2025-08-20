package com.gmail.uprial.customnukes.schema;

import com.gmail.uprial.customnukes.CustomNukes;
import com.gmail.uprial.customnukes.common.CustomLogger;
import com.gmail.uprial.customnukes.common.Utils;
import com.gmail.uprial.customnukes.config.ConfigReaderNumbers;
import com.gmail.uprial.customnukes.config.ConfigReaderSimple;
import com.gmail.uprial.customnukes.config.InvalidConfigException;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import static com.gmail.uprial.customnukes.common.Formatter.format;

public class EScenarioActionRepeater extends AbstractEScenarioActionDelayed {
    private static final String LOCATION_PLACEHOLDER = "%l";

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

    private String message = null;
    private int duration = 0;
    private int interval = 0;
    private EScenario scenario = null;

    public EScenarioActionRepeater(String actionId) {
        super(actionId);
        EScenarioActionRepeaterMap.INSTANCE.set(actionId, this);
    }

    @Override
    public void explode(CustomNukes plugin, Location location) {
        if(message != null) {
            for (final Player p : plugin.getServer().getOnlinePlayers()) {
                if (p.isValid()) {
                    final String lMessage;
                    if (p.getWorld().getUID().equals(location.getWorld().getUID())) {
                        lMessage = message.replace(LOCATION_PLACEHOLDER,
                                String.format("%.0f block distance", location.distance(p.getLocation())));
                    } else {
                        lMessage = message.replace(LOCATION_PLACEHOLDER,
                                "another world");
                    }
                    p.sendMessage(ChatColor.YELLOW + lMessage);
                }
            }
            plugin.getLogger().info(message.replace(LOCATION_PLACEHOLDER, format(location)));
        }

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
    public void loadFromConfig(FileConfiguration config, CustomLogger customLogger, String key, String title) throws InvalidConfigException {
        super.loadFromConfig(config, customLogger, key, title);

        if (config.get(key + ".message") == null) {
            message = null;
        } else {
            message = ConfigReaderSimple.getString(config, key + ".message",
                    String.format("message of %s", title));
        }
        duration = ConfigReaderNumbers.getInt(config, customLogger, key + ".duration",
                String.format("duration of %s", title), minDuration(), maxDuration());
        interval = ConfigReaderNumbers.getInt(config, customLogger, key + ".interval",
                String.format("interval of %s", title), minInterval(), maxInterval(), defaultInterval());
        scenario = EScenario.getFromConfig(config, customLogger, key,
                String.format("scenario of %s", title), false);
    }
}
