package org.greenem.plugins.Server.minigames.murderMystery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class MurderMysteryLocations {
	public static Map<String, ArrayList<Location>> gameStartSpawnLocations = new HashMap<>();
	public static Map<String, ArrayList<Location>> goldSpawnLocations = new HashMap<>();
	
	public static void init() {
		initPlayerSpawnLocations();
		initGoldSpawnLocations();
	}
	
	private static void initPlayerSpawnLocations() {
		ArrayList<Location> current = new ArrayList<>();
		current.add(new Location(Bukkit.getWorld(""), 0, 102, 0));
		current.add(new Location(Bukkit.getWorld(""), 3, 102, 3));
		current.add(new Location(Bukkit.getWorld(""), 10, 105, -10));
		current.add(new Location(Bukkit.getWorld(""), 7, 100, 10));
		current.add(new Location(Bukkit.getWorld(""), 14, 99, 12));
		current.add(new Location(Bukkit.getWorld(""), 14, 99, 23));
		current.add(new Location(Bukkit.getWorld(""), 20, 99, 22));
		current.add(new Location(Bukkit.getWorld(""), 20, 99, 32));
		current.add(new Location(Bukkit.getWorld(""), 19, 99, 12));
		current.add(new Location(Bukkit.getWorld(""), 13, 99, 3));
		current.add(new Location(Bukkit.getWorld(""), 20, 99, 3));
		current.add(new Location(Bukkit.getWorld(""), 17, 99, -4));
		current.add(new Location(Bukkit.getWorld(""), 13, 99, -23));
		current.add(new Location(Bukkit.getWorld(""), -2, 99, -36));
		current.add(new Location(Bukkit.getWorld(""), -17, 99, -26));
		current.add(new Location(Bukkit.getWorld(""), -14, 99, -12));
		current.add(new Location(Bukkit.getWorld(""), -6, 101, -7));
		current.add(new Location(Bukkit.getWorld(""), 15, 108, 5));
		current.add(new Location(Bukkit.getWorld(""), 20, 108, 11));
		current.add(new Location(Bukkit.getWorld(""), 32, 108, 12));
		current.add(new Location(Bukkit.getWorld(""), 30, 108, 0));
		current.add(new Location(Bukkit.getWorld(""), 27, 103, 8));
		current.add(new Location(Bukkit.getWorld(""), 26, 101, 14));
		current.add(new Location(Bukkit.getWorld(""), 32, 103, 21));
		current.add(new Location(Bukkit.getWorld(""), -16, 106, 7));
		current.add(new Location(Bukkit.getWorld(""), -18, 108, -5));
		current.add(new Location(Bukkit.getWorld(""), -23, 108, -9));
		gameStartSpawnLocations.put("map1", current);
	}
	
	private static void initGoldSpawnLocations() {
		ArrayList<Location> current = new ArrayList<>();
		current.add(new Location(Bukkit.getWorld(""), 1, 102, 5));
		current.add(new Location(Bukkit.getWorld(""), -5, 102, -2));
		current.add(new Location(Bukkit.getWorld(""), 9, 103, -5));
		current.add(new Location(Bukkit.getWorld(""), 12, 106, -3));
		current.add(new Location(Bukkit.getWorld(""), 20, 108, 1));
		current.add(new Location(Bukkit.getWorld(""), 33, 108, 3));
		current.add(new Location(Bukkit.getWorld(""), 30, 108, 8));
		current.add(new Location(Bukkit.getWorld(""), 33, 108, 14));
		current.add(new Location(Bukkit.getWorld(""), 19, 108, 12));
		current.add(new Location(Bukkit.getWorld(""), 11, 108, 15));
		current.add(new Location(Bukkit.getWorld(""), 8, 108, 13));
		current.add(new Location(Bukkit.getWorld(""), 10, 100, 7));
		current.add(new Location(Bukkit.getWorld(""), 12, 99, 15));
		current.add(new Location(Bukkit.getWorld(""), 18, 99, 11));
		current.add(new Location(Bukkit.getWorld(""), 19, 99, 22));
		current.add(new Location(Bukkit.getWorld(""), 13, 99, 35));
		current.add(new Location(Bukkit.getWorld(""), 21, 99, 33));
		current.add(new Location(Bukkit.getWorld(""), 13, 99, 40));
		current.add(new Location(Bukkit.getWorld(""), 22, 99, 45));
		current.add(new Location(Bukkit.getWorld(""), 18, 97, 42));
		current.add(new Location(Bukkit.getWorld(""), 15, 95, 38));
		current.add(new Location(Bukkit.getWorld(""), 16, 99, 2));
		current.add(new Location(Bukkit.getWorld(""), 18, 99, -11));
		current.add(new Location(Bukkit.getWorld(""), 6, 99, -29));
		current.add(new Location(Bukkit.getWorld(""), -7, 99, -36));
		current.add(new Location(Bukkit.getWorld(""), -17, 99, -26));
		current.add(new Location(Bukkit.getWorld(""), -18, 99, -14));
		current.add(new Location(Bukkit.getWorld(""), -7, 100, -10));
		goldSpawnLocations.put("map1", current);
	}
	
	public static Location getMurderMysterySpectatorSpawn(String map) {
		switch (map) {
		case "map1": {
			return new Location(null, 0.5, 105, 0.5);
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + map);
		}
	}
}
