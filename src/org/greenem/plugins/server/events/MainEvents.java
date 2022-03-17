package org.greenem.plugins.server.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.greenem.plugins.server.data.Locs;
import org.greenem.plugins.server.data.NamesAndLines;
import org.greenem.plugins.server.data.References;
import org.greenem.plugins.server.game.GameSelectScreen;
import org.greenem.plugins.server.game.TeamManager;
import org.greenem.plugins.server.miscellaneous.Preparations;
import org.greenem.plugins.server.miscellaneous.WorldLoadHelper;
import static org.greenem.plugins.server.util.SimpleLogger.log;
import static org.greenem.plugins.server.util.SimpleLogger.debug;

public class MainEvents implements Listener {
	@EventHandler
	public static void onJoin(PlayerJoinEvent e) {
		e.setJoinMessage(ChatColor.WHITE + "" + ChatColor.BOLD + e.getPlayer().getName() + ChatColor.RESET + "" + ChatColor.GRAY + " joined the game");
		operationsAsIfJoined(e.getPlayer());
	}
	
	private static void operationsAsIfJoined(Player p) {
		
		// currently no authorization
		WorldLoadHelper.loadWorld(NamesAndLines.HubName);
		p.teleport(Locs.mainWorldSpawn);
		Preparations.preparePlayerInHub(p);
		p.getInventory().setHeldItemSlot(0);
	}
	
	@EventHandler
	public static void onLeave(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		e.setQuitMessage(ChatColor.WHITE + "" + ChatColor.BOLD + p.getName() + ChatColor.RESET + "" + ChatColor.GRAY + " left the game");
	}
	
	@EventHandler
	public static void itemInHandClick(PlayerInteractEvent e) {
		// what about Action.Physical?
		Player p = e.getPlayer();
		if((e.getAction()==Action.RIGHT_CLICK_AIR || e.getAction()==Action.RIGHT_CLICK_BLOCK) &&
				e.getItem()!=null && e.getItem().getType()==Material.NETHER_STAR && e.getItem().getItemMeta().getDisplayName().equals(NamesAndLines.gameMenuItemName)) {
			TeamManager tm = References.playersTeam.get(p.getName());
			if(tm==null || (tm!=null && tm.selectedMinigame==null)) { // do I need the second null check?
				e.getPlayer().openInventory(GameSelectScreen.menu);
			}
			else if(tm.leader.equals(p.getName())) {
				e.getPlayer().openInventory(tm.gameStartScreenLeader);
			}
			else {
				e.getPlayer().openInventory(tm.gameStartScreenNormal);
			}
		}
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent e) {
		Player p = e.getPlayer();
//		if(!References.playersTeam.get(p.getName()).inGame) {
		Bukkit.getScheduler().runTaskLater(References.plugin, new Runnable() {		
			@Override
			public void run() {
//				p.teleport(Locs.mainWorldSpawn); // what happens after world reload or triggering it while the world is offline
				operationsAsIfJoined(p);
			}
		}, 1);
//		} else {
//			log(p.getName() + " died in a game and was respawned at spawn");
//			p.teleport(Locs.mainWorldSpawn); // temporary
//		}
	}
}
