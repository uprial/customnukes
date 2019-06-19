package com.gmail.uprial.customnukes.schema;

import com.gmail.uprial.customnukes.CustomNukes;
import com.gmail.uprial.customnukes.common.CustomLogger;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public final class EScenario {
    private final List<EScenarioAction> actions;

    private EScenario() {
        actions = new ArrayList<>();
    }

    private void addAction(EScenarioAction action) {
        actions.add(action);
    }

    public void execute(CustomNukes plugin, Location location) {
        int delay = 0;
        for(EScenarioAction action : actions) {
            delay = action.execute(plugin, location, delay);
        }
    }

    @SuppressWarnings("BooleanParameter")
    public static EScenario getFromConfig(FileConfiguration config, CustomLogger customLogger, String key, String name, boolean isRepeaterAllowed) {
        List<?> scenarioConfig = config.getList(key + ".scenario");
        if((scenarioConfig == null) || (scenarioConfig.size() < 0)) {
            customLogger.error(String.format("Empty scenario of item '%s'", name));
            return null;
        }

        EScenario scenario = new EScenario();
        int scenarioConfigSize = scenarioConfig.size();
        for(int i = 0; i < scenarioConfigSize; i++) {
            Object item = scenarioConfig.get(i);
            if(item == null) {
                customLogger.error(String.format("Null key in scenario of item '%s' at pos %d", name, i));
                return null;
            }
            String scenarioKey = item.toString();
            if(scenarioKey.length() < 1) {
                customLogger.error(String.format("Empty key in scenario of item '%s' at pos %d", name, i));
                return null;
            }
            if(config.getConfigurationSection(key + '.' + scenarioKey) == null) {
                customLogger.error(String.format("Null definition of scenario action '%s' from pos %d of item '%s'", scenarioKey, i, name));
                return null;
            }

            EScenarioAction action = EScenarioAction.getFromConfig(config, customLogger, key + '.' + scenarioKey, name + '/' + scenarioKey, isRepeaterAllowed);
            if(action == null) {
                return null;
            }

            scenario.addAction(action);
        }

        return scenario;
    }
}
