package com.gmail.uprial.customnukes.schema;

import com.gmail.uprial.customnukes.CustomNukes;
import com.gmail.uprial.customnukes.common.CustomLogger;
import com.gmail.uprial.customnukes.common.Nuke;
import com.gmail.uprial.customnukes.config.ConfigReaderSimple;
import com.gmail.uprial.customnukes.config.InvalidConfigException;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

@SuppressWarnings("ClassWithTooManyMethods")
public class EScenarioActionNuke extends AbstractEScenarioActionExplosion {
    @Override
    protected int defaultMinDelay() { return 2; }
    @Override
    protected int defaultMaxDelay() { return 4; }
    @Override
    protected int minDelayValue() { return 2; }
    @Override
    protected int maxDelayValue() { return 1000; }

    @Override
    protected double minRadius() { return Nuke.MAX_ENGINE_POWER + 1.0D; }
    @Override
    protected double maxRadius() { return 512; }

    public EScenarioActionNuke(String actionId) {
        super(actionId);
    }

    private static boolean defaultWitherFluids() { return false; }

    private boolean witherFluids = defaultWitherFluids();

    @Override
    public int execute(CustomNukes plugin, Location fromLocation, int delay) {
        return new Nuke(plugin).explode(fromLocation, null,
                (float)radius, witherFluids,
                delay, this::generateCurrentDelay, (final Long time) -> {});
    }

    @Override
    public void explode(CustomNukes plugin, Location location) {
        //
    }

    @Override
    public void loadFromConfig(FileConfiguration config, CustomLogger customLogger, String key, String title) throws InvalidConfigException {
        super.loadFromConfig(config, customLogger, key, title);

        witherFluids = ConfigReaderSimple.getBoolean(config, customLogger, key + ".wither-fluids",
                String.format("'wither-fluids' value of %s", title), defaultWitherFluids());
    }
}
