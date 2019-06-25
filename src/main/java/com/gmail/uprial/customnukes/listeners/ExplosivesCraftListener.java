package com.gmail.uprial.customnukes.listeners;

import com.gmail.uprial.customnukes.CustomNukes;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;

import com.gmail.uprial.customnukes.common.CustomLogger;
import com.gmail.uprial.customnukes.schema.EItem;
import org.bukkit.inventory.Recipe;

public class ExplosivesCraftListener implements Listener {

    private final CustomNukes plugin;

    public ExplosivesCraftListener(CustomNukes plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.NORMAL)
    public void onItemCraft(PrepareItemCraftEvent event) {
        Recipe recipe = event.getRecipe();
        if (recipe != null) {
            EItem explosive = plugin.getExplosivesConfig().searchExplosiveByItemStack(recipe.getResult());
            if (explosive != null) {
                HumanEntity entityPlayer = event.getView().getPlayer();
                Player player = plugin.getPlayerByName(entityPlayer.getName());
                if (player == null) {
                    event.getInventory().setResult(null);
                } else if (!explosive.hasPermission(player)) {
                    event.getInventory().setResult(null);
                    CustomLogger userLogger = new CustomLogger(plugin.getLogger(), player);
                    userLogger.error("you don't have permissions to craft this type of item.");
                }
            }
        }
    }
}
