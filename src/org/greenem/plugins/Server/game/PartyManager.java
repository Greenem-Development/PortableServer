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
import org.greenem.plugins.Server.data.MinigamesStaticData;
import org.greenem.plugins.Server.data.NamesAndLines;
import org.greenem.plugins.Server.miscellaneous.Stash;
import org.greenem.plugins.Server.util.SimpleLogger;

public class PartyManager implements Listener {
	public ArrayList<String> players = new ArrayList<>();
	public String leader = null;
	public Inventory gameStartScreenLeader = null;
	public Inventory gameStartScreenNormal = null;
	public Minigame selectedMinigame = null;
	public boolean inGame = false;
	public ArrayList<Integer> settingsNumbers = new ArrayList<>();
	
	public PartyManager (String leader) {
		this.leader = leader;
		players.add(leader);
		checkExisting();
//		initZeros();
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
//		initSettingsNumbers(); // lazy way
		ItemStack GrayStainedPlane = Stash.createGuiItem(Material.GRAY_STAINED_GLASS_PANE, " ", " ", "");
		for (int i = 0; i < gameStartScreenLeader.getSize(); i++) {
			gameStartScreenLeader.setItem(i, GrayStainedPlane);
		}
		for (int i = 0; i < gameStartScreenNormal.getSize(); i++) {
			gameStartScreenNormal.setItem(i, GrayStainedPlane);
		}
		
		if(selectedMinigame!=null) {
			switch (selectedMinigame) {
			case MURDER_MYSTERY: {
				setItemOnBoth(0, Stash.createGuiItem(Material.NETHERITE_HOE, ChatColor.WHITE + "Mode", getHighlightedList(new String[] {"Murder Mystery"}, ChatColor.WHITE, ChatColor.GOLD, 1)));
				setItemOnBoth(2, Stash.createGuiItem(Material.MAP, ChatColor.WHITE + "Map", getHighlightedList(getMapList(selectedMinigame), ChatColor.WHITE, ChatColor.GOLD, 1)));
				setItemOnBoth(4, Stash.createGuiItem(Material.IRON_SWORD, ChatColor.WHITE + "Murderers", getHighlightedList(new String[] {"1", "2", "3"}, ChatColor.WHITE, ChatColor.GOLD, 1)));
				// check if the total amount is not more that players (when ok click)
				setItemOnBoth(6, Stash.createGuiItem(Material.BOW, ChatColor.WHITE + "Detectives", getHighlightedList(new String[] {"1", "2", "3"}, ChatColor.WHITE, ChatColor.GOLD, 1)));
				setItemOnBoth(8, Stash.createGuiItem(Material.CLOCK, ChatColor.WHITE + "Max time", getHighlightedList(new String[] {"5 Minutes", "10 Minutes", "2 Minutes"}, ChatColor.WHITE, ChatColor.GOLD, 1)));
				break;
			}
			default:
				throw new IllegalArgumentException("Unexpected value: " + selectedMinigame);
			}	
		}
		
		gameStartScreenLeader.setItem(Stash.getSlotNumber(3, 4), Stash.createGuiItem(Material.RED_CONCRETE, ChatColor.WHITE + "Back"));
		gameStartScreenLeader.setItem(Stash.getSlotNumber(3, 6), Stash.createGuiItem(Material.GREEN_CONCRETE, ChatColor.WHITE + "Start"));
		gameStartScreenNormal.setItem(Stash.getSlotNumber(3, 5), Stash.createGuiItem(Material.RED_CONCRETE, ChatColor.WHITE + NamesAndLines.closeGuiText));
	}
	
	private String[] getMapList(Minigame minigame) {
		ArrayList<String> maps = MinigamesStaticData.minigameMaps.get(minigame);
		Object[] array = maps.toArray();
		String[] array2 = new String[array.length];
		for (int i = 0; i < array.length; i++) {
			array2[i] = MinigamesStaticData.getFullMapName(minigame, (String) array[i]);
		}
		return array2;
	}
	
	public String[] getHighlightedList(String[] strs, ChatColor normalColor, ChatColor highlightColor, int num) {
		int current = 0;
		if(settingsNumbers.size()<=num) {
			for (int i = settingsNumbers.size(); i <= num; i++) {
				settingsNumbers.add(null);
			}
		}
		
		if(settingsNumbers.get(num)!=null) {
			current = settingsNumbers.get(num);
		}
		else {
			settingsNumbers.set(num, 0); // maybe not needed already
		}
		for (int i = 0; i < strs.length; i++) {
			if(i==current) {
				strs[i] = highlightColor + strs[i];
			} else {
				strs[i] = normalColor + strs[i];
			}
		}
		return strs; // its actually a link, already modified, remember that
	}
	
	@Deprecated
	private void initSettingsNumbers() { 
		switch (selectedMinigame) {
		case MURDER_MYSTERY: {
			settingsNumbers.set(0, 1);
			settingsNumbers.set(1, MinigamesStaticData.minigameMaps.get(selectedMinigame).size());
			settingsNumbers.set(0, 1);
			settingsNumbers.set(0, 1);
			settingsNumbers.set(0, 1);
			break;
		}
		default:
			//throw new IllegalArgumentException("Unexpected value: " + );
		}
	}
	
	@Deprecated
	public void initZeros() {
		settingsNumbers.add(0);
		settingsNumbers.add(0);
		settingsNumbers.add(0);
		settingsNumbers.add(0);
		settingsNumbers.add(0);
		settingsNumbers.add(0);
		settingsNumbers.add(0);
		settingsNumbers.add(0);
		settingsNumbers.add(0);
		settingsNumbers.add(0);
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
			int slot = e.getSlot();
			if(item!=null) {
				if(players.contains(name)) {
					if(leader.equals(name)) {
						if(item.getType()==Material.RED_CONCRETE) {
							Bukkit.getPlayerExact(name).openInventory(GameSelectScreen.menu); // what if not a player? (not really)
							selectedMinigame = null;
							updateGui();
							for (String s : players) {
								Player player = Bukkit.getPlayerExact(s);
								if(player.getOpenInventory().getTopInventory()==gameStartScreenNormal) {
									player.closeInventory();
								}
							}
						}
						if(slot==2) { // maps currently

						}
						else {
							switch (selectedMinigame) {
							case MURDER_MYSTERY: {
								if(slot==4 || slot==6 || slot==8) {
									
								}
								break;
							}
							default:
								throw new IllegalArgumentException("Unexpected value: " + selectedMinigame);
							}
						}
					}
					else {
						if(item.getType()==Material.RED_CONCRETE) {
							Bukkit.getPlayerExact(name).closeInventory();
						}
					}
				}
			}
		}
	}
}
