package com.gmail.uprial.customnukes;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.uprial.customnukes.common.CustomLogger;
import com.gmail.uprial.customnukes.schema.EItem;

public class CustomNukesCommandExecutor implements CommandExecutor {
    public static final String COMMAND_NS = "customnukes";

    private final CustomNukes plugin;

    public CustomNukesCommandExecutor(CustomNukes plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase(COMMAND_NS)) {
            CustomLogger customLogger = new CustomLogger(plugin.getLogger(), sender);
            if((args.length >= 1) && (args[0].equalsIgnoreCase("reload"))) {
                if (sender.hasPermission(COMMAND_NS + ".reload")) {
                    plugin.reloadExplosivesConfig(customLogger);
                    customLogger.info("CustomNukes config reloaded.");
                    return true;
                }
            }
            else if((args.length >= 1) && (args[0].equalsIgnoreCase("clear"))) {
                if (sender.hasPermission(COMMAND_NS + ".clear")) {
                    plugin.clear();
                    customLogger.info("CustomNukes explosive blocks and active repeaters were removed.");
                    return true;
                }
            }
            else if((args.length >= 1) && (args[0].equalsIgnoreCase("give"))) {
                if (sender.hasPermission(COMMAND_NS + ".give")) {
                    boolean error = false;

                    Player player = null;
                    EItem explosive = null;
                    int amount = 0;

                    if(args.length < 3) {
                        customLogger.info(COMMAND_NS + " give <player> <explosive-key> <amount>");
                        error = true;
                    }

                    if(!error) {
                        player = plugin.getPlayerByName(args[1]);
                        if(null == player) {
                            customLogger.error(String.format("Player '%s' is not exists.", args[1]));
                            error = true;
                        }
                    }
                    if(!error) {
                        explosive = plugin.getExplosivesConfig().searchExplosiveByKey(args[2]);
                        if(null == explosive)
                            explosive = plugin.getExplosivesConfig().searchExplosiveByName(args[2]);

                        if(null == explosive) {
                            customLogger.error(String.format("Explosive '%s' is not exists.", args[2]));
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
                                customLogger.error("Amount should be an integer between 1 and 64.");
                                error = true;
                            }
                            if(!error) {
                                if(amount < 1) {
                                    customLogger.error("Amount should be at least 1.");
                                    error = true;
                                } else if(amount > 64) {
                                    customLogger.error("Amount should be at most 64.");
                                    error = true;
                                }
                            }
                        }
                    }
                    if(!error) {
                        player.getInventory().addItem(explosive.getCustomItemStack(amount));
                        customLogger.info(String.format("Player '%s' got %d * '%s'", player.getName(), amount, explosive.getName()));
                    }

                    return true;
                }
            }
            else if((args.length == 0) || (args[0].equalsIgnoreCase("help"))) {
                String Help = "==== CustomNukes help ====";
                if (sender.hasPermission(COMMAND_NS + ".reload"))
                    Help += "\n/" + COMMAND_NS + " reload - reload config from disk";
                if (sender.hasPermission(COMMAND_NS + ".give"))
                    Help += "\n/" + COMMAND_NS + " give <player> <explosive-key> <amount>";
                if (sender.hasPermission(COMMAND_NS + ".clear"))
                    Help += "\n/" + COMMAND_NS + " clear - remove all explosive blocks and active repeaters";
                Help += "\n";
                customLogger.info(Help);
                return true;
            }
        }
        return false;
    }
}
