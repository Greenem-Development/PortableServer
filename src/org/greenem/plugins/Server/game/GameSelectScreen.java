package org.greenem.plugins.Server.game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.greenem.plugins.Server.data.NamesAndLines;
import org.greenem.plugins.Server.data.References;
import org.greenem.plugins.Server.miscellaneous.Stash;
import org.greenem.plugins.Server.util.SimpleLogger;

public class GameSelectScreen implements Listener {
	public static Inventory menu;
	
	public static void init() {
		menu = Bukkit.createInventory(null, 45, "Select Minigame");
		
		drawGui();
	}
	
	public static void drawGui() {
		ItemStack GrayStainedPlane = Stash.createGuiItem(Material.GRAY_STAINED_GLASS_PANE, " ", " ", "");
		for (int i = 0; i < menu.getSize(); i++) {
			menu.setItem(i, GrayStainedPlane);
		}
		
		menu.setItem(Stash.getSlotNumber(2, 5), Stash.createGuiItem(Material.NETHERITE_HOE, ChatColor.WHITE + "" + ChatColor.BOLD + "Murder Mystery", ChatColor.GRAY + "Find the murderer!"));
		menu.setItem(Stash.getSlotNumber(5, 5), Stash.createGuiItem(Material.RED_CONCRETE, ChatColor.WHITE + NamesAndLines.closeGuiText));
	}
	
	@EventHandler
	public void onGuiClick(InventoryClickEvent e) {
		if(e.getInventory()==menu) {
			e.setCancelled(true);
			ItemStack item = e.getCurrentItem();
			if(e.getWhoClicked() instanceof Player) {
				Player p = (Player) e.getWhoClicked();
				boolean requiresLeader = false;
				boolean isntLeader = false;
				if(item!=null) {
					if(item.getType().equals(Material.NETHERITE_HOE)) {
						requiresLeader = true;
						PartyManager pm = References.playersParty.get(p.getName());
						if(pm==null) {
//							p.chat("/createparty");
							CreatePartyManager.createParty(p);
							// add to party if not already
							// set current game in menu for party class
							pm = References.playersParty.get(p.getName());
							pm.selectedMinigame = Minigame.MURDER_MYSTERY;
							pm.updateGui();
							p.openInventory(pm.gameStartScreenLeader);
						}
						else if(pm.leader.equals(p.getName())) {
							pm.selectedMinigame = Minigame.MURDER_MYSTERY;
							pm.updateGui();
							p.openInventory(pm.gameStartScreenLeader);
							for (String s : pm.players) {
								if(!s.equals(p.getName()) && Bukkit.getPlayerExact(s).getOpenInventory().getTopInventory().equals(GameSelectScreen.menu)) {
//									Bukkit.getPlayerExact(s).closeInventory();
									Bukkit.getPlayerExact(s).openInventory(pm.gameStartScreenNormal);
								}
							}
						}
						else { // party, but not leader
							isntLeader = true;
//							SimpleLogger.log(p.getName() + " got a way to open minigame selection screen in party with no leader on him");
						}
					}
					else if(item.getType().equals(Material.RED_CONCRETE)) {
						p.closeInventory();
					}
					
					if(requiresLeader && isntLeader) {
						p.sendMessage(ChatColor.RED + "Only party owner can select the minigame!");
					}
				}
			}
			else {
				SimpleLogger.log("Strange type of WhoClicked - " + e.getWhoClicked().getClass().getName());
			}
		}
	}
}
