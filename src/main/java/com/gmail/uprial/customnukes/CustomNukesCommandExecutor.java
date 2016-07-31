package com.gmail.uprial.customnukes;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.uprial.customnukes.common.CustomLogger;
import com.gmail.uprial.customnukes.schema.EItem;

class CustomNukesCommandExecutor implements CommandExecutor {
    public static final String COMMAND_NS = "customnukes";

    private final CustomNukes plugin;

    CustomNukesCommandExecutor(CustomNukes plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase(COMMAND_NS)) {
            CustomLogger customLogger = new CustomLogger(plugin.getLogger(), sender);
            //noinspection IfStatementWithTooManyBranches
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
                    boolean canGive = true;

                    Player player = null;
                    EItem explosive = null;
                    int amount = 0;

                    if(args.length < 3) {
                        customLogger.info(COMMAND_NS + " give <player> <explosive-key> <amount>");
                        canGive = false;
                    }

                    if(canGive) {
                        player = plugin.getPlayerByName(args[1]);
                        if(player == null) {
                            customLogger.error(String.format("Player '%s' is not exists.", args[1]));
                            canGive = false;
                        }
                    }
                    if(canGive) {
                        explosive = plugin.getExplosivesConfig().searchExplosiveByKey(args[2]);
                        if(explosive == null) {
                            explosive = plugin.getExplosivesConfig().searchExplosiveByName(args[2]);
                        }

                        if(explosive == null) {
                            customLogger.error(String.format("Explosive '%s' is not exists.", args[2]));
                            canGive = false;
                        }
                    }
                    if(canGive) {
                        if(args.length < 4) {
                            amount = 1;
                        } else {
                            try {
                                amount = Integer.valueOf(args[3]);
                            } catch (NumberFormatException ignored) {
                                customLogger.error("Amount should be an integer between 1 and 64.");
                                canGive = false;
                            }
                            if(canGive) {
                                if(amount < 1) {
                                    customLogger.error("Amount should be at least 1.");
                                    canGive = false;
                                } else if(amount > 64) {
                                    customLogger.error("Amount should be at most 64.");
                                    canGive = false;
                                }
                            }
                        }
                    }
                    if(canGive) {
                        player.getInventory().addItem(explosive.getCustomItemStack(amount));
                        customLogger.info(String.format("Player '%s' got %d * '%s'", player.getName(), amount, explosive.getName()));
                    }

                    return true;
                }
            }
            else if((args.length == 0) || (args[0].equalsIgnoreCase("help"))) {
                String helpString = "==== CustomNukes help ====";
                if (sender.hasPermission(COMMAND_NS + ".reload")) {
                    helpString += "\n/" + COMMAND_NS + " reload - reload config from disk";
                }
                if (sender.hasPermission(COMMAND_NS + ".give")) {
                    helpString += "\n/" + COMMAND_NS + " give <player> <explosive-key> <amount>";
                }
                if (sender.hasPermission(COMMAND_NS + ".clear")) {
                    helpString += "\n/" + COMMAND_NS + " clear - remove all explosive blocks and active repeaters";
                }
                helpString += "\n";
                customLogger.info(helpString);
                return true;
            }
        }
        return false;
    }
}
