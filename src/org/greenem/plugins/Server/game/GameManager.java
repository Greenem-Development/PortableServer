package org.greenem.plugins.Server.game;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.greenem.plugins.Server.data.Worlds;

import miscellaneous.WorldLoadHelper;

public class GameManager {
	public Minigame minigameType = null;
	public String map = "default";
	public ArrayList<String> players = new ArrayList<>();
	public ArrayList<String> actualPlayers = new ArrayList<>();
	public String worldName = null;
	
	public int start() {
		prepareWorld();
		if(worldName == null) {
			return -1;
		}
		for (String s : players) {
			actualPlayers.add(s);
		}
		for (String s : actualPlayers) {
			switch (minigameType) {
			case MURDER_MYSTERY: {
				Bukkit.getPlayerExact(s).teleport(new Location(Bukkit.getWorld(worldName), 0, 101, 0));
			}
			case AMONG_US: {
				
			}
			case SKYWARS: {
				
			}
			case BEDWARS: {
				
			}
			case TNT_RUN: {
				
			}
			default:
				throw new IllegalArgumentException("Unexpected value in prepareWorld: " + minigameType);
			}
		}
		return 0;
	}
	
	public void stop() {
		
	}
	
	public void prepareWorld() {
		switch (minigameType) {
		case MURDER_MYSTERY: {
			worldName = searchForAvaliableWorld("murder_mystery", "map1");
		}
		case AMONG_US: {
			worldName = searchForAvaliableWorld("among_us_", "map1");
		}
		case SKYWARS: {
			worldName = searchForAvaliableWorld("skywars_", "map1");
		}
		case BEDWARS: {
			worldName = searchForAvaliableWorld("bedwars_", "map1");
		}
		case TNT_RUN: {
			worldName = searchForAvaliableWorld("tnt_run_", "map1");
		}
		default:
			throw new IllegalArgumentException("Unexpected value in prepareWorld: " + minigameType);
		}
	}
	
	private String searchForAvaliableWorld(String minigameName, String map) {
		boolean foundWorldsEnd = false; // does not work if like world1 doesn't exist, think about it later
		int avaliableNumber = 1;
		while (!foundWorldsEnd) {
			String worldName = minigameName + "_" + map + "_" + avaliableNumber;
			if(WorldLoadHelper.worldExists(worldName)) {
				if(!Worlds.usedWorlds.contains(worldName)) {
					WorldLoadHelper.loadWorld(worldName);
					// maybe something else???
					Worlds.usedWorlds.add(worldName);
					return worldName;
				}
			} else {
				foundWorldsEnd = true;
			}	
		}
//		if(avaliableNumber == 1) {
//			return 
//		}
		return null;
	}
}
