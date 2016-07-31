package com.gmail.uprial.customnukes;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;

import com.gmail.uprial.customnukes.common.CustomLogger;
import com.gmail.uprial.customnukes.schema.EItem;

public class ExplosivesCraftListener implements Listener {

    private final CustomNukes plugin;
    private final CustomLogger customLogger;
    
    public ExplosivesCraftListener(CustomNukes plugin, CustomLogger customLogger) {
        this.plugin = plugin;
        this.customLogger = customLogger;
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onItemCraft(PrepareItemCraftEvent event) {
        EItem explosive = plugin.getExplosivesConfig().searchExplosiveByItemStack(event.getRecipe().getResult());
        if(null != explosive) {
            HumanEntity entityPlayer = event.getView().getPlayer();
            Player player = plugin.getPlayerByName(entityPlayer.getName());
            if(null == player)
                event.getInventory().setResult(null);
            else if (!explosive.hasPermission(player)) {
                event.getInventory().setResult(null);
                customLogger.userError(player, "you don't have permissions to craft this type of item.");
            }
        }
    }    
}
