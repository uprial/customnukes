package com.gmail.uprial.customnukes.config;

public final class ConfigReaderResult {
    private final boolean error;
    private int intValue = 0;
    private float floatValue = 0.0F;

    private ConfigReaderResult() {
        error = true;
    }

    private ConfigReaderResult(int value) {
        error = false;
        intValue = value;
    }

    private ConfigReaderResult(float value) {
        error = false;
        floatValue = value;
    }

    @SuppressWarnings("StaticMethodOnlyUsedInOneClass")
    public static ConfigReaderResult errorResult() {
        return new ConfigReaderResult();
    }

    @SuppressWarnings("StaticMethodOnlyUsedInOneClass")
    public static ConfigReaderResult intResult(int value) {
        return new ConfigReaderResult(value);
    }

    @SuppressWarnings("StaticMethodOnlyUsedInOneClass")
    public static ConfigReaderResult floatResult(float value) {
        return new ConfigReaderResult(value);
    }

    public boolean isError() {
        return error;
    }

    public int getIntValue() {
        return intValue;
    }

    public float getFloatValue() {
        return floatValue;
    }
}
