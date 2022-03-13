package org.greenem.plugins.Server.game;

import java.util.Calendar;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.greenem.plugins.Server.data.ConfigData;
import org.greenem.plugins.Server.data.References;
import org.greenem.plugins.Server.main.Main;
import org.greenem.plugins.Server.util.SimpleLogger;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class CreatePartyManager implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(Main.isSenderAPlayer(sender)) {
			createParty((Player) sender);
		}
		return false;
	}
	
	public static void createParty(Player p) {
		if(References.playersParty.get(p.getName())==null) {
			
			PartyManager partyManager = new PartyManager(p.getName());
			int id = References.addInvitationIdToParty(partyManager);
			References.playersParty.put(p.getName(), partyManager);
			
			for (Player player : Bukkit.getOnlinePlayers()) {
				if(!References.playersInGame.contains(player.getName())) {
					if(!player.equals(p)) { // uncomment
						TextComponent message = new TextComponent(ChatColor.WHITE + "" + ChatColor.BOLD + p.getName() + ChatColor.RESET + "" + ChatColor.WHITE + " is staring a game, ");
						TextComponent clickable = new TextComponent(ChatColor.UNDERLINE + "" + ChatColor.BOLD + "Click here to join!");
						
						String acceptCommand = "/acceptparty " + id;
						
						if(!ConfigData.allowInvitesBeforeReload) {
							acceptCommand += " " + Calendar.getInstance().getTimeInMillis();
						}
						
						if(!ConfigData.allowInvitesNicknameMismatch) {
							acceptCommand += " " + p.getName();
						}
						
						clickable.setClickEvent(new ClickEvent( ClickEvent.Action.RUN_COMMAND, acceptCommand));
						message.addExtra(clickable);
	//					message.addExtra( "!" ); // can add something if needed
				        player.spigot().sendMessage(message); //does this work on bukkit?
					}
				}
			}
			p.sendMessage(ChatColor.GREEN + "The invitation link was sent to other players");
			partyManager.updateGui();
			Bukkit.getPluginManager().registerEvents(partyManager, Main.getPlugin(Main.class));
			partyManager.openGui(p);
		}
		else {
			p.sendMessage(ChatColor.RED + "You already have a party!");
		}
	}
}
