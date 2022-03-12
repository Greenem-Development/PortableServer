package minigames.murderMystery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class MurderMysterySpawnLocations {
	public static Map<String, ArrayList<Location>> locs = new HashMap<>();
	
	public static void init() {
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
		locs.put("map1", current);
	}
}
