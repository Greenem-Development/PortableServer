package org.greenem.plugins.server.worldsVersionControl;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ReplaceWorldCommand {
	public static void replaceWorldCommand(CommandSender sender, String[] args) {
		String from = null;
		String to = null;
		if(args.length==0) {
			sender.sendMessage(ChatColor.RED + "Specify the world name to copy from and/or to!");
			return;
		}
		if(args.length==1) {
			from = args[0];
			to = from;
		} else if(args.length==2) {
			from = args[0];
			to = args[1];
		}
		if(BuildingServerOperations.buildingServerWorlds.contains(from)) {
			String message = BuildingServerOperations.replaceWorlds(from, to);
			if(message!=null) {
				sender.sendMessage(message);
			}
		}
		else {
			sender.sendMessage(ChatColor.RED + "That world isn't whitelisted on the building server or doesn't exist!"); // also make a different messsage to
			// such things if there was config loading problem to be correct here
			return;
		}
	}
}
