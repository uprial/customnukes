package com.gmail.uprial.customnukes.schema;

import com.gmail.uprial.customnukes.config.ConfigReaderNumbers;
import com.gmail.uprial.customnukes.config.ConfigReaderSimple;
import com.gmail.uprial.customnukes.CustomNukes;
import com.gmail.uprial.customnukes.common.CustomLogger;
import com.gmail.uprial.customnukes.common.Utils;
import com.gmail.uprial.customnukes.config.InvalidConfigException;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.gmail.uprial.customnukes.config.ConfigReaderSimple.getKey;

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
    protected double minRadius() { return 1; }
    @Override
    protected double maxRadius() { return 5000; }

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
    public void loadFromConfig(FileConfiguration config, CustomLogger customLogger, String key, String title) throws InvalidConfigException {
        super.loadFromConfig(config, customLogger, key, title);

        effects = getEffectsFromConfig(config, customLogger, key, title);
        strength = ConfigReaderNumbers.getInt(config, customLogger, key + ".strength",
                String.format("strength of %s", title), minStrength(), maxStrength(), defaultStrength());
        duration = ConfigReaderNumbers.getInt(config, customLogger, key + ".duration",
                String.format("duration of %s", title), minDuration(), maxDuration());
        playersOnly = ConfigReaderSimple.getBoolean(config, customLogger, key + ".players-only",
                String.format("'players-only' value of %s", title), defaultPlayersOnly());
    }

    private List<PotionEffectType> getEffectsFromConfig(FileConfiguration config, CustomLogger customLogger, String key, String title) throws InvalidConfigException {
        List<?> typeConfig = config.getList(key + ".effects");
        if((typeConfig == null) || (typeConfig.size() <= 0)) {
            throw new InvalidConfigException(String.format("Empty effects list of %s", title));
        }

        List<PotionEffectType> effects = new ArrayList<>();
        int typeConfigSize = typeConfig.size();
        for(int i = 0; i < typeConfigSize; i++) {
            String effectName = getKey(typeConfig.get(i), String.format("effects list of %s", title), i);
            PotionEffectType effect = PotionEffectType.getByName(effectName);
            if(effect == null) {
                throw new InvalidConfigException(String.format("Null definition of effect '%s' of %s at pos %d", effectName, title, i));
            }

            effects.add(effect);
        }

        return effects;
    }
}
