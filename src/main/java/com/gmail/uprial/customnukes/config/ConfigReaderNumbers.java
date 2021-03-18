package com.gmail.uprial.customnukes.config;

import com.gmail.uprial.customnukes.common.CustomLogger;
import org.bukkit.configuration.file.FileConfiguration;

import static com.gmail.uprial.customnukes.common.DoubleHelper.*;

public final class ConfigReaderNumbers {

    public static int getInt(FileConfiguration config, CustomLogger customLogger, String key, String title,
                             int min, int max) throws InvalidConfigException {
        return getIntInternal(config, customLogger, key, title, min, max, null);
    }

    public static int getInt(FileConfiguration config, CustomLogger customLogger, String key, String title,
                             int min, int max, @SuppressWarnings("SameParameterValue") int defaultValue) throws InvalidConfigException {
        return getIntInternal(config, customLogger, key, title, min, max, defaultValue);
    }

    private static int getIntInternal(FileConfiguration config, CustomLogger customLogger, String key, String title,
                                      int min, int max, Integer defaultValue) throws InvalidConfigException {
        if (min > max) {
            throw new InternalConfigurationError(String.format("Max value of %s is greater than max value", title));
        }

        Integer value = defaultValue;

        if(config.getString(key) == null) {
            if (defaultValue == null) {
                throw new InvalidConfigException(String.format("Empty %s", title));
            } else {
                customLogger.debug(String.format("Empty %s. Use default value %d", title, defaultValue));
            }
        } else if (! config.isInt(key)) {
            throw new InvalidConfigException(String.format("A %s is not an integer", title));
        } else {
            int intValue = config.getInt(key);

            if(min > intValue) {
                throw new InvalidConfigException(String.format("A %s should be at least %d", title, min));
            } else if(max < intValue) {
                throw new InvalidConfigException(String.format("A %s should be at most %d", title, max));
            } else {
                value = intValue;
            }
        }

        return value;
    }

    public static double getDouble(FileConfiguration config, CustomLogger customLogger, String key, String title,
                                   double min, double max) throws InvalidConfigException {
        return getDoubleInternal(config, customLogger, key, title, min, max, null);
    }

    public static double getDouble(FileConfiguration config, CustomLogger customLogger, String key, String title,
                                   double min, double max, double defaultValue) throws InvalidConfigException {
        return getDoubleInternal(config, customLogger, key, title, min, max, defaultValue);
    }

    private static double getDoubleInternal(FileConfiguration config, CustomLogger customLogger, String key, String title,
                                            double min, double max, Double defaultValue) throws InvalidConfigException {
        //noinspection IfStatementWithTooManyBranches
        if (!isLengthOfDoubleGood(min)) {
            throw new InternalConfigurationError(String.format("Min value of %s has too many digits", title));
        } else if (!isLengthOfDoubleGood(max)) {
            throw new InternalConfigurationError(String.format("Max value of %s has too many digits", title));
        } else if ((defaultValue != null) && (!isLengthOfDoubleGood(defaultValue))) {
            throw new InternalConfigurationError(String.format("Default value of %s has too many digits", title));
        } else if (min > max) {
            throw new InternalConfigurationError(String.format("Max value of %s is greater than max value", title));
        }

        Double value = defaultValue;

        if(config.getString(key) == null) {
            if (defaultValue == null) {
                throw new InvalidConfigException(String.format("Empty %s", title));
            } else {
                customLogger.debug(String.format("Empty %s. Use default value %s", title, formatDoubleValue(defaultValue)));
            }
        } else if ((! config.isDouble(key)) && (! config.isInt(key))) {
            throw new InvalidConfigException(String.format("A %s is not a double", title));
        } else {
            double doubleValue = config.getDouble(key);

            //noinspection IfStatementWithTooManyBranches
            if(!isLengthOfLeftPartOfDoubleGood(doubleValue)) {
                throw new InvalidConfigException(String.format("A left part of %s has too many digits", title));
            } else if(!isLengthOfRightPartOfDoubleGood(doubleValue)) {
                throw new InvalidConfigException(String.format("A right part of %s has too many digits", title));
            } else if ((min - Double.MIN_VALUE) > doubleValue) {
                throw new InvalidConfigException(String.format("A %s should be at least %s", title, formatDoubleValue(min)));
            } else if ((max + Double.MIN_VALUE) < doubleValue) {
                throw new InvalidConfigException(String.format("A %s should be at most %s", title, formatDoubleValue(max)));
            } else {
                value = doubleValue;
            }
        }

        return value;
    }
}
