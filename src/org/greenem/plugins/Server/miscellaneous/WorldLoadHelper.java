package org.greenem.plugins.Server.miscellaneous;

import java.io.File;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.greenem.plugins.Server.util.SimpleLogger;

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
		File worldsDir = Bukkit.getWorldContainer();
		File theWorldFile = new File(worldsDir, name);
		return theWorldFile.exists() && theWorldFile.isDirectory(); // does isDirectory() always work fine?..
	}
	
	public static World loadWorld (String name) {
		World newWorld = Bukkit.createWorld(new WorldCreator(name));
		return newWorld;
//		if error return null;
	}
}
