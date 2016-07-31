package com.gmail.uprial.customnukes.common;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public final class EUtils {
    private static final Character ESCAPE_CHAR = '\\';

    public static int seconds2ticks(int seconds) {
        return seconds * 20;
    }

    public static String join(String[] items, Character delimiter) {
        String escapeString = ESCAPE_CHAR.toString();
        String[] escapedItems = new String[items.length];
        for(int i = 0; i < items.length; i++) {
            escapedItems[i] = items[i].replace(escapeString, escapeString + escapeString).replace(delimiter.toString(), escapeString + delimiter);
        }

        return StringUtils.join(escapedItems, delimiter);
    }

    public static String[] split(String value, Character delimiter) {
        List<String> items = new ArrayList<>();
        //noinspection NonConstantStringShouldBeStringBuffer
        String item = "";
        int l = value.length();
        int i = 0;
        while(i < l) {
            if(value.charAt(i) == ESCAPE_CHAR) {
                item += value.charAt(i + 1);
                i += 2;
            } else if (value.charAt(i) == delimiter) {
                items.add(item);
                item = "";
                i++;
            } else {
                item += value.charAt(i);
                i++;
            }
        }
        items.add(item);

        return items.toArray(new String[items.size()]);
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

    @SuppressWarnings("StaticMethodOnlyUsedInOneClass")
    public static double expBase(double degree, double result) {
        return Math.exp(Math.log(result) / degree);
    }

    @SuppressWarnings("StaticMethodOnlyUsedInOneClass")
    public static String lcFirst(String string) {
        return Character.toLowerCase(string.charAt(0)) + string.substring(1);
    }

}
