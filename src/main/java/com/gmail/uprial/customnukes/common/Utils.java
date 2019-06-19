package com.gmail.uprial.customnukes.common;

import org.bukkit.Location;

public final class Utils {
    // A number of server ticks in one second
    public static final int SERVER_TICKS_IN_SECOND = 20;

    public static int seconds2ticks(int seconds) {
        return seconds * SERVER_TICKS_IN_SECOND;
    }

    public static boolean isInRange(Location location1, Location location2, double radius) {
        return (location1.getX() < (location2.getX() + radius))
                && (location1.getX() > (location2.getX() - radius))
                && (location1.getY() < (location2.getY() + radius))
                && (location1.getY() > (location2.getY() - radius))
                && (location1.getZ() < (location2.getZ() + radius))
                && (location1.getZ() > (location2.getZ() - radius))
                && (location1.distance(location2) < radius);
    }
}
