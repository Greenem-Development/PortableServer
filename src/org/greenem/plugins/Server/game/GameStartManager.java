package org.greenem.plugins.Server.game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.greenem.plugins.Server.data.References;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class GameStartManager {
	public static void setupGameStart(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if(!References.playersInGame.contains(player.getName())) {
//				if(!player.equals(sender)) { // uncomment
					TextComponent message = new TextComponent(ChatColor.WHITE + "" + ChatColor.BOLD + sender.getName() + ChatColor.RESET + "" + ChatColor.WHITE + " is staring a game, ");
					TextComponent clickable = new TextComponent(ChatColor.UNDERLINE + "" + ChatColor.BOLD + "Click here to join!");
					clickable.setClickEvent(new ClickEvent( ClickEvent.Action.RUN_COMMAND, "/acceptgame " + sender.getName()));
					message.addExtra(clickable);
//					message.addExtra( "!" ); // can add something if needed
			        player.spigot().sendMessage(message); //does this work on bukkit?
//				}
			}
		}
		
	}
}
