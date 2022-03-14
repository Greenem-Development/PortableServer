package org.greenem.plugins.Server.commands;

import java.util.Calendar;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.greenem.plugins.Server.data.ConfigData;
import org.greenem.plugins.Server.data.OutsideData;
import org.greenem.plugins.Server.data.References;
import org.greenem.plugins.Server.game.TeamManager;
import org.greenem.plugins.Server.main.Main;
import org.greenem.plugins.Server.miscellaneous.Stash;
import org.greenem.plugins.Server.util.SimpleLogger;

public class AcceptTeam implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(Main.isSenderAPlayer(sender)) {
			if(args.length<=3) {
				if(Stash.isInteger(args[0])) {
					String outdatedMsg = ChatColor.RED + "The invite link is outdated!";
					TeamManager tm = References.invitationIdToTeam.get(Integer.parseInt(args[0]));
					if(tm==null) {
						sender.sendMessage(outdatedMsg);
						return false;
					}
					long time = 0;
					if(Stash.isLong(args[1])) {
						time = Long.parseLong(args[1]); 
					}
					else {
						if(!ConfigData.allowInvitesBeforeReload) {
							return false;
						}
					}
					if(!ConfigData.allowInvitesBeforeReload && OutsideData.timeOfPluginStart<time) {
						sender.sendMessage(outdatedMsg);
						return false;
					}
					if(!ConfigData.allowInvitesNicknameMismatch && !tm.leader.equals(args[1]) && !tm.leader.equals(args[2])) {
						sender.sendMessage(outdatedMsg);
						return false;
					}
					if(References.playersTeam.get(sender.getName())==tm) {
						sender.sendMessage(ChatColor.RED + "You have already joined that team!");
						return false;
					}
					if(References.playersTeam.get(sender.getName())!=null) {
						sender.sendMessage(ChatColor.RED + "You have a team already!"); // a little bad implementation
						return false;
					}
					tm.players.add(sender.getName());
					References.playersTeam.put(sender.getName(), tm);
					sender.sendMessage(ChatColor.GREEN + "You have successfully joined the team!");
					Player leader = Bukkit.getPlayerExact(tm.leader);
					if(leader!=null) {
						leader.sendMessage(ChatColor.WHITE + "" + ChatColor.BOLD + sender.getName() + ChatColor.RESET + "" + ChatColor.GREEN + " joined your team!");
					}
					//SimpleLogger.log(References.invitationIdToTeam.toString());
				}
				else {
					sender.sendMessage(ChatColor.RED + "You should put a number there! (from auto-command)");
				}
			}
			else {
				sender.sendMessage(ChatColor.RED + "You should specify only team id, maybe the time and maybe player name! (from auto-command)");
			}
		}
		return false;
	}

}
