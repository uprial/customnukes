package com.gmail.uprial.customnukes.common;

import com.google.common.collect.ImmutableSet;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Nuke {
    /*
        According to https://minecraft.wiki/w/Explosion,
        explosions with radius greater than 16 destroy blocks extremely ineffectively:
        there is definitely no way to destroy big amount of blocks by single explosion.
     */
    public static final float MAX_ENGINE_POWER = 16.0f;

    /*
        According to https://minecraft.wiki/w/Explosion,
        fluids have high blast resistance, causing it to absorb any normal blasts.

        That prevents making huge nukes,
        so I wither fluid blocks in the explosion radius
        when the blocks are visible from the explosion epicenter.
     */
    private static final int FLUID_WITHERING_POWER = 12;

    /*
        A ball of explosions is made of many spheres,
        increasing their radius step by step.
     */
    private static final float STEP = MAX_ENGINE_POWER / 2.0f;

    /*
        The whole class could be easily implemented in static,
        but that would prevent its proper mocking in tests.
     */
    private final JavaPlugin plugin;
    public Nuke(final JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /*
        To not freeze the server via big explosions,
        explosions are scheduled.

        initialDelay = 0 means an immediate epicenter explosion.
        initialDelay = 0 AND period = 0 means all explosions are immediate.

        The explosions are supposed to be distributed evenly, but not ideally:
        the explosion isn't actually a smooth ball, especially for smaller values of radius.
     */
    public int explode(
            final Location fromLocation,
            final Entity source,
            final float explosionRadius,
            final boolean witherFluids,
            final int initialDelay,
            final Supplier<Integer> nextDelayGenerator,
            final Consumer<Long> callback) {

        final long start = System.currentTimeMillis();

        int delay = initialDelay;
        schedule(() -> explode(fromLocation, source, witherFluids), delay);

        final int spheres = Math.round(explosionRadius / STEP);
        /*
            No epicenter sphere.
            No last sphere.

            For example, for a 24-radius ball,
            spheres should be 8 and 16 but not 0 or 24.
         */
        for(int i = 1; i < spheres; i++) {
            delay += nextDelayGenerator.get();
            final float sphereRadius = i * STEP;
            schedule(() -> explode(fromLocation, source, explosionRadius, sphereRadius, witherFluids), delay);
        }

        delay += nextDelayGenerator.get();
        schedule(() -> {
            final long end = System.currentTimeMillis();
            callback.accept(end - start);
        }, delay);

        return delay;
    }

    /*
        The main generation method idea:
        https://stackoverflow.com/questions/63726093/how-to-easily-make-a-mesh-of-sphere-3d-points-over-a-vector
     */
    void explode(final Location fromLocation, final Entity source, final float explosionRadius, final float sphereRadius, final boolean witherFluids) {
        /*
            To distribute angles evenly,
            must be a number not equal to 1.0D nor 0.0D, closer to 1.0D.
         */
        final double gr = (3.0D - Math.sqrt(5.0D));

        final int density = getDecayedDensity(explosionRadius, sphereRadius);
        for(int i = 0; i < density; i++){
            final double y = 1.0D - 2.0D * (double)i / density;
            final double angle1 = Math.acos(y);
            final double angle2 = Math.PI * gr * i;
            final double x = Math.sin(angle1) * Math.cos(angle2);
            final double z = Math.sin(angle1) * Math.sin(angle2);

            Location toLocation = fromLocation.clone().add(x * sphereRadius, y * sphereRadius, z * sphereRadius);
            final Vector direction = getDirection(fromLocation, toLocation);
            final RayTraceResult rayTraceResult = fromLocation.getWorld().rayTraceBlocks(
                    fromLocation,
                    direction,
                    toLocation.distance(fromLocation),
                    FluidCollisionMode.ALWAYS);

            // Make an explosion in front of the block found.
            if(rayTraceResult != null) {
                toLocation = rayTraceResult.getHitPosition().toLocation(toLocation.getWorld());
                // Direction is always normalized to length of 1.0.
                toLocation.subtract(direction);
            }

            explode(toLocation, source, witherFluids);
        }
    }

    static float getExplosionDistance(final float explosionRadius, final float sphereRadius) {
        /*
            Though it might seem enough to split a big ball into many smaller spheres,
            we need to create explosions more frequently closer to the epicenter
            to overcome block resistance in the epicenter.

            So, moving from the epicenter to the periphery,
            we increase the distance between explosions.
         */
        final float epicenterExplosionDistance = STEP;
        final float peripheryExplosionDistance = MAX_ENGINE_POWER + STEP;

        return epicenterExplosionDistance
                + (sphereRadius - epicenterExplosionDistance)
                / (explosionRadius - epicenterExplosionDistance)
                * (peripheryExplosionDistance - epicenterExplosionDistance);

    }

    static int getDensity(final float sphereRadius, final float explosionDistance) {
        /*
            The surface area of a sphere of radius r1 is 4 * pi * r1^2.

            The surface area of a circle of radius r2 = pi * r2^2.

            To cover a sphere of radius r1 with circles of radius r2,
            4 * pi * r1^2 / (pi * r2^2) = 4 * r1^2 / r2^2 = 4 * (r1 / r2)^2 are needed.
         */
        return (int)Math.round(4 * Math.pow(sphereRadius / explosionDistance, 2.0D));
    }

    static int getDecayedDensity(final float explosionRadius, final float sphereRadius) {
        /*
            Radius: full density > decayed density
            8: 4 > 4
            16: 16 > 13
            24: 36 > 22
            ...
            120: 900 > 100
         */
        return getDensity(sphereRadius, getExplosionDistance(explosionRadius, sphereRadius));
    }

    void explode(final Location fromLocation, final Entity source, final boolean witherFluids) {
        if(witherFluids) {
            witherFluids(fromLocation);
        }
        /*
        Based on explosion radius 400 test:

        setFire sample time
          false 143,438
          false 137,296
           true 163,705
           true 158,700

        setFire=true brings 15% performance drawback.

        I decided to set the beautiful fire. :-)
         */
        fromLocation.getWorld().createExplosion(fromLocation, MAX_ENGINE_POWER, true);
        // NEW: fromLocation.getWorld().createExplosion(fromLocation, MAX_ENGINE_POWER, true, true, source);
    }

    private static final double EPSILON = 0.01d;
    private static final Set<Material> FLUIDS = ImmutableSet.<Material>builder()
            .add(Material.WATER)
            .add(Material.LAVA)
            .add(Material.BUBBLE_COLUMN)
            .build();
    void witherFluids(final Location fromLocation) {
        /*
            Since we're withering fluid,
            it's better to start from top layers that may affect lower levels.
         */
        for(int dy = FLUID_WITHERING_POWER; dy >= -FLUID_WITHERING_POWER; dy--) {
            for(int dx = -FLUID_WITHERING_POWER; dx <= FLUID_WITHERING_POWER; dx++) {
                for(int dz = -FLUID_WITHERING_POWER; dz <= FLUID_WITHERING_POWER; dz++) {
                    final Location toLocation = fromLocation.clone().add(dx, dy, dz);
                    final double distance = toLocation.distance(fromLocation);
                    if(distance <= FLUID_WITHERING_POWER + EPSILON) {
                        // If not the epicenter block
                        if(distance > EPSILON) {
                            // Wither only fluids visible from the epicenter
                            final RayTraceResult rayTraceResult = fromLocation.getWorld().rayTraceBlocks(
                                    fromLocation,
                                    getDirection(fromLocation, toLocation),
                                    distance,
                                    FluidCollisionMode.NEVER);
                            if (rayTraceResult != null) {
                                continue;
                            }
                        }

                        final Block block = fromLocation.getWorld().getBlockAt(toLocation);
                        if (FLUIDS.contains(block.getType())) {
                            block.setType(Material.AIR);
                        } else if (block.getBlockData() instanceof Waterlogged) {
                            /*
                                According to https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html#WATER,
                                Material.WATER BlockData is Levelled but not Waterlogged
                             */
                            final Waterlogged waterlogged = (Waterlogged) block.getBlockData();
                            if (waterlogged.isWaterlogged()) {
                                waterlogged.setWaterlogged(false);
                                block.setBlockData(waterlogged);
                            }
                        }
                    }
                }
            }
        }
    }

    private Vector getDirection(final Location fromLocation, final Location toLocation) {
        final Location direction = toLocation.clone().subtract(fromLocation);
        final double length = direction.length();

        return new Vector(
                direction.getX() / length,
                direction.getY() / length,
                direction.getZ() / length
        );
    }

    void schedule(final Runnable task, final long delay) {
        if(delay > 0) {
            plugin.getServer().getScheduler()
                    .scheduleSyncDelayedTask(plugin, task, delay);
        } else {
            // Run immediately.
            task.run();
        }
    }
}
