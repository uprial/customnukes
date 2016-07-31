package com.gmail.uprial.customnukes;

import java.util.Collection;
import java.util.Iterator;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import com.gmail.uprial.customnukes.common.BlockMetaStorage;
import com.gmail.uprial.customnukes.common.CustomLogger;
import com.gmail.uprial.customnukes.common.MicroTimestamp;
import com.gmail.uprial.customnukes.schema.RepeaterTaskStorage;
import com.gmail.uprial.customnukes.schema.EItem;

public final class CustomNukes extends JavaPlugin {
    private static int saveInterval = 20 * 300;
    
    private ExplosivesConfig explosivesConfig;
    private CustomLogger customLogger;
    private BlockMetaStorage blockMetaStorage;
    private RepeaterTaskStorage repeaterTaskStorage;
    private BukkitTask saveTask;
    
    @Override
    public void onEnable() {
        saveDefaultConfig();
        customLogger = new CustomLogger(getLogger());
    
        blockMetaStorage = new BlockMetaStorage(this, getDataFolder(), customLogger);
        repeaterTaskStorage = new RepeaterTaskStorage(this, getDataFolder(), customLogger);
        explosivesConfig = new ExplosivesConfig(getConfig(), customLogger);
        repeaterTaskStorage.restore();
        loadExplosives();
        
        saveTask = new TaskPeriodicSave(this).runTaskTimer(this, saveInterval, saveInterval);

        getServer().getPluginManager().registerEvents(new ExplosivesBlocksListener(this, customLogger), this);
        getServer().getPluginManager().registerEvents(new ExplosivesActivateListener(this, customLogger), this);
        getServer().getPluginManager().registerEvents(new ExplosivesCraftListener(this, customLogger), this);
        getCommand("customnukes").setExecutor(new CustomNukesCommandExecutor(this, customLogger));

        customLogger.info("Plugin enabled");
    }
    
    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        saveTask.cancel();
        
        unloadExplosives();
        saveData();

        customLogger.info("Plugin disabled");
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
    
    public void reloadExplosivesConfig() {
        reloadConfig();
        unloadExplosives();
        explosivesConfig = new ExplosivesConfig(getConfig(), customLogger);
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
            customLogger.info("Added " + explosive.toString());
        }
    }
    
    private void unloadExplosives() {
        Iterator<Recipe> iterator = getServer().recipeIterator();
        while (iterator.hasNext()) {
            Recipe recipe = iterator.next();
            EItem explosive = explosivesConfig.searchExplosiveByItemStack(recipe.getResult());
            if(null != explosive) {
                iterator.remove();
                customLogger.info("Removed " + explosive.toString());
            }
        }
    }
}
