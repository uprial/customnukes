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
    public static EScenario getFromConfig(FileConfiguration config, CustomLogger customLogger, String key, String title, boolean isRepeaterAllowed) {
        List<?> scenarioConfig = config.getList(key + ".scenario");
        if((scenarioConfig == null) || (scenarioConfig.size() <= 0)) {
            customLogger.error(String.format("Empty %s", title));
            return null;
        }

        EScenario scenario = new EScenario();
        int scenarioConfigSize = scenarioConfig.size();
        for(int i = 0; i < scenarioConfigSize; i++) {
            Object item = scenarioConfig.get(i);
            if(item == null) {
                customLogger.error(String.format("Null key in %s at pos %d", title, i));
                return null;
            }
            String scenarioKey = item.toString();
            if(scenarioKey.length() < 1) {
                customLogger.error(String.format("Empty key in %s at pos %d", title, i));
                return null;
            }
            if(config.getConfigurationSection(key + '.' + scenarioKey) == null) {
                customLogger.error(String.format("Null definition of scenario action '%s' from pos %d of %s", scenarioKey, i, title));
                return null;
            }

            EScenarioAction action = EScenarioAction.getFromConfig(config, customLogger, key + '.' + scenarioKey, String.format("action '%s' of %s", scenarioKey, title), isRepeaterAllowed);
            if(action == null) {
                return null;
            }

            scenario.addAction(action);
        }

        return scenario;
    }
}
