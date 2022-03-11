package org.greenem.plugins.Server.game;

import java.util.ArrayList;

public class GameManager {
	public Minigame minigameType = null;
	public ArrayList<String> players = new ArrayList<>();
	public ArrayList<String> actualPlayers = new ArrayList<>();
	
	public void start() {
		prepareWorld();
		for (String s : players) {
			actualPlayers.add(s);
		}
		for (String s : actualPlayers) {
			
		}
	}
	
	public void stop() {
		
	}
	
	public void prepareWorld() {
		switch (minigameType) {
			case MURDER_MUSTERY: {
				
			}
			case AMONG_US: {
				
			}
			case SKYWARS: {
				
			}
			case BEDWARS: {
				
			}
			case TNT_RUN: {
				
			}
		default:
			throw new IllegalArgumentException("Unexpected value in prepareWorld: " + minigameType);
		}
	}
}
