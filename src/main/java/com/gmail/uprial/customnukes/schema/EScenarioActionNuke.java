package com.gmail.uprial.customnukes.schema;

import com.gmail.uprial.customnukes.CustomNukes;
import com.gmail.uprial.customnukes.common.Nuke;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

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

    @Override
    public int execute(CustomNukes plugin, Location fromLocation, int delay) {
        return new Nuke(plugin).explode(fromLocation, (float)radius, delay, this::generateCurrentDelay);
    }

    @Override
    public void explode(CustomNukes plugin, Location location) {
        //
    }
}
