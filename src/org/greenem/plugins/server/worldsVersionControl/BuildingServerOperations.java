package org.greenem.plugins.server.worldsVersionControl;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystemException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.greenem.plugins.server.miscellaneous.WorldLoadHelper;

import static org.greenem.plugins.server.util.SimpleLogger.log;
import static org.greenem.plugins.server.util.SimpleLogger.debug;

public class BuildingServerOperations {
	public static File buildingServerLocation;
	private static File buildingServerConfigLocation;
	public static List<String> buildingServerWorlds;
	private static int folderTypeSensivity = 2;
	
	public static void init() {
		buildingServerConfigLocation = new File(buildingServerLocation, "configs/Building");
		loadWorldListFromConfig();
	}
	
	public static void loadWorldListFromConfig() {
		YamlConfiguration conf = new YamlConfiguration();
//		debug(buildingServerWorlds);
		try {
			conf.load(new File(buildingServerConfigLocation, "mainconfig.yml"));
		} catch (FileNotFoundException e) {
			buildingServerWorlds = new ArrayList<>();
			log(ChatColor.RED + "Couldn't find building server's config!");
			return;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
		
		buildingServerWorlds = conf.getStringList("building_worlds");
//		debug(buildingServerWorlds);
	}
	
	public static String replaceWorlds(String from, String to) {
		File sourceFolder = new File(buildingServerLocation, from);
		if(!sourceFolder.exists()) {
			return ChatColor.RED + "That world is whitelisted, but doesn't exist!";
		}
		if(from.contains(".") || to.contains(".")) {
			return ChatColor.RED + "That's not a folder!";
		}
		String sourceFolderString = null;
		try {
			sourceFolderString = sourceFolder.getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		boolean multipleFolders = WorldLoadHelper.worldExists(to + "_1");
		if(!multipleFolders) {
			return prepareReplaceWorld(sourceFolderString, to);
		}
		else {
			String s = "";
			boolean foundWorldsEnd = false; // does not work if like world1 doesn't exist, think about it later
			int replacingNumber = 1;
			while (!foundWorldsEnd) {
				String worldName = to + "_" + replacingNumber;
				if(WorldLoadHelper.worldExists(worldName)) {
					if(!s.equals("")) {
						s+= "\n";
					}
					s += prepareReplaceWorld(sourceFolderString, worldName);
				} else {
					foundWorldsEnd = true;
					break;
				}
				replacingNumber++;
			}
			if(!s.equals("")) {
				return s;
			}
		}
		return null;
	}
	
	public static String prepareReplaceWorld(String from, String to) {
		File fFrom = new File(from);
		File fTo = new File(to);
		if(fTo.exists()) {
			String[] files = fTo.list();
			if(files.length>0) { //TODO: don't create folder if was not there and something wrong or delete later and make sure it was still deleted with any errors
				int okWorldfolder = 0;
				for (int i = 0; i < files.length; i++) {
					String s = files[i];
					if(s.equals("level.dat")) okWorldfolder++;
//					if(s.equals("session.lock")) okWorldfolder++;
					if(s.equals("poi")) okWorldfolder++;	
				}
				if(okWorldfolder<folderTypeSensivity) {
					return ChatColor.RED + "That folder (" + to + ") doesn't seem like a world folder. If that's a mistake, "
							+ "check/delete the folder or change folder type sensitivity settings.";
				}
			}
		}
		else {
			fTo.mkdir(); // i think there is no need to check that the server folder exists
		}
		actualReplaceWorld(fFrom, fTo);
		return null;
	}
	
	public static void actualReplaceWorld(File from, File to) {
		try {
			FileUtils.deleteDirectory(to); // wanted to delete each folder separate to keep git repo in world folder, but repos should be in source folder
			to.mkdir();
			try {
				FileUtils.copyDirectory(from, to, new WorldCopyFileFilter());
			} catch(FileSystemException e) {
				e.printStackTrace();
				// NOT ignore, it's most likely session.lock (ALREADY NOT)
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public static void test() {
//		File sourceFile = new File(buildingServerLocation, "testworld_1");
//		File to = new File(Bukkit.getWorldContainer(), "testfiles");
//		to.mkdir();
//		
//		try {
//			FileUtils.deleteDirectory(to);
//			FileUtils.copyDirectory(sourceFile, to);
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
	}
}
