package com.gmail.uprial.customnukes.common;

import java.util.logging.Level;
import java.util.logging.Logger;

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
}
