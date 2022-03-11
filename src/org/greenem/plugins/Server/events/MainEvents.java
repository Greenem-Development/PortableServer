package org.greenem.plugins.Server.events;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.greenem.plugins.Server.data.Locs;
import org.greenem.plugins.Server.data.NamesAndLines;

public class MainEvents {
	@EventHandler
	public static void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		// currently no authorization
		p.teleport(Locs.mainWorldSpawn);
	}
}
