package org.greenem.plugins.server.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.greenem.plugins.server.data.References;
import org.greenem.plugins.server.game.TeamManager;
import org.greenem.plugins.server.main.Main;

public class LeaveTeam implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(Main.isSenderAPlayer(sender)) {
			if(args.length==0) {
				leaveTeam((Player) sender);
			}
		}
		return false;
	}
	
	public static void leaveTeam(Player p) {
		TeamManager tm = References.playersTeam.get(p.getName());
		if(tm!=null) {
			tm.players.remove(p.getName());
			if(References.playersTeam.get(p.getName()).players.size()==0) {
				References.playersTeam.put(p.getName(), null);
			}
			References.playersTeam.remove(p.getName());
			p.sendMessage(ChatColor.GREEN + "You have left the team");
		}
	}
}
