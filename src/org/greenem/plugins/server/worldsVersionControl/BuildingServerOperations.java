package org.greenem.plugins.server.worldsVersionControl;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import static org.greenem.plugins.server.util.SimpleLogger.log;
import static org.greenem.plugins.server.util.SimpleLogger.debug;

public class BuildingServerOperations {
	public static File buildingServerLocation;
	private static File buildingServerConfigLocation;
	
	public static void init() {
		buildingServerConfigLocation = new File(buildingServerLocation, "cofigs/Building");
	}
	
	public static void test() {
		File sourceFile = new File(buildingServerLocation, "testworld_1");
		File to = new File(Bukkit.getWorldContainer(), "testfilesxxx");
		to.mkdir();
		
		try {
			FileUtils.deleteDirectory(to);
			FileUtils.copyDirectory(sourceFile, to);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
