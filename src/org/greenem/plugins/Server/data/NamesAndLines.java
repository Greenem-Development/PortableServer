package org.greenem.plugins.Server.data;

import org.bukkit.ChatColor;
import org.greenem.plugins.Server.game.Minigame;

public class NamesAndLines {
	public static String HubName = "hub";
	public static String gameMenuItemName = "Game Menu";
	public static String closeGuiText = "Close";
	public static String noMapsAvaliable = ChatColor.RED + "There is currently no world with that map avaliable to start the game!";
	public static String MmMessageKilledByUnknownPlayer = "You were killed by a player";
	public static String goldIngotName = "Gold";
	
	public static String getMinigameNameInFolder(Minigame minigame) {
		switch (minigame) {
		case MURDER_MYSTERY: {
			return "murder_mystery";
		}
		case AMONG_US: {
			return "among_us";
		}
		case BEDWARS: {
			return "bedwars";
		}
		case SKYWARS: {
			return "skywars";
		}
		case TNT_RUN: {
			return "tnt_run";
		}
		case TAG: {
			return "tag";
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + minigame);
		}
	}
	
	public static String getMinigameNameNormal(Minigame minigame) {
		switch (minigame) {
		case MURDER_MYSTERY: {
			return "Murder Mystery";
		}
		case AMONG_US: {
			return "Among Us";
		}
		case BEDWARS: {
			return "Bedwars";
		}
		case SKYWARS: {
			return "Skywars";
		}
		case TNT_RUN: {
			return "Tnt Run";
		}
		case TAG: {
			return "Tag";
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + minigame);
		}
	}
}
