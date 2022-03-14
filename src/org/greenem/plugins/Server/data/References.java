package org.greenem.plugins.Server.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.plugin.java.JavaPlugin;
import org.greenem.plugins.Server.game.Minigame;
import org.greenem.plugins.Server.game.PartyManager;
import org.greenem.plugins.Server.main.Main;

public class References {
	public static JavaPlugin plugin = Main.getPlugin(Main.class); // maybe to do in init
	public static ArrayList<String> playersInGame = new ArrayList<>();
	
	private static int lastPartyId = 1;
	public static Map<Integer, PartyManager> invitationIdToParty = new HashMap<>();
	
	public static int addInvitationIdToParty(PartyManager pm) {
		invitationIdToParty.put(lastPartyId, pm);
		lastPartyId++;
		return lastPartyId-1;
	}
	
	public static Map<String, PartyManager> playersParty = new HashMap<>();
	
	public static void updateAllMapListsInGui(Minigame minigame) {
		for (PartyManager pm : playersParty.values()) {
			if(pm!=null && !pm.inGame && pm.selectedMinigame==minigame) { // what about checking when a bug occures and there is a null PartyManager
				pm.updateGuiMapList();
			}
		}
	}
}
