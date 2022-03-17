package org.greenem.plugins.server.game;

import java.awt.Desktop.Action;
import java.util.Calendar;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.greenem.plugins.server.data.ConfigData;
import org.greenem.plugins.server.data.References;
import org.greenem.plugins.server.main.Main;
import org.greenem.plugins.server.util.SimpleLogger;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class CreateTeamManager implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(Main.isSenderAPlayer(sender)) {
			createTeam((Player) sender);
		}
		return false;
	}
	
	public static void createTeam(Player p) {
		if(References.playersTeam.get(p.getName())==null) {
			
			TeamManager teamManager = new TeamManager(p.getName());
			int id = References.addInvitationIdToTeam(teamManager);
			References.playersTeam.put(p.getName(), teamManager);
			
			for (Player player : Bukkit.getOnlinePlayers()) {
				if(!References.playersInGame.contains(player.getName())) {
					if(!player.equals(p)) { // uncomment
						TextComponent message = new TextComponent(ChatColor.WHITE + "" + ChatColor.BOLD + p.getName() + ChatColor.RESET + "" + ChatColor.WHITE + " is staring a game, ");
						TextComponent clickable = new TextComponent(ChatColor.UNDERLINE + "" + ChatColor.BOLD + "Click here to join!");
						
						String acceptCommand = "/acceptteam " + id;
						
						if(!ConfigData.allowInvitesBeforeReload) {
							acceptCommand += " " + Calendar.getInstance().getTimeInMillis();
						}
						
						if(!ConfigData.allowInvitesNicknameMismatch) {
							acceptCommand += " " + p.getName();
						}
						
						clickable.setClickEvent(new ClickEvent( ClickEvent.Action.RUN_COMMAND, acceptCommand));
//						clickable.setHoverEvent(new HoverEvent()); // to do later, show that player's name
						message.addExtra(clickable);
	//					message.addExtra( "!" ); // can add something if needed
				        player.spigot().sendMessage(message); //does this work on bukkit?
					}
				}
			}
			p.sendMessage(ChatColor.GREEN + "The invitation link was sent to other players");
			teamManager.updateGui();
			Bukkit.getPluginManager().registerEvents(teamManager, References.plugin);
			teamManager.openGui(p);
		}
		else {
			p.sendMessage(ChatColor.RED + "You already have a team!");
		}
	}
}
