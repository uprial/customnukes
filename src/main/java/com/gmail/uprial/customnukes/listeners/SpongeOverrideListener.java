package com.gmail.uprial.customnukes.listeners;

import com.gmail.uprial.customnukes.CustomNukes;
import com.gmail.uprial.customnukes.common.CustomLogger;
import com.gmail.uprial.customnukes.schema.EItem;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SpongeAbsorbEvent;

import static com.gmail.uprial.customnukes.common.Formatter.format;

public class SpongeOverrideListener implements Listener  {
    private final CustomNukes plugin;
    private final CustomLogger customLogger;

    public SpongeOverrideListener(CustomNukes plugin, CustomLogger customLogger) {
        this.plugin = plugin;
        this.customLogger = customLogger;
    }

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.NORMAL)
    public void onSpongeAbsorb(SpongeAbsorbEvent event) {
        if(!event.isCancelled()) {
            Block block = event.getBlock();
            EItem explosive = plugin.getExplosiveBlockStorage().searchExplosiveByBlock(block);
            if(explosive != null) {
                customLogger.debug(String.format("Cancel SpongeAbsorb of '%s' at %s",
                        explosive.getName(), format(block)));
                event.setCancelled(true);
            }
        }
    }
}
