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
	    			sendMessage(sender, "CustomNukes config reloaded.");
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
	    				sender.sendMessage("customnukes give <player> <explosive-key> <amount>");
	    				error = true;
	    			}

    				if(!error) {
		    			player = getPlayerByName(args[1]);
		    			if(null == player) {
		    				sendMessage(sender, String.format("Error: player '%s' is not exists.", args[1]));
		    				error = true;
		    			}
	    			}
	    			if(!error) {
		    			explosive = plugin.getExplosivesConfig().searchExplosiveByKey(args[2]);
		    			if(null == explosive)
		    				explosive = plugin.getExplosivesConfig().searchExplosiveByName(args[2]);
		    			
		    			if(null == explosive) {
		    				sendMessage(sender, String.format("Error: explosive '%s' is not exists.", args[2]));
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
			    				sendMessage(sender, "Error: amount should be an integer between 1 and 64.");
			    				error = true;
		    				}
		    				if(!error) {
				    			if(amount < 1) {
				    				sendMessage(sender, "Error: amount should be at least 1.");
				    				error = true;
				    			} else if(amount > 64) {
				    				sendMessage(sender, "Error: amount should be at most 64.");
				    				error = true;
				    			}
		    				}
		    			}
	    			}
	    			if(!error) {
	    				player.getInventory().addItem(explosive.getCustomItemStack(amount));
	    				sendMessage(sender, String.format("Player '%s' got %d * '%s'", player.getName(), amount, explosive.getName()));
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
    			sender.sendMessage(Help);
    			return true;
			}
    	} 
    	return false; 
    }    
	
    private void sendMessage(CommandSender sender, String message) {
    	sender.sendMessage(message);
    	customLogger.info(message);
    }
    
    private Player getPlayerByName(String playerName) {
		Player[] players = plugin.getServer().getOnlinePlayers();
		for(int i = 0; i < players.length; i++)
			if(players[i].getName().equalsIgnoreCase(playerName))
				return players[i];
		
		return null;
    }
}
