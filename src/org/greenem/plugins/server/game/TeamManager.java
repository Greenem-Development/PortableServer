package org.greenem.plugins.server.game;

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
import org.greenem.plugins.server.data.MinigamesStaticData;
import org.greenem.plugins.server.data.NamesAndLines;
import org.greenem.plugins.server.data.References;
import org.greenem.plugins.server.data.Structure;
import org.greenem.plugins.server.data.Worlds;
import org.greenem.plugins.server.main.Main;
import org.greenem.plugins.server.minigames.murderMystery.ExtraMurderMysteryData;
import org.greenem.plugins.server.minigames.murderMystery.MurderMysterySettingsItem;
import org.greenem.plugins.server.minigames.murderMystery.MurderMysterySetupSettings;
import org.greenem.plugins.server.miscellaneous.Stash;
import org.greenem.plugins.server.miscellaneous.WorldLoadHelper;
import org.greenem.plugins.server.util.SimpleLogger;

public class TeamManager implements Listener {
	public ArrayList<String> players = new ArrayList<>();
	public String leader = null;
	public Inventory gameStartScreenLeader = null;
	public Inventory gameStartScreenNormal = null;
	public Minigame selectedMinigame = null;
	public boolean inGame = false;
	public ArrayList<Integer> settingsNumbers = new ArrayList<>();
	public ArrayList<Integer> settingsMaxNumbers = new ArrayList<>();
	
	public TeamManager (String leader) {
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
				setItemOnBoth(0, Stash.createGuiItem(Material.NETHERITE_HOE, ChatColor.WHITE + "Mode", getHighlightedList(MurderMysterySetupSettings.getOptionsList(
						MurderMysterySettingsItem.MODE), ChatColor.WHITE, ChatColor.GOLD, MurderMysterySetupSettings.getArrayPosition(MurderMysterySettingsItem.MODE))));
				updateGuiMapList();
				setItemOnBoth(4, Stash.createGuiItem(Material.IRON_SWORD, ChatColor.WHITE + "Murderers", getHighlightedList(MurderMysterySetupSettings.getOptionsList(
						MurderMysterySettingsItem.MURDERERS), ChatColor.WHITE, ChatColor.GOLD, MurderMysterySetupSettings.getArrayPosition(MurderMysterySettingsItem.MURDERERS))));
				// check if the total amount is not more that players (when ok click)
				setItemOnBoth(6, Stash.createGuiItem(Material.BOW, ChatColor.WHITE + "Detectives", getHighlightedList(MurderMysterySetupSettings.getOptionsList(
						MurderMysterySettingsItem.DETECTIVES), ChatColor.WHITE, ChatColor.GOLD, MurderMysterySetupSettings.getArrayPosition(MurderMysterySettingsItem.DETECTIVES))));
				setItemOnBoth(8, Stash.createGuiItem(Material.CLOCK, ChatColor.WHITE + "Max time", getHighlightedList(MurderMysterySetupSettings.getOptionsList(
						MurderMysterySettingsItem.MAX_TIME), ChatColor.WHITE, ChatColor.GOLD, MurderMysterySetupSettings.getArrayPosition(MurderMysterySettingsItem.MAX_TIME))));
				break;
			}
			default:
				throw new IllegalArgumentException("Unexpected value: " + selectedMinigame);
			}	
		}
		
		gameStartScreenLeader.setItem(Stash.getSlotNumber(3, 4), Stash.createGuiItem(Material.GREEN_CONCRETE, ChatColor.WHITE + "Start"));
		gameStartScreenLeader.setItem(Stash.getSlotNumber(3, 6), Stash.createGuiItem(Material.RED_CONCRETE, ChatColor.WHITE + "Back"));
		gameStartScreenNormal.setItem(Stash.getSlotNumber(3, 5), Stash.createGuiItem(Material.RED_CONCRETE, ChatColor.WHITE + NamesAndLines.closeGuiText));
	}
	
	public void updateGuiMapList() {
		int slot = getMapListSlotInGui(selectedMinigame);
		int arrayPos = getMapListPosInArray(selectedMinigame);
		
		setItemOnBoth(slot, Stash.createGuiItem(Material.MAP, ChatColor.WHITE + "Map", getHighlightedList(getMapList(selectedMinigame), ChatColor.WHITE, ChatColor.GOLD, arrayPos)));
	}
	
	private String[] getMapList(Minigame minigame) {
		ArrayList<String> maps = MinigamesStaticData.minigameMaps.get(minigame);
		Object[] array = maps.toArray();
		String[] array2 = new String[array.length];
		for (int i = 0; i < array.length; i++) {
			String systemMap = (String) array[i];
			String map = MinigamesStaticData.getFullMapName(minigame, systemMap);
			int avaliable = getWorldsAvaliable(systemMap);
			String worldsOrNotS = "world";
			if(avaliable!=1) {
				worldsOrNotS += "s";
			}
			String strAval = String.valueOf(avaliable);
			if(avaliable==0) {
				strAval = "no";
			}
			ChatColor color = ChatColor.GRAY;
			String exclamationMark = "";
			int pos = getMapListPosInArray(selectedMinigame);
			System.out.println(settingsNumbers.toString());
			System.out.println(pos);
			if(strAval.equals("no") && ((settingsNumbers.size()-1==pos) || (settingsNumbers.size()>pos && settingsNumbers.get(pos)==i))) { // weird ( && i!=0)
				color = ChatColor.RED;
				exclamationMark = "!";
			}
			array2[i] = map + color + " (" + strAval + " " + worldsOrNotS + " avaliable" + exclamationMark + ")";
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
		
		makeNewIndexes(settingsMaxNumbers, num);
		
		settingsMaxNumbers.set(num, strs.length);
		return strs; // its actually a link, already modified, remember that
	}
	
	private void makeNewIndexes(ArrayList<Integer> arrayList, int num) {
		if(arrayList.size()<=num) {
			for (int i = arrayList.size(); i <= num; i++) {
				arrayList.add(null);
			}
		}
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
			Player p = (Player) e.getWhoClicked();
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
						if(item.getType()==Material.GREEN_CONCRETE) {
							String map = getCurrentSelectedMapSystemName(selectedMinigame);
							if(getWorldsAvaliable(map)==0) {
								p.sendMessage(NamesAndLines.noMapsAvaliable);
							}
							else {
								switch (selectedMinigame) {
								case MURDER_MYSTERY: {
									ExtraMurderMysteryData data = new ExtraMurderMysteryData();
									data.amountOfMurderers = MurderMysterySetupSettings.getAmountOfMurderers(settingsNumbers.get(MurderMysterySetupSettings.getArrayPosition(MurderMysterySettingsItem.MURDERERS)));
									data.amountOfDetectives = MurderMysterySetupSettings.getAmountOfDetectives(settingsNumbers.get(MurderMysterySetupSettings.getArrayPosition(MurderMysterySettingsItem.DETECTIVES)));
									data.fullTime = MurderMysterySetupSettings.getMaxTimeInt(settingsNumbers.get(MurderMysterySetupSettings.getArrayPosition(MurderMysterySettingsItem.MAX_TIME)));
									GameCreation.startNewGame(p, selectedMinigame, map, data);
									break;
								}
								default:
									throw new IllegalArgumentException("Unexpected value: " + selectedMinigame);
								}
							}
						}
						else {
							if(selectedMinigame!=null) {
								switch (selectedMinigame) {
								case MURDER_MYSTERY: {
									if(slot==2 || slot==4 || slot==6 || slot==8) {
										int newSlot = slot/2;
										makeNewIndexes(settingsNumbers, newSlot);
										makeNewIndexes(settingsMaxNumbers, newSlot);
										SimpleLogger.log(settingsNumbers.toString());
										settingsNumbers.set(newSlot, (settingsNumbers.get(newSlot)+1) % settingsMaxNumbers.get(newSlot));
										updateGui();
									}
									break;
								}
								default:
									throw new IllegalArgumentException("Unexpected value: " + selectedMinigame);
								}
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
	
	public String getCurrentSelectedMapSystemName(Minigame minigame) {
		ArrayList<String> maps = MinigamesStaticData.minigameMaps.get(minigame);
		return maps.get(settingsNumbers.get(getMapListPosInArray(minigame)));
	}
	
	private int getWorldsAvaliable(String map) {
		boolean foundWorldsEnd = false; // does not work if like world1 doesn't exist, think about it later (dublicated comment)
		int avaliableAmount = 0;
		int number = 1;
		while (!foundWorldsEnd) {
//			SimpleLogger.log("while"); //uncomment if loop
			String worldName = NamesAndLines.getMinigameNameInFolder(selectedMinigame) + "_" + map + "_" + number;
			if(WorldLoadHelper.worldExists(worldName)) {
				if(!Worlds.usedWorlds.contains(worldName) && !Worlds.temporaryUnaliableWorlds.contains(worldName)) {
					avaliableAmount++;
				} else {
					if(foundWorldsEnd) {
						break;
					}
				}
			} else {
				// does this work?
				foundWorldsEnd = true;
				break;
			}
			number++;
		}
		return avaliableAmount;
	}
	
	public int getMapListSlotInGui(Minigame minigame) { // the grid
		switch (minigame) {
		case MURDER_MYSTERY: {
			return 2;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + selectedMinigame);
		}
	}
	
	public int getMapListPosInArray(Minigame minigame) { // array
		switch (minigame) {
		case MURDER_MYSTERY: {
			return 1;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + selectedMinigame);
		}
	}
}
