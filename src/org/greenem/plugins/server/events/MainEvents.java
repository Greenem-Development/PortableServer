package org.greenem.plugins.server.events;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.greenem.plugins.server.data.Locs;
import org.greenem.plugins.server.data.NamesAndLines;
import org.greenem.plugins.server.data.References;
import org.greenem.plugins.server.game.GameSelectScreen;
import org.greenem.plugins.server.game.TeamManager;
import org.greenem.plugins.server.miscellaneous.Preparations;
import org.greenem.plugins.server.miscellaneous.WorldLoadHelper;

public class MainEvents implements Listener {
	@EventHandler
	public static void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		e.setJoinMessage(ChatColor.WHITE + "" + ChatColor.BOLD + p.getName() + ChatColor.RESET + "" + ChatColor.GRAY + " joined the game");
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
}
