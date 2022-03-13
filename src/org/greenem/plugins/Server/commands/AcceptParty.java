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
import org.greenem.plugins.Server.game.PartyManager;
import org.greenem.plugins.Server.main.Main;
import org.greenem.plugins.Server.miscellaneous.Stash;
import org.greenem.plugins.Server.util.SimpleLogger;

public class AcceptParty implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(Main.isSenderAPlayer(sender)) {
			if(args.length<=3) {
				if(Stash.isInteger(args[0])) {
					String outdatedMsg = ChatColor.RED + "The invite link is outdated!";
					PartyManager pm = References.invitationIdToParty.get(Integer.parseInt(args[0]));
					if(pm==null) {
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
					if(!ConfigData.allowInvitesNicknameMismatch && !pm.leader.equals(args[1]) && !pm.leader.equals(args[2])) {
						sender.sendMessage(outdatedMsg);
						return false;
					}
					if(References.playersParty.get(sender.getName())==pm) {
						sender.sendMessage(ChatColor.RED + "You have already joined that party!");
						return false;
					}
					if(References.playersParty.get(sender.getName())!=null) {
						sender.sendMessage(ChatColor.RED + "You have a party already!"); // a little bad implementation
						return false;
					}
					pm.players.add(sender.getName());
					References.playersParty.put(sender.getName(), pm);
					sender.sendMessage(ChatColor.GREEN + "You have successfully joined the party!");
					Player leader = Bukkit.getPlayerExact(pm.leader);
					if(leader!=null) {
						leader.sendMessage(ChatColor.WHITE + "" + ChatColor.BOLD + sender.getName() + ChatColor.RESET + "" + ChatColor.GREEN + " joined your party!");
					}
					//SimpleLogger.log(References.invitationIdToParty.toString());
				}
				else {
					sender.sendMessage(ChatColor.RED + "You should put a number there!");
				}
			}
			else {
				sender.sendMessage(ChatColor.RED + "You should specify only party id, maybe the time and maybe player name!");
			}
		}
		return false;
	}

}
