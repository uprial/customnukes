package com.gmail.uprial.customnukes.schema;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.gmail.uprial.customnukes.ConfigReader;
import com.gmail.uprial.customnukes.ConfigReaderResult;
import com.gmail.uprial.customnukes.CustomNukes;
import com.gmail.uprial.customnukes.common.CustomLogger;
import com.gmail.uprial.customnukes.common.EUtils;

public class EScenarioActionEffect extends AbstractEScenarioActionExplosion {
    protected int defaultMinDelay() { return 2; }
    protected int defaultMaxDelay() { return 10; }
    protected int minDelayValue() { return 2; }
    protected int maxDelayValue() { return 2000; }

    protected float minRadius() { return 1; }
    protected float maxRadius() { return 5000; }

    protected int minStrength() { return 1; }
    protected int maxStrength() { return 100; }
    protected int defaultStrength() { return 1; }

    protected int minDuration() { return 1; }
    protected int maxDuration() { return 6000; }

    protected boolean defaultPlayersOnly() { return true; }

    private List<PotionEffectType> effects;
    private int strength;
    private int duration;
    private boolean playersOnly;

    public EScenarioActionEffect(String actionId) {
        super(actionId);
    }

    public void explode(CustomNukes plugin, Location location) {
        List<?> entities;
        if(playersOnly)
            entities = location.getWorld().getPlayers();
        else
            entities = location.getWorld().getLivingEntities();

        for(int pid = 0; pid < entities.size(); pid++) {
            LivingEntity entity = (LivingEntity)entities.get(pid);
            if(EUtils.isInRange(location, entity.getLocation(), radius)) {
                for(int eid = 0; eid < effects.size(); eid++) {
                    PotionEffectType effect = effects.get(eid);
                    addEffect(entity, new PotionEffect(effect, EUtils.seconds2ticks(duration), strength - 1));
                }
            }
        }
    }

    private void addEffect(LivingEntity entity, PotionEffect effect) {
        boolean hasPowered = false;
        Iterator<PotionEffect> effectsIterator = entity.getActivePotionEffects().iterator();
        while (effectsIterator.hasNext()) {
            PotionEffect currentEffect = effectsIterator.next();
            if(currentEffect.getType() == effect.getType()) {
                if(currentEffect.getAmplifier() > effect.getAmplifier())
                    hasPowered = true;
                else
                    entity.removePotionEffect(currentEffect.getType());
            }
        }
        if(!hasPowered)
            entity.addPotionEffect(effect);
    }

    public void setEffects(List<PotionEffectType> effects) {
        this.effects = effects;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setPlayersOnly(boolean playersOnly) {
        this.playersOnly = playersOnly;
    }

    public boolean isLoadedFromConfig(FileConfiguration config, CustomLogger customLogger, String key, String name) {
        if(!super.isLoadedFromConfig(config, customLogger, key, name))
            return false;

        if(!isLoadedTypeConfig(config, customLogger, key, name))
            return false;

        setStrength(ConfigReader.getInt(config, customLogger, key + ".strength", "Strength of action", name, minStrength(), maxStrength(), defaultStrength()));

        if(!isLoadedDurationFromConfig(config, customLogger, key, name))
            return false;

        setPlayersOnly(ConfigReader.getBoolean(config, customLogger, key + ".players-only", "'players-only' value of action", name, defaultPlayersOnly()));

        return true;
    }

    private boolean isLoadedTypeConfig(FileConfiguration config, CustomLogger customLogger, String key, String name) {
        List<?> typeConfig = config.getList(key + ".effects");
        if((null == typeConfig) || (typeConfig.size() < 0)) {
            customLogger.error(String.format("Empty effects list of action '%s'", name));
            return false;
        }

        List<PotionEffectType> effects = new ArrayList<PotionEffectType>();
        for(int i = 0; i < typeConfig.size(); i++) {
            Object item = typeConfig.get(i);
            if(null == item) {
                customLogger.error(String.format("Null effect in effects list of action '%s' at pos %d", name, i));
                return false;
            }
            String effectName = item.toString();
            PotionEffectType effect = PotionEffectType.getByName(effectName);
            if(null == effect) {
                customLogger.error(String.format("Invalid effect '%s' of action '%s' at pos %d", effectName, name, i));
                return false;
            }

            effects.add(effect);
        }

        setEffects(effects);

        return true;
    }

    private boolean isLoadedDurationFromConfig(FileConfiguration config, CustomLogger customLogger, String key, String name) {
        ConfigReaderResult result = ConfigReader.getIntComplex(config, customLogger, key + ".duration", "Duration of action", name, minDuration(), maxDuration());
        if(result.isError())
            return false;
        else {
            setDuration(result.getInt());
            return true;
        }
    }
}
