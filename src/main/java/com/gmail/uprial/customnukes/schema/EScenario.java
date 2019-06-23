package com.gmail.uprial.customnukes.schema;

import com.gmail.uprial.customnukes.CustomNukes;
import com.gmail.uprial.customnukes.common.CustomLogger;
import com.gmail.uprial.customnukes.config.InvalidConfigException;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

import static com.gmail.uprial.customnukes.config.ConfigReaderSimple.getKey;

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
    public static EScenario getFromConfig(FileConfiguration config, CustomLogger customLogger, String key, String title, boolean isRepeaterAllowed) throws InvalidConfigException {
        List<?> scenarioConfig = config.getList(key + ".scenario");
        if((scenarioConfig == null) || (scenarioConfig.size() <= 0)) {
            throw new InvalidConfigException(String.format("Empty %s", title));
        }

        EScenario scenario = new EScenario();
        int scenarioConfigSize = scenarioConfig.size();
        for(int i = 0; i < scenarioConfigSize; i++) {
            String scenarioKey = getKey(scenarioConfig.get(i), title, i);
            if(config.getConfigurationSection(key + '.' + scenarioKey) == null) {
                throw new InvalidConfigException(String.format("Null definition of scenario action '%s' from pos %d of %s", scenarioKey, i, title));
            }

            scenario.addAction(EScenarioAction.getFromConfig(config, customLogger, key + '.' + scenarioKey, String.format("action '%s' of %s", scenarioKey, title), isRepeaterAllowed));
        }

        return scenario;
    }
}
