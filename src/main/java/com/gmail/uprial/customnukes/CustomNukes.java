package com.gmail.uprial.customnukes;

import com.gmail.uprial.customnukes.common.BlockMetaStorage;
import com.gmail.uprial.customnukes.common.CustomLogger;
import com.gmail.uprial.customnukes.common.MicroTimestamp;
import com.gmail.uprial.customnukes.schema.EItem;
import com.gmail.uprial.customnukes.schema.RepeaterTaskStorage;
import org.bukkit.World;
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

import static com.gmail.uprial.customnukes.CustomNukesCommandExecutor.COMMAND_NS;

public final class CustomNukes extends JavaPlugin {
    private final String CONFIG_FILE_NAME = "config.yml";
    private final File configFile = new File(getDataFolder(), CONFIG_FILE_NAME);

    private static int saveInterval = 20 * 300;

    private ExplosivesConfig explosivesConfig;
    private CustomLogger consoleLogger;
    private BlockMetaStorage blockMetaStorage;
    private RepeaterTaskStorage repeaterTaskStorage;
    private BukkitTask saveTask;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        consoleLogger = new CustomLogger(getLogger());

        blockMetaStorage = new BlockMetaStorage(this, getDataFolder(), consoleLogger);
        repeaterTaskStorage = new RepeaterTaskStorage(this, getDataFolder(), consoleLogger);
        explosivesConfig = loadConfig(getConfig(), consoleLogger);
        repeaterTaskStorage.restore();
        loadExplosives();

        saveTask = new TaskPeriodicSave(this).runTaskTimer(this, saveInterval, saveInterval);

        getServer().getPluginManager().registerEvents(new ExplosivesBlocksListener(this, consoleLogger), this);
        getServer().getPluginManager().registerEvents(new ExplosivesActivateListener(this, consoleLogger), this);
        getServer().getPluginManager().registerEvents(new ExplosivesCraftListener(this), this);
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

    public void global_log(String message) {
        getLogger().info(message);
        for(World w : getServer().getWorlds()){
            for(Player p : w.getPlayers()){
                  p.sendMessage("[" + MicroTimestamp.INSTANCE.get() + "] " + message);
            }
        }
    }

    public ExplosivesConfig getExplosivesConfig() {
        return explosivesConfig;
    }

    public BlockMetaStorage getBlockMetaStorage() {
        return blockMetaStorage;
    }

    public RepeaterTaskStorage getRepeaterTaskStorage() {
        return repeaterTaskStorage;
    }

    public int scheduleDelayed(Runnable runnable, long delay) {
        return getServer().getScheduler().scheduleSyncDelayedTask(this, runnable, delay);
    }

    public void saveData() {
        repeaterTaskStorage.save();
        blockMetaStorage.save();
    }

    public void reloadExplosivesConfig(CustomLogger userLogger) {
        reloadConfig();
        unloadExplosives();
        explosivesConfig = loadConfig(getConfig(), userLogger, consoleLogger);
        loadExplosives();
    }

    public Player getPlayerByName(String playerName) {
        Collection<? extends Player> players = getServer().getOnlinePlayers();
        Iterator<? extends Player> iterator = players.iterator();
        while (iterator.hasNext()) {
            Player player = iterator.next();
            if(player.getName().equalsIgnoreCase(playerName))
                return player;
        }

        return null;
    }

    public void clear() {
        blockMetaStorage.clear();
        repeaterTaskStorage.clear();
    }

    private void loadExplosives() {
        for(int i = 0; i < explosivesConfig.getExplosives().size(); i++) {
            EItem explosive = explosivesConfig.getExplosives().get(i);

            ShapedRecipe shapedRecipe = explosive.getShapedRecipe();
            getServer().addRecipe(shapedRecipe);
            consoleLogger.info("Added " + explosive.toString());
        }
    }

    private void unloadExplosives() {
        Iterator<Recipe> iterator = getServer().recipeIterator();
        while (iterator.hasNext()) {
            Recipe recipe = iterator.next();
            EItem explosive = explosivesConfig.searchExplosiveByItemStack(recipe.getResult());
            if(null != explosive) {
                iterator.remove();
                consoleLogger.info("Removed " + explosive.toString());
            }
        }
    }


    private static ExplosivesConfig loadConfig(FileConfiguration config, CustomLogger customLogger) {
        return loadConfig(config, customLogger, null);
    }

    private static ExplosivesConfig loadConfig(FileConfiguration config, CustomLogger mainLogger, CustomLogger secondLogger) {
        boolean isDebugMode = ExplosivesConfig.isDebugMode(config, mainLogger);
        mainLogger.setDebugMode(isDebugMode);
        if(secondLogger != null) {
            secondLogger.setDebugMode(isDebugMode);
        }

        return ExplosivesConfig.getFromConfig(config, mainLogger);
    }
}
