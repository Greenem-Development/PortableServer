package org.greenem.plugins.Server.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.greenem.plugins.Server.game.PartyManager;

public class References {
	public static ArrayList<String> playersInGame = new ArrayList<>();
	
	private static int lastPartyId = 1;
	public static Map<Integer, PartyManager> invitationIdToParty = new HashMap<>();
	
	public static int addInvitationIdToParty(PartyManager pm) {
		invitationIdToParty.put(lastPartyId, pm);
		lastPartyId++;
		return lastPartyId-1;
	}
	
	public static Map<String, PartyManager> playersParty = new HashMap<>();
}
