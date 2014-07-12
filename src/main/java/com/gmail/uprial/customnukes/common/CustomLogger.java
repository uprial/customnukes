package com.gmail.uprial.customnukes.common;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CustomLogger {
	private final Logger logger;
	
	public CustomLogger(Logger logger) {
		this.logger = logger;
	}
	
	public void error(String message) {
		logger.log(Level.SEVERE, "[ERROR] " + message);
	}

	public void warning(String message) {
		logger.log(Level.WARNING, "[WARNING] " + message);
	}

	public void info(String message) {
		logger.info(message);
	}
	
	public void sendError(CommandSender sender, String message) {
    	message = "Error: " + message;
    	sender.sendMessage(ChatColor.RED + message);
    	info(message);
    }

	public void sendMessage(CommandSender sender, String message) {
    	sender.sendMessage(message);
    	info(message);
    }
}
