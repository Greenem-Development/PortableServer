package org.greenem.plugins.server.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.greenem.plugins.server.data.Locs;
import org.greenem.plugins.server.data.NamesAndLines;
import org.greenem.plugins.server.main.Main;

public class UnloadAll implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(command.getName().equalsIgnoreCase("unloadallworlds")) {
			if(Main.isSenderTheConsole(sender)) {
				unloadAllWorlds(sender.getName());
				sender.sendMessage(ChatColor.GREEN + "All the worlds have been unloaded!");
			}
		}
		return false;
	}
	
	public static void unloadAllWorlds(String whoRequested) {
		for (World w : Bukkit.getWorlds()) {
			if(!w.getName().equals(NamesAndLines.HubName)) {
				for (Player player : w.getPlayers()) {
					player.teleport(Locs.mainWorldSpawn);
					String message = ChatColor.GRAY + "You were teleported to the main world because the world you were in was unloaded";
					if(whoRequested!=null && !whoRequested.trim().equals("")) {
						message += " by " + whoRequested;
					}
					player.sendMessage(message);
				}
				Bukkit.unloadWorld(w, true);
			}
		}
	}
}
