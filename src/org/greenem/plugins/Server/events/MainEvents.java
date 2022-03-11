package org.greenem.plugins.Server.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.greenem.plugins.Server.data.Locs;
import org.greenem.plugins.Server.data.NamesAndLines;

import miscellaneous.WorldLoadHelper;

public class MainEvents implements Listener {
	@EventHandler
	public static void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		// currently no authorization
		WorldLoadHelper.loadWorld(NamesAndLines.HubName);
		p.teleport(Locs.mainWorldSpawn);
	}
}
