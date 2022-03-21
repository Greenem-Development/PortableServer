package org.greenem.plugins.server.data;

import static org.greenem.plugins.server.util.SimpleLogger.log;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.YamlConfiguration;
import org.greenem.plugins.server.worldsVersionControl.BuildingServerOperations;

public class ConfigData {
	public static boolean allowGmCommandForOps = true;
	public static boolean allowHealCommandForOps = true;
	public static boolean allowInvitesBeforeReload = true;
	public static boolean allowInvitesNicknameMismatch = false;
	
	public static void logCastEx(String currentKey) {
		log("Wrong value type for \"" + currentKey + "\" in the config");
	}
	
	public static void loadConfig() {
		BuildingServerOperations.init();
		File f = new File(OutsideData.mainConfigDirectory, "mainconfig.yml");
		f.getParentFile().mkdirs();
		
		YamlConfiguration c = YamlConfiguration.loadConfiguration(f);
		YamlConfiguration cOld = YamlConfiguration.loadConfiguration(f);
		String currentKey = null;

		try {
			currentKey = "allow-gm-command-for-ops";
			if (c.get(currentKey) == null) {
				c.set(currentKey, true);
			} else {
				try {
					ConfigData.allowGmCommandForOps = (boolean) c.get(currentKey);
				} catch (ClassCastException e) {
					logCastEx(currentKey);
				}
			}
			currentKey = "allow-heal-command-for-ops";
			if (c.get(currentKey) == null) {
				c.set(currentKey, true);
			} else {
				try {
					ConfigData.allowHealCommandForOps = (boolean) c.get(currentKey);
				} catch (ClassCastException e) {
					logCastEx(currentKey);
				}
			}
			currentKey = "allow-invites-before-reload";
			if (c.get(currentKey) == null) {
				c.set(currentKey, true);
			} else {
				try {
					ConfigData.allowInvitesBeforeReload = (boolean) c.get(currentKey);
				} catch (ClassCastException e) {
					logCastEx(currentKey);
				}
			}
			currentKey = "allow-invites-nickname-mismatch";
			if (c.get(currentKey) == null) {
				c.set(currentKey, false);
			} else {
				try {
					ConfigData.allowInvitesNicknameMismatch = (boolean) c.get(currentKey);
				} catch (ClassCastException e) {
					logCastEx(currentKey);
				}
			}
		} catch (Throwable t) {
			log("Error while reading \"" + currentKey + "\" from the config");
		}
		
		// DONE
		
		boolean same = true;
	    for(String key : c.getKeys(false)) {
	        if(!cOld.contains(key)) {
	        	same = false;
	        }
	    }
	    
		if (!same) {
			try {
				c.save(f);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
