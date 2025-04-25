package com.gmail.uprial.customnukes.common;

import org.bukkit.block.Block;

public class Formatter {
    public static String format(final Block block) {
        return String.format("%s[%s:%d:%d:%d]",
                block.getType(),
                block.getWorld().getName(),
                block.getX(), block.getY(), block.getZ());
    }
}
