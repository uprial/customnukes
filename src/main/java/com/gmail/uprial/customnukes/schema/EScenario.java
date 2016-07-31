package com.gmail.uprial.customnukes.schema;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import com.gmail.uprial.customnukes.CustomNukes;
import com.gmail.uprial.customnukes.common.CustomLogger;

public class EScenario {
    private List<EScenarioAction> actions;

    public EScenario() {
        actions = new ArrayList<EScenarioAction>();
    }

    public void addAction(EScenarioAction action) {
        actions.add(action);
    }

    public void execute(CustomNukes plugin, Location location) {
        int delay = 0;
        for(int i = 0; i < actions.size(); i++)
            delay = actions.get(i).execute(plugin, location, delay);
    }

    public static EScenario getFromConfig(FileConfiguration config, CustomLogger customLogger, String key, String name, boolean isRepeaterAllowed) {
        List<?> scenarioConfig = config.getList(key + ".scenario");
        if((null == scenarioConfig) || (scenarioConfig.size() < 0)) {
            customLogger.error(String.format("Empty scenario of item '%s'", name));
            return null;
        }

        EScenario scenario = new EScenario();
        for(int i = 0; i < scenarioConfig.size(); i++) {
            Object item = scenarioConfig.get(i);
            if(null == item) {
                customLogger.error(String.format("Null key in scenario of item '%s' at pos %d", name, i));
                return null;
            }
            String scenario_key = item.toString();
            if(scenario_key.length() < 1) {
                customLogger.error(String.format("Empty key in scenario of item '%s' at pos %d", name, i));
                return null;
            }
            if(null == config.getConfigurationSection(key + "." + scenario_key)) {
                customLogger.error(String.format("Null definition of scenario action '%s' from pos %d of item '%s'", scenario_key, i, name));
                return null;
            }

            EScenarioAction action = EScenarioAction.getFromConfig(config, customLogger, key + "." + scenario_key, name + "/" + scenario_key, isRepeaterAllowed);
            if(null == action)
                return null;

            scenario.addAction(action);
        }

        return scenario;
    }
}
