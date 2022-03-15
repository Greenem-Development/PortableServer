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
	
	public static int getMaxTimeInt(int id) {
		switch (id) {
		case 0:
			return 300;
		case 1:
			return 600;
		case 2:
			return 120;
		default:
			throw new IllegalArgumentException("Unexpected value: " + id);
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
	
	public static int getAmountOfMurderers(int pos) {
		switch (pos) {
		case 0: { // first item in gui list, id=0, but says 1 murderer
			return 1;
		}
		case 1: {
			return 2;
		}
		case 2: {
			return 3;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + pos);
		}
	}
	
	public static int getAmountOfDetectives(int pos) {
		switch (pos) {
		case 0: { // first item in gui list, id=0, but says 1 detective
			return 1;
		}
		case 1: {
			return 2;
		}
		case 2: {
			return 3;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + pos);
		}
	}
}
