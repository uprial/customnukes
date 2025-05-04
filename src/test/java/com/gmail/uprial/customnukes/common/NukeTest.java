package com.gmail.uprial.customnukes.common;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.Test;

import static com.gmail.uprial.customnukes.common.Nuke.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class NukeTest {
    private static class NukeMock extends Nuke {
        private final Location zeroLocation;

        NukeMock() {
            super(mock(JavaPlugin.class));

            final Block block = mock(Block.class);

            final World world = mock(World.class);
            when(world.getBlockAt(any())).thenReturn(block);

            zeroLocation = new Location(world, 0, 0, 0);
        }

        void explode(final float radius) {
            super.explode(zeroLocation, radius, 0, () -> 0);
        }
    }

    private static class NukeLayersCounter extends NukeMock {
        private int layers;

        NukeLayersCounter() {
            super();
        }

        int test(final float radius) {
            layers = 0;

            explode(radius);

            return layers;
        }

        @Override
        void explode(final Location fromLocation, final float explosionRadius, final float sphereRadius) {
            layers++;
        }

        @Override
        void explode(final Location fromLocation) {
            // nop
        }
    }

    private static class NukeExplosionsCounter extends NukeMock {
        private int explosions;

        NukeExplosionsCounter() {
            super();
        }

        int test(final float radius) {
            explosions = 0;

            explode(radius);

            return explosions;
        }

        @Override
        void explode(final Location fromLocation) {
            explosions++;
        }
    }

    @Test
    public void testLayers() {
        final NukeLayersCounter nuke = new NukeLayersCounter();

        for(int i = 0; i <= 11; i++) {
            assertEquals(String.format("r: %d", i), 0, nuke.test(i));
        }
        for(int i = 12; i <= 19; i++) {
            assertEquals(String.format("r: %d", i), 1, nuke.test(i));
        }
        for(int i = 20; i <= 27; i++) {
            assertEquals(String.format("r: %d", i), 2, nuke.test(i));
        }
        assertEquals(3, nuke.test(28));
    }

    @Test
    public void testGetExplosionDistance() {
        assertEquals(8.0f, getExplosionDistance(20.0f, 8.0f), 0.1f);
        assertEquals(18.6f, getExplosionDistance(20.0f, 16.0f), 0.1f);
        // Sphere radius can't be 20.0f in practice
        assertEquals(24.0f, getExplosionDistance(20.0f, 20.0f), 0.1f);

        assertEquals(8.0f, getExplosionDistance(24.0f, 8.0f), 0.1f);
        assertEquals(16.0f, getExplosionDistance(24.0f, 16.0f), 0.1f);
        assertEquals(24.0f, getExplosionDistance(24.0f, 24.0f), 0.1f);

        assertEquals(8.0f, getExplosionDistance(120.0f, 8.0f), 0.1f);
        assertEquals(9.1f, getExplosionDistance(120.0f, 16.0f), 0.1f);
        assertEquals(22.8f, getExplosionDistance(120.0f, 112.0f), 0.1f);
        assertEquals(24.0f, getExplosionDistance(120.0f, 120.0f), 0.1f);
    }

    @Test
    public void testGetDensity() {
        assertEquals(0, getDensity(1.0f, 8.0f));

        assertEquals(4, getDensity(8.0f, 8.0f));
        assertEquals(16, getDensity(16.0f, 8.0f));
        assertEquals(36, getDensity(24.0f, 8.0f));

        assertEquals(900, getDensity(120.0f, 8.0f));
    }

    @Test
    public void testGetDecayedDensity() {
        assertEquals(0, getDecayedDensity(120.0f, 1.0f));

        assertEquals(4, getDecayedDensity(120.0f, 8.0f));
        assertEquals(12, getDecayedDensity(120.0f, 16.0f));
        assertEquals(22, getDecayedDensity(120.0f, 24.0f));

        assertEquals(100, getDecayedDensity(120.0f, 120.0f));
    }

    @Test
    public void testExplosions() {
        final NukeExplosionsCounter nuke = new NukeExplosionsCounter();

        for(int i = 0; i <= 11; i++) {
            assertEquals(String.format("r: %d", i), 1, nuke.test(i));
        }
        for(int i = 12; i <= 19; i++) {
            assertEquals(String.format("r: %d", i), 5, nuke.test(i));
        }
        for(int i = 20; i <= 22; i++) {
            assertEquals(String.format("r: %d", i), 8, nuke.test(i));
        }
        for(int i = 23; i <= 26; i++) {
            assertEquals(String.format("r: %d", i), 9, nuke.test(i));
        }
        assertEquals(10, nuke.test(27));
        assertEquals(15, nuke.test(28));
        assertEquals(16, nuke.test(29));
        assertEquals(16, nuke.test(30));
        assertEquals(17, nuke.test(31));
        assertEquals(18, nuke.test(32));
    }
}