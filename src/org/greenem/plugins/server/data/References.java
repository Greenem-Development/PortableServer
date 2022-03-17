package org.greenem.plugins.server.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.plugin.java.JavaPlugin;
import org.greenem.plugins.server.game.Minigame;
import org.greenem.plugins.server.game.TeamManager;
import org.greenem.plugins.server.main.Main;

public class References {
	public static JavaPlugin plugin = Main.getPlugin(Main.class); // maybe to do in init
	public static ArrayList<String> playersInGame = new ArrayList<>();
	
	private static int lastTeamId = 1;
	public static Map<Integer, TeamManager> invitationIdToTeam = new HashMap<>();
	
	public static int addInvitationIdToTeam(TeamManager tm) {
		invitationIdToTeam.put(lastTeamId, tm);
		lastTeamId++;
		return lastTeamId-1;
	}
	
	public static Map<String, TeamManager> playersTeam = new HashMap<>();
	
	public static void updateAllMapListsInGui(Minigame minigame) {
		for (TeamManager tm : playersTeam.values()) {
			if(tm!=null && !tm.inGame && tm.selectedMinigame==minigame) { // what about checking when a bug occures and there is a null TeamManager
				tm.updateGuiMapList();
			}
		}
	}
}
