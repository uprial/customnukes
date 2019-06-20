package com.gmail.uprial.customnukes.schema;

import com.gmail.uprial.customnukes.ConfigReader;
import com.gmail.uprial.customnukes.ConfigReaderResult;
import com.gmail.uprial.customnukes.CustomNukes;
import com.gmail.uprial.customnukes.common.CustomLogger;
import com.gmail.uprial.customnukes.common.Utils;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("ClassWithTooManyMethods")
public class EScenarioActionEffect extends AbstractEScenarioActionExplosion {
    @Override
    protected int defaultMinDelay() { return 2; }
    @Override
    protected int defaultMaxDelay() { return 10; }
    @Override
    protected int minDelayValue() { return 2; }
    @Override
    protected int maxDelayValue() { return 2000; }

    @Override
    protected float minRadius() { return 1; }
    @Override
    protected float maxRadius() { return 5000; }

    @SuppressWarnings("SameReturnValue")
    private static int minStrength() { return 1; }
    @SuppressWarnings("SameReturnValue")
    private static int maxStrength() { return 100; }
    @SuppressWarnings("SameReturnValue")
    private static int defaultStrength() { return 1; }

    @SuppressWarnings("SameReturnValue")
    private static int minDuration() { return 1; }
    @SuppressWarnings("SameReturnValue")
    private static int maxDuration() { return 6000; }

    @SuppressWarnings({"SameReturnValue", "BooleanMethodNameMustStartWithQuestion"})
    private static boolean defaultPlayersOnly() { return true; }

    private List<PotionEffectType> effects = null;
    private int strength = 0;
    private int duration = 0;
    private boolean playersOnly = false;

    public EScenarioActionEffect(String actionId) {
        super(actionId);
    }

    @Override
    public void explode(CustomNukes plugin, Location location) {
        List<?> entities;
        entities = playersOnly ? location.getWorld().getPlayers() : location.getWorld().getLivingEntities();

        int entitiesSize = entities.size();
        //noinspection ForLoopReplaceableByForEach
        for(int pid = 0; pid < entitiesSize; pid++) {
            LivingEntity entity = (LivingEntity)entities.get(pid);
            if(Utils.isInRange(location, entity.getLocation(), radius)) {
                int effectsSize = effects.size();
                //noinspection ForLoopReplaceableByForEach
                for(int eid = 0; eid < effectsSize; eid++) {
                    PotionEffectType effect = effects.get(eid);
                    addEffect(entity, new PotionEffect(effect, Utils.seconds2ticks(duration), strength - 1));
                }
            }
        }
    }

    private static void addEffect(LivingEntity entity, PotionEffect effect) {
        //noinspection LocalVariableNamingConvention
        boolean notAffectedOrAffectedMoreWeakly = true;
        Iterator<PotionEffect> effectsIterator = entity.getActivePotionEffects().iterator();
        //noinspection WhileLoopReplaceableByForEach
        while (effectsIterator.hasNext()) {
            PotionEffect currentEffect = effectsIterator.next();
            if(currentEffect.getType() == effect.getType()) {
                if(currentEffect.getAmplifier() > effect.getAmplifier()) {
                    notAffectedOrAffectedMoreWeakly = false;
                } else {
                    entity.removePotionEffect(currentEffect.getType());
                }
            }
        }
        if(notAffectedOrAffectedMoreWeakly) {
            entity.addPotionEffect(effect);
        }
    }

    @Override
    public boolean isLoadedFromConfig(FileConfiguration config, CustomLogger customLogger, String key, String title) {
        if(!super.isLoadedFromConfig(config, customLogger, key, title)) {
            return false;
        }

        if(!isLoadedTypeConfig(config, customLogger, key, title)) {
            return false;
        }

        strength = ConfigReader.getInt(config, customLogger, key + ".strength", String.format("Strength of %s", title), minStrength(), maxStrength(), defaultStrength());

        if(!isLoadedDurationFromConfig(config, customLogger, key, title)) {
            return false;
        }

        playersOnly = ConfigReader.getBoolean(config, customLogger, key + ".players-only", String.format("'players-only' value of %s", title), defaultPlayersOnly());

        return true;
    }

    private boolean isLoadedTypeConfig(FileConfiguration config, CustomLogger customLogger, String key, String title) {
        List<?> typeConfig = config.getList(key + ".effects");
        if((typeConfig == null) || (typeConfig.size() <= 0)) {
            customLogger.error(String.format("Empty effects list of %s", title));
            return false;
        }

        List<PotionEffectType> effects = new ArrayList<>();
        int typeConfigSize = typeConfig.size();
        for(int i = 0; i < typeConfigSize; i++) {
            Object item = typeConfig.get(i);
            if(item == null) {
                customLogger.error(String.format("Null effect in effects list of %s at pos %d", title, i));
                return false;
            }
            String effectName = item.toString();
            PotionEffectType effect = PotionEffectType.getByName(effectName);
            if(effect == null) {
                customLogger.error(String.format("Invalid effect '%s' of %s at pos %d", effectName, title, i));
                return false;
            }

            effects.add(effect);
        }

        this.effects = effects;

        return true;
    }

    private boolean isLoadedDurationFromConfig(FileConfiguration config, CustomLogger customLogger, String key, String title) {
        ConfigReaderResult result = ConfigReader.getIntComplex(config, customLogger, key + ".duration", String.format("duration of %s", title), minDuration(), maxDuration());
        if(result.isError()) {
            return false;
        } else {
            duration = result.getIntValue();
            return true;
        }
    }
}
