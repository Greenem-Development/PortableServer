package org.greenem.plugins.Server.game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import miscellaneous.Stash;

public class GameSelectScreen implements Listener {
	public static Inventory menu;
	
	public static void init() {
		menu = Bukkit.createInventory(null, 45);
		
		drawGui();
	}
	
	public static void drawGui() {
		ItemStack GrayStainedPlane = Stash.createGuiItem(Material.GRAY_STAINED_GLASS_PANE, " ", " ", "");
		for (int i = 0; i < menu.getSize(); i++) {
			menu.setItem(i, GrayStainedPlane);
		}
		
		menu.setItem(Stash.getSlotNumber(3, 5), Stash.createGuiItem(Material.NETHERITE_HOE, ChatColor.WHITE + "" + ChatColor.BOLD + "Murder Mystery", ChatColor.GRAY + "Find the murderer!"));
	}
	
	@EventHandler
	public void onGuiClick(InventoryClickEvent e) {
		if(e.getInventory()==menu) {
			e.setCancelled(true);
			ItemStack item = e.getCurrentItem();
			if(item!=null) {
				if(item.getType().equals(Material.NETHERITE_HOE)) {
					// add to party if not already
					// set current game in menu for party class
				}
			}
		}
	}
}
