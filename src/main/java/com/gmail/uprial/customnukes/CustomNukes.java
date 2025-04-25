package com.gmail.uprial.customnukes;

import com.gmail.uprial.customnukes.common.CustomLogger;
import com.gmail.uprial.customnukes.config.InvalidConfigException;
import com.gmail.uprial.customnukes.listeners.ExplosivesActivateListener;
import com.gmail.uprial.customnukes.listeners.ExplosivesBlocksListener;
import com.gmail.uprial.customnukes.listeners.ExplosivesCraftListener;
import com.gmail.uprial.customnukes.listeners.SpongeOverrideListener;
import com.gmail.uprial.customnukes.schema.EItem;
import com.gmail.uprial.customnukes.schema.RepeaterTaskStorage;
import com.gmail.uprial.customnukes.storage.ExplosiveBlockStorage;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static com.gmail.uprial.customnukes.CustomNukesCommandExecutor.COMMAND_NS;

public final class CustomNukes extends JavaPlugin {
    private final String CONFIG_FILE_NAME = "config.yml";
    private final File configFile = new File(getDataFolder(), CONFIG_FILE_NAME);

    private ExplosivesConfig explosivesConfig = null;
    private CustomLogger consoleLogger = null;
    private RepeaterTaskStorage repeaterTaskStorage = null;
    private BukkitTask saveTask = null;
    private ExplosiveBlockStorage explosiveBlockStorage = null;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        consoleLogger = new CustomLogger(getLogger());

        explosiveBlockStorage = new ExplosiveBlockStorage(this, getDataFolder(), consoleLogger);
        repeaterTaskStorage = new RepeaterTaskStorage(this, getDataFolder(), consoleLogger);
        explosivesConfig = loadConfig(this, getConfig(), consoleLogger);
        repeaterTaskStorage.restore();
        loadExplosives();

        saveTask = new TaskPeriodicSave(this).runTaskTimer();

        getServer().getPluginManager().registerEvents(new ExplosivesBlocksListener(this, consoleLogger), this);
        getServer().getPluginManager().registerEvents(new ExplosivesActivateListener(this, consoleLogger), this);
        getServer().getPluginManager().registerEvents(new ExplosivesCraftListener(this), this);
        getServer().getPluginManager().registerEvents(new SpongeOverrideListener(this, consoleLogger), this);
        getCommand(COMMAND_NS).setExecutor(new CustomNukesCommandExecutor(this));

        consoleLogger.info("Plugin enabled");
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        saveTask.cancel();

        unloadExplosives();
        saveData();

        consoleLogger.info("Plugin disabled");
    }

    @Override
    public void saveDefaultConfig() {
        if (!configFile.exists()) {
            saveResource(CONFIG_FILE_NAME, false);
        }
    }

    @Override
    public FileConfiguration getConfig() {
        return YamlConfiguration.loadConfiguration(configFile);
    }

    public ExplosivesConfig getExplosivesConfig() {
        return explosivesConfig;
    }

    public ExplosiveBlockStorage getExplosiveBlockStorage() {
        return explosiveBlockStorage;
    }

    public RepeaterTaskStorage getRepeaterTaskStorage() {
        return repeaterTaskStorage;
    }

    public void scheduleDelayed(Runnable runnable, long delay) {
        getServer().getScheduler().scheduleSyncDelayedTask(this, runnable, delay);
    }

    public void saveData() {
        repeaterTaskStorage.save();
        explosiveBlockStorage.save();
    }

    public void reloadExplosivesConfig(CustomLogger userLogger) {
        reloadConfig();
        unloadExplosives();
        explosivesConfig = loadConfig(this, getConfig(), userLogger, consoleLogger);
        loadExplosives();
    }

    public Player getPlayerByName(String playerName) {
        Collection<? extends Player> players = getServer().getOnlinePlayers();
        Iterator<? extends Player> iterator = players.iterator();
        //noinspection WhileLoopReplaceableByForEach
        while (iterator.hasNext()) {
            Player player = iterator.next();
            if(player.getName().equalsIgnoreCase(playerName)) {
                return player;
            }
        }

        return null;
    }

    public void clear() {
        explosiveBlockStorage.clear();
        repeaterTaskStorage.clear();
    }

    private void loadExplosives() {
        List<EItem> explosives = explosivesConfig.getExplosives();
        for(EItem explosive : explosives) {
            ShapedRecipe shapedRecipe = explosive.getShapedRecipe();
            getServer().addRecipe(shapedRecipe);
            consoleLogger.info("Added " + explosive);
        }
    }

    private void unloadExplosives() {
        /*
            We need to search by name
            because the method getServer().removeRecipe()
            is implemented in later versions.
         */
        Iterator<Recipe> iterator = getServer().recipeIterator();
        while (iterator.hasNext()) {
            Recipe recipe = iterator.next();
            EItem explosive = explosivesConfig.searchExplosiveByItemStack(recipe.getResult());
            if(explosive != null) {
                iterator.remove();
                consoleLogger.info("Removed " + explosive);
            }
        }
    }


    private static ExplosivesConfig loadConfig(CustomNukes plugin, FileConfiguration config, CustomLogger customLogger) {
        return loadConfig(plugin, config, customLogger, null);
    }

    private static ExplosivesConfig loadConfig(CustomNukes plugin, FileConfiguration config, CustomLogger mainLogger, CustomLogger secondLogger) {
        ExplosivesConfig explosivesConfig = null;
        try {
            boolean isDebugMode = ExplosivesConfig.isDebugMode(config, mainLogger);
            mainLogger.setDebugMode(isDebugMode);
            if (secondLogger != null) {
                secondLogger.setDebugMode(isDebugMode);
            }

            explosivesConfig = ExplosivesConfig.getFromConfig(plugin, config, mainLogger);
        } catch (InvalidConfigException e) {
            mainLogger.error(e.getMessage());
        }

        return explosivesConfig;
    }
}
