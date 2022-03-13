package org.greenem.plugins.Server.game;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import miscellaneous.Stash;

public class PartyManager {
	public ArrayList<String> players = new ArrayList<>();
	public String leader = null;
	public Inventory gameStartScreen = null;
	
	public PartyManager (String leader) {
		this.leader = leader;
		checkExisting();
	}
	
	public void checkExisting() {
		if(gameStartScreen==null) {
			gameStartScreen = Bukkit.createInventory(null, 27, "Game setup menu");
		}
	}
	
	public void openGui(Player p) {
		p.openInventory(gameStartScreen);
	}
	
	public void updateGui() {
		ItemStack GrayStainedPlane = Stash.createGuiItem(Material.GRAY_STAINED_GLASS_PANE, " ", " ", "");
		gameStartScreen.all(GrayStainedPlane);
		gameStartScreen.setItem(5, Stash.createGuiItem(Material.WHITE_STAINED_GLASS_PANE, ChatColor.WHITE + "Text", ChatColor.GOLD + "Desc", ""));
	}
}
