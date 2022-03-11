package miscellaneous;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

public class WorldLoadHelper {
	public static boolean worldLoaded(String name) {
		List<World> worlds = Bukkit.getWorlds();
		for (World w : worlds) {
			if(w.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean worldExists(String name) {
		return true;
	}
	
	public static World loadWorld (String name) {
		World newWorld = Bukkit.createWorld(new WorldCreator(name));
		return newWorld;
//		if error return null;
	}
}
