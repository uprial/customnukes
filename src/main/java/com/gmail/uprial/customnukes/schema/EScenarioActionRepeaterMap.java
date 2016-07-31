package com.gmail.uprial.customnukes.schema;

import java.util.HashMap;
import java.util.Map;

public enum EScenarioActionRepeaterMap {
    INSTANCE;

    private Map<String,EScenarioActionRepeater> data;

    private EScenarioActionRepeaterMap() {
        data = new HashMap<String,EScenarioActionRepeater>();
    }

    public void set(String actionId, EScenarioActionRepeater action) {
        data.put(actionId, action);
    }

    public EScenarioActionRepeater get(String actionId) {
        return data.get(actionId);
    }
}
