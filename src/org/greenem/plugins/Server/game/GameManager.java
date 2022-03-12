package org.greenem.plugins.Server.game;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.greenem.plugins.Server.data.Worlds;

import miscellaneous.WorldLoadHelper;

public class GameManager {
	public Minigame minigameType = null;
	public ArrayList<String> players = new ArrayList<>();
	public ArrayList<String> actualPlayers = new ArrayList<>();
	public String worldName = null;
	public String map = null;
	
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
			case MURDER_MUSTERY: {
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
			case MURDER_MUSTERY: {
				worldName = searchForAvaliableWorld("murder_mystery_");
			}
			case AMONG_US: {
				worldName = searchForAvaliableWorld("among_us_");
			}
			case SKYWARS: {
				worldName = searchForAvaliableWorld("skywars_");
			}
			case BEDWARS: {
				worldName = searchForAvaliableWorld("bedwars_");
			}
			case TNT_RUN: {
				worldName = searchForAvaliableWorld("tnt_run_");
			}
		default:
			throw new IllegalArgumentException("Unexpected value in prepareWorld: " + minigameType);
		}
	}
	
	private String searchForAvaliableWorld(String minigameName) {
		boolean foundWorldsEnd = false; // does not work if like world1 doesn't exist, think about it later
		int avaliableNumber = 1;
		while (!foundWorldsEnd) {
			String worldName = minigameName + avaliableNumber;
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
