package com.gmail.uprial.customnukes;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.uprial.customnukes.common.CustomLogger;
import com.gmail.uprial.customnukes.schema.EItem;

public class CustomNukesCommandExecutor implements CommandExecutor {
	private final CustomNukes plugin;
	private final CustomLogger customLogger;

	public CustomNukesCommandExecutor(CustomNukes plugin, CustomLogger customLogger) {
		this.plugin = plugin;
		this.customLogger = customLogger;
	}
	
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    	if (command.getName().equalsIgnoreCase("customnukes")) {
			if((args.length >= 1) && (args[0].equalsIgnoreCase("reload"))) {
	    		if (sender.hasPermission("customnukes.reload")) {
	    			plugin.reloadExplosivesConfig();
	    			customLogger.userInfo(sender, "CustomNukes config reloaded.");
	    			return true;
	    		}
			}
			if((args.length >= 1) && (args[0].equalsIgnoreCase("give"))) {
	    		if (sender.hasPermission("customnukes.give")) {
	    			boolean error = false;
		    			
	    			Player player = null;
	    			EItem explosive = null;
	    			int amount = 0;
	    			
	    			if(args.length < 3) {
	    				customLogger.userInfo(sender, "customnukes give <player> <explosive-key> <amount>");
	    				error = true;
	    			}

    				if(!error) {
		    			player = plugin.getPlayerByName(args[1]);
		    			if(null == player) {
		    				customLogger.userError(sender, String.format("Player '%s' is not exists.", args[1]));
		    				error = true;
		    			}
	    			}
	    			if(!error) {
		    			explosive = plugin.getExplosivesConfig().searchExplosiveByKey(args[2]);
		    			if(null == explosive)
		    				explosive = plugin.getExplosivesConfig().searchExplosiveByName(args[2]);
		    			
		    			if(null == explosive) {
		    				customLogger.userError(sender, String.format("Explosive '%s' is not exists.", args[2]));
		    				error = true;
		    			}
	    			}
	    			if(!error) {
		    			if(args.length < 4)
		    				amount = 1;
		    			else {
		    				try {
		    					amount = Integer.valueOf(args[3]);
		    				} catch (NumberFormatException e) {
		    					customLogger.userError(sender, "Amount should be an integer between 1 and 64.");
			    				error = true;
		    				}
		    				if(!error) {
				    			if(amount < 1) {
				    				customLogger.userError(sender, "Amount should be at least 1.");
				    				error = true;
				    			} else if(amount > 64) {
				    				customLogger.userError(sender, "Amount should be at most 64.");
				    				error = true;
				    			}
		    				}
		    			}
	    			}
	    			if(!error) {
	    				player.getInventory().addItem(explosive.getCustomItemStack(amount));
	    				customLogger.userInfo(sender, String.format("Player '%s' got %d * '%s'", player.getName(), amount, explosive.getName()));
	    			}
		    			
	    			return true;
	    		}
			}
			else if((args.length == 0) || (args[0].equalsIgnoreCase("help"))) {
				String Help = "==== CustomNukes help ====";
				if (sender.hasPermission("customnukes.reload"))
					Help += "\n/customnukes reload - reload config from disk";
				if (sender.hasPermission("customnukes.give"))
					Help += "\n/customnukes give <player> <explosive-key> <amount>";
				Help += "\n";
				customLogger.userInfo(sender, Help);
    			return true;
			}
    	} 
    	return false; 
    }    
}
