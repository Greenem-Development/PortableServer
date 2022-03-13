package org.greenem.plugins.Server.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.greenem.plugins.Server.game.Minigame;

public class MinigamesStaticData {
	public static Map<Minigame, String> defaultMap = new HashMap<>();
	public static Map<Minigame, ArrayList<String>> minigameMaps = new HashMap<>();
	public static Map<Minigame, Integer> minimumPlayersForGame = new HashMap<>();
	
	public static void init() {
		initDefaults();
		initMinigameMaps();
		initMinimumPlayersForGame();
	}
	
	private static void initDefaults() {
		defaultMap.put(Minigame.MURDER_MYSTERY, "map1");		
	}
	
	private static void initMinigameMaps() {
		ArrayList<String> murderMysteryMaps = new ArrayList<>();
		murderMysteryMaps.add("map1");
		minigameMaps.put(Minigame.MURDER_MYSTERY, murderMysteryMaps);
		// and others
	}
	
	public static String getFullMapName(Minigame minigame, String map) {
		switch (minigame) {
		case MURDER_MYSTERY: {
			switch (map) {
			case "default": {
				return "Main";
			}
			default:
				throw new IllegalArgumentException("Unexpected value in getFullMapName#map: " + map);
			}
		}
		default:
			throw new IllegalArgumentException("Unexpected value in getFullMapName#minigame: " + minigame);
		}
	}
	
	private static void initMinimumPlayersForGame() {
		minimumPlayersForGame.put(Minigame.MURDER_MYSTERY, 2); //3
	}
}
