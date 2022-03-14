package org.greenem.plugins.Server.game;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.greenem.plugins.Server.data.Locs;
import org.greenem.plugins.Server.data.MinigamesStaticData;
import org.greenem.plugins.Server.data.NamesAndLines;
import org.greenem.plugins.Server.data.References;
import org.greenem.plugins.Server.data.Structure;
import org.greenem.plugins.Server.data.Worlds;
import org.greenem.plugins.Server.minigames.murderMystery.MurderMysterySpawnLocations;
import org.greenem.plugins.Server.miscellaneous.Preparations;
import org.greenem.plugins.Server.miscellaneous.WorldLoadHelper;
import org.greenem.plugins.Server.util.SimpleLogger;

public class GameManager {
	Random rand = new Random();
	
	public Minigame minigameType = null;
	public String map = "default"; // DON'T USE "default" AS THE NAME LATER
	public ArrayList<String> players = new ArrayList<>();
	public ArrayList<String> actualPlayers = new ArrayList<>();
	public String worldName = null;
	public ExtraGameData extraGameData;
	
	/**extraGameData has to be already set**/
	public int start() {
		if(extraGameData==null) {
			return 1;
		}
		SimpleLogger.log("start");
		prepareWorld();
		if(worldName == null) {
			return -1;
		}
		for (String s : players) {
			actualPlayers.add(s);
		}
		Bukkit.getPluginManager().registerEvents(extraGameData, References.plugin);
		extraGameData.start();
		return 0;
	}
	
	public void stop() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if(p.getWorld().getName().equals(worldName)) {
				p.teleport(Locs.mainWorldSpawn);
				Preparations.preparePlayerInHub(p);
				References.playersInGame.remove(p.getName());
			}
		}
		Bukkit.unloadWorld(worldName, true); // later false
		Worlds.usedWorlds.remove(worldName);
		References.updateAllMapListsInGui(minigameType);
	}
	
	public void prepareWorld() {
		worldName = searchForAvaliableWorld(NamesAndLines.getMinigameNameInFolder(minigameType));
	}
	
	private String searchForAvaliableWorld(String minigameName) {
		if(map.equals("default")) {
			map = MinigamesStaticData.defaultMap.get(minigameType);
		}
		SimpleLogger.log("searchForAvaliableWorld");
		boolean foundWorldsEnd = false; // does not work if like world1 doesn't exist, think about it later
		int avaliableNumber = 1;
		while (!foundWorldsEnd) {
			SimpleLogger.log("while");
			String worldName = minigameName + "_" + map + "_" + avaliableNumber;
			if(WorldLoadHelper.worldExists(worldName)) {
				if(!Worlds.usedWorlds.contains(worldName)) {
					WorldLoadHelper.loadWorld(worldName);
					// maybe something else???
					Worlds.usedWorlds.add(worldName);
					SimpleLogger.log("return");
					return worldName;
				} else {
					if(foundWorldsEnd) {
						break;
					}
				}
			} else {
				SimpleLogger.log("else");
				foundWorldsEnd = true;
				break;
			}
			avaliableNumber++;
		}
		if(avaliableNumber == 1) {
			SimpleLogger.log(ChatColor.RED + "There is no singe map for " + minigameName + " minigame with map " + map + ", fix it!"
					+ "example for murder mystery, map 1, instance 1: murder_mystery_map1_1"); 
		}
		return null;
	}
}
