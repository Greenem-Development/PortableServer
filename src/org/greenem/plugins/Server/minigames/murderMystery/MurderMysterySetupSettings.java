package org.greenem.plugins.Server.minigames.murderMystery;

public class MurderMysterySetupSettings {
	public static int getArrayPosition(MurderMysterySettingsItem settings) {
		switch (settings) {
		case MODE:
			return 0;
		case MAP:
			return 1;
		case MURDERERS:
			return 2;
		case DETECTIVES:
			return 3;
		case MAX_TIME:
			return 4;
		default:
			throw new IllegalArgumentException("Unexpected value: " + settings);
		}
	}
	
	public static int getMaxTimeInt(String sentence) {
		switch (sentence) {
		case "5 Minutes":
			return 300;
		case "10 Minutes":
			return 600;
		case "2 Minutes":
			return 120;
		default:
			throw new IllegalArgumentException("Unexpected value: " + sentence);
		}
	}
	
	public static String[] getOptionsList(MurderMysterySettingsItem settings) {
		switch (settings) {
		case MODE:
			return new String[] {"Murder Mystery"};
		case MURDERERS:
			return new String[] {"1", "2", "3"};
		case DETECTIVES:
			return new String[] {"1", "2", "3"};
		case MAX_TIME:
			return new String[] {"5 Minutes", "10 Minutes", "2 Minutes"};
		default:
			throw new IllegalArgumentException("Unexpected value: " + settings);
		}
	}
}
