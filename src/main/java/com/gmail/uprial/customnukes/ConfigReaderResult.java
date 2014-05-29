package com.gmail.uprial.customnukes;

public class ConfigReaderResult {
	private boolean error;
	private int intValue;
	private float floatValue;
	
	public ConfigReaderResult() {
		this.error = true;
	}
	
	public ConfigReaderResult(int value) {
		this.error = false;
		this.intValue = value;
	}
	
	public ConfigReaderResult(float value) {
		this.error = false;
		this.floatValue = value;
	}
	
	public static ConfigReaderResult errorResult() {
		return new ConfigReaderResult();
	}

	public static ConfigReaderResult intResult(int value) {
		return new ConfigReaderResult(value);
	}

	public static ConfigReaderResult floatResult(float value) {
		return new ConfigReaderResult(value);
	}
	
	public boolean isError() {
		return this.error;
	}
	
	public int getInt() {
		return this.intValue;
	}
	
	public float getFloat() {
		return this.floatValue;
	}
}
