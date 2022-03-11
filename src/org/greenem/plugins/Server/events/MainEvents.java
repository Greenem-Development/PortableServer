package org.greenem.plugins.Server.events;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.greenem.plugins.Server.data.Locs;
import org.greenem.plugins.Server.data.NamesAndLines;

import miscellaneous.WorldLoadHelper;

public class MainEvents implements Listener {
	@EventHandler
	public static void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		e.setJoinMessage(ChatColor.WHITE + "" + ChatColor.BOLD + p.getName() + ChatColor.RESET + "" + ChatColor.GRAY + " joined the game");
		// currently no authorization
		WorldLoadHelper.loadWorld(NamesAndLines.HubName);
		p.teleport(Locs.mainWorldSpawn);
	}
	
	@EventHandler
	public static void onLeave(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		e.setQuitMessage(ChatColor.WHITE + "" + ChatColor.BOLD + p.getName() + ChatColor.RESET + "" + ChatColor.GRAY + " left the game");
	}
}
