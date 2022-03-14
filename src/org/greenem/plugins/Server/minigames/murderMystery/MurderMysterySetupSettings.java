package org.greenem.plugins.Server.minigames.murderMystery;

public class MurderMysterySetupSettings {
	public int getArrayPosition(MurderMysterySettingsItem settings) {
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
	
	public int getMaxTimeInt(String sentence) {
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
	
	public String[] getOptionsList(MurderMysterySettingsItem settings) {
		
	}
}
