package org.greenem.plugins.server.worldProtection;

public class ProtectionNames {
	public static ProtectionType getProtectionType(String worldName) {
		ProtectionType protectionType = null;
		String mapName = null;
		String currentName = null;
		if(worldName.contains("_")) {
			currentName = "murder_mystery";
			if(worldName.startsWith(currentName)) {
				protectionType = ProtectionType.MURDER_MUSTERY_GAME;
				mapName = worldName.substring(currentName.length()+1).split("_")[0];
				// maybe chenk more variants due to different maps (if needed new, create new type for that map or similar)
				return protectionType;
			}
		}
		currentName = "hub";
		if(worldName.startsWith(currentName)) { // or equals
			protectionType = ProtectionType.HUB;
			return protectionType;
		}
		return null;
	}
}
