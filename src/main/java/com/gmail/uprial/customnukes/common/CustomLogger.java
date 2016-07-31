package com.gmail.uprial.customnukes.common;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CustomLogger {
    private boolean debug = true;

    private final Logger logger;

    public CustomLogger(Logger logger) {
        this.logger = logger;
    }

    public void setDebugMode(boolean value) {
        this.debug = value;
    }

    public boolean isDebugMode() {
        return this.debug;
    }

    public void error(String message) {
        logger.log(Level.SEVERE, "[ERROR] " + message);
    }

    public void warning(String message) {
        logger.log(Level.WARNING, "[WARNING] " + message);
    }

    public void debug(String message) {
        if (debug) {
            logger.log(Level.INFO, "[DEBUG] " + message);
        }
    }

    public void info(String message) {
        logger.log(Level.INFO, message);
    }

    public void userError(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.RED + "ERROR: " + message);
        logger.log(Level.INFO, "[user-error] <" + sender.getName() + ">: " + message);
    }

    public void userInfo(CommandSender sender, String message) {
        sender.sendMessage(message);
        logger.log(Level.INFO, "[user-info] <" + sender.getName() + ">: " + message);
    }
}
