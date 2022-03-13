package org.greenem.plugins.Server.game;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.greenem.plugins.Server.miscellaneous.Stash;

public class PartyManager implements Listener {
	public ArrayList<String> players = new ArrayList<>();
	public String leader = null;
	public Inventory gameStartScreenLeader = null;
	public Inventory gameStartScreenNormal = null;
	
	public PartyManager (String leader) {
		this.leader = leader;
		checkExisting();
	}
	
	public void checkExisting() {
		if(gameStartScreenLeader==null) {
			gameStartScreenLeader = Bukkit.createInventory(null, 27, "Game setup menu");
		}
		if(gameStartScreenNormal==null) {
			gameStartScreenNormal = Bukkit.createInventory(null, 27, "Game settings menu");
		}
	}
	
	public void openGui(Player p) {
		p.openInventory(gameStartScreenLeader);
	}
	
	public void updateGui() {
		ItemStack GrayStainedPlane = Stash.createGuiItem(Material.GRAY_STAINED_GLASS_PANE, " ", " ", "");
		for (int i = 0; i < gameStartScreenLeader.getSize(); i++) {
			gameStartScreenLeader.setItem(i, GrayStainedPlane);
		}
		for (int i = 0; i < gameStartScreenNormal.getSize(); i++) {
			gameStartScreenNormal.setItem(i, GrayStainedPlane);
		}
		
		setItemOnBoth(1, Stash.createGuiItem(Material.NETHERITE_HOE, ChatColor.WHITE + "Mode", ChatColor.WHITE + "Murder Mystery"));
		setItemOnBoth(3, Stash.createGuiItem(Material.MAP, ChatColor.WHITE + "Map", ChatColor.WHITE + "Main"));
		setItemOnBoth(5, Stash.createGuiItem(Material.IRON_SWORD, ChatColor.WHITE + "Murderers", ChatColor.WHITE + "1"));
		setItemOnBoth(7, Stash.createGuiItem(Material.BOW, ChatColor.WHITE + "Detectives", ChatColor.WHITE + "1"));
		setItemOnBoth(9, Stash.createGuiItem(Material.CLOCK, ChatColor.WHITE + "Max time", ChatColor.WHITE + "5 Minutes"));
		
		gameStartScreenLeader.setItem(Stash.getSlotNumber(3, 5), Stash.createGuiItem(Material.GREEN_CONCRETE, ChatColor.WHITE + "Start"));
		gameStartScreenNormal.setItem(Stash.getSlotNumber(3, 5), Stash.createGuiItem(Material.RED_CONCRETE, ChatColor.WHITE + "Close"));
	}
	
	private void setItemOnBoth(int slot, ItemStack is) {
		gameStartScreenLeader.setItem(slot, is);
		gameStartScreenNormal.setItem(slot, is);
	}
	
	@EventHandler
	public void onGuiClick(InventoryClickEvent e) {
		if(e.getInventory()==gameStartScreenLeader || e.getInventory()==gameStartScreenNormal) {
			String name = e.getWhoClicked().getName();
			e.setCancelled(true);
			ItemStack item = e.getCurrentItem();
			if(item!=null) {
				if(players.contains(name)) {
					if(leader.equals(name)) {
						
					}
					else {
						
					}
				}
			}
		}
	}
}
