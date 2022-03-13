package org.greenem.plugins.Server.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.greenem.plugins.Server.data.References;
import org.greenem.plugins.Server.game.PartyManager;
import org.greenem.plugins.Server.main.Main;

public class LeaveParty implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(Main.isSenderAPlayer(sender)) {
			if(args.length==0) {
				PartyManager pm = References.playersParty.get(sender.getName());
				if(pm!=null) {
					pm.players.remove(sender.getName());
					if(References.playersParty.get(sender.getName()).players.size()==0) {
						References.playersParty.put(sender.getName(), null);
					}
					References.playersParty.remove(sender.getName());
					sender.sendMessage(ChatColor.GREEN + "You have left the party");
				}
			}
		}
		return false;
	}
}
