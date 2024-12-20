package com.gmail.uprial.customnukes.schema;

import com.gmail.uprial.customnukes.CustomNukes;
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

    /*
        Explosions with radius greater than 16 destroy blocks extremely effectively.
        There are definitely no way to destroy big amount of blocks by single explosion.
     */
    private static final float INTERNAL_POWER = 16.0f;

    /*
        A ball of explosions is made of many spheres,
        increasing their radius step by step.
     */
    private static final float STEP = INTERNAL_POWER / 2.0f;

    @Override
    protected double minRadius() { return INTERNAL_POWER + 1.0D; }
    @Override
    protected double maxRadius() { return 512; }

    public EScenarioActionNuke(String actionId) {
        super(actionId);
    }

    @Override
    public int execute(CustomNukes plugin, Location fromLocation, int delay) {
        delay += generateCurrentDelay();
        plugin.scheduleDelayed(new TaskEScenarioActionDelayedExplode(this, plugin, fromLocation), delay);

        int spheres = (int)Math.round(radius / STEP);
        /*
            No last sphere.

            For example, for a 24-radius ball,
            spheres should be 0, 8, 16 but not 24.

            total: 4990 > 1655 > 860 > 760
         */
        for(int i = 1; i < spheres; i++) {
            delay += generateCurrentDelay();
            plugin.scheduleDelayed(
                    new TaskEScenarioActionNuke(this, plugin, fromLocation, i * STEP), delay);
        }

        return delay;
    }

    @Override
    public void explode(CustomNukes plugin, Location location) {
        exp(location);
    }

    /*
        The main generation method idea:
        https://stackoverflow.com/questions/63726093/how-to-easily-make-a-mesh-of-sphere-3d-points-over-a-vector
     */
    public void explode(CustomNukes plugin, Location fromLocation, double r) {
        /*
            To distribute angles evenly,
            must be a number not equal to 1.0D nor 0.0D, closer to 1.0D.
         */
        double gr = (3.0D - Math.sqrt(5.0D));

        /*
            Though it might seem enough to split a big sphere into many smaller ones,
            we need to create explosions more frequently closer to the epicenter
            to overcome block resistance in the epicenter.

            So, moving from the epicenter to the periphery,
            we increase the distance between explosions.

            R: full > decayed
            8: 4 > 4 > 4
            16: 16 > 13 > 10
            24: 36 > 25 > 19
            32: 64 > 40 > 28
            40: 100 > 56 > 37
            48: 144 > 74 > 45
            56: 196 > 92 > 53
            64: 256 > 109 > 60
            72: 354 > 127 > 67
            80: 400 > 145 > 74
            88: 484 > 162 > 80
            96: 576 > 178 > 86
            104: 676 > 195 > 91
            112: 784 > 210 > 96
            120: 900 > 225 > 100

            total: 4990 > 1655 > 860
         */
        float epicenterDistance = STEP;
        float peripheryDistance = INTERNAL_POWER + STEP;

        double distance = epicenterDistance
                + r / radius * (peripheryDistance - epicenterDistance);
        /*
            The surface area of a sphere of radius r1 is 4 * pi * r1^2.

            The surface area of a circle of radius r2 = pi * r2^2.

            To cover a sphere of radius r1 with circles of radius r2,
            4 * pi * r1^2 / (pi * r2^2) = 4 * r1^2 / r2^2 = 4 * (r1 / r2)^2 are needed.
         */
        int number = (int)Math.ceil(4 * Math.pow(r / distance, 2.0D));
        //System.out.printf("r: %.2f, number: %d%n", r, number);
        for(int i = 0; i < number; i++){
            double y = 1.0D - 2.0D * (double)i / number;
            double angle1 = Math.acos(y);
            double angle2 = Math.PI * gr * i;
            double x = Math.sin(angle1) * Math.cos(angle2);
            double z = Math.sin(angle1) * Math.sin(angle2);

            Location toLocation = fromLocation.clone().add(x * r, y * r, z * r);
            Vector direction = getDirection(fromLocation, toLocation);
            RayTraceResult rayTraceResult = fromLocation.getWorld().rayTraceBlocks(
                    fromLocation,
                    direction,
                    toLocation.distance(fromLocation));

            // Make an explosion in front of the block found.
            if(rayTraceResult != null) {
                toLocation = rayTraceResult.getHitPosition().toLocation(toLocation.getWorld());
                // Direction is always of 1.0 length.
                toLocation.subtract(direction);
            }

            exp(toLocation);
        }
    }

    private void exp(Location location) {
        //location.getWorld().getBlockAt(location).setType(Material.MAGMA_BLOCK);
        location.getWorld().createExplosion(location, INTERNAL_POWER, true);
    }

    /*
    private static String format(Location location) {
        if(location == null) {
            return "null";
        }
        return String.format("[w: %s, x: %.0f, y: %.0f, z: %.0f]",
                (location.getWorld() != null) ? location.getWorld().getName() : "empty",
                location.getX(), location.getY(), location.getZ());
    }

    private static boolean isRayTraceable(final Location fromLocation, final Location toLocation) {
        System.out.printf("Ray from %s to %s in direction %s and distance %.2f...%n",
                format(fromLocation),
                format(toLocation),
                format(getDirection(fromLocation, toLocation).toLocation(toLocation.getWorld())),
                toLocation.distance(fromLocation));
        System.out.printf("%s%n",
                fromLocation.getWorld().rayTraceBlocks(
                        fromLocation,
                        getDirection(fromLocation, toLocation),
                        toLocation.distance(fromLocation)
                ));
        return (null == fromLocation.getWorld().rayTraceBlocks(
                fromLocation,
                getDirection(fromLocation, toLocation),
                toLocation.distance(fromLocation)
        ));
    }*/

    private static Vector getDirection(final Location fromLocation, final Location toLocation) {
        final Location direction = toLocation.clone().subtract(fromLocation);
        final double length = direction.length();

        return new Vector(
                direction.getX() / length,
                direction.getY() / length,
                direction.getZ() / length
        );
    }
}
