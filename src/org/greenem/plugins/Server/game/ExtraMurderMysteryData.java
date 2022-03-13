package org.greenem.plugins.Server.game;

import java.util.ArrayList;

public class ExtraMurderMysteryData implements ExtraGameData {
	public int fullTime = -1;
	public int timePast = 0;
	public int amountOfMurderers = 1;
	public int amountOfDetectives = 1;
	
	public ArrayList<String> murderers = new ArrayList<>();
	public ArrayList<String> detectives = new ArrayList<>();
	public ArrayList<String> innocents = new ArrayList<>();
	public ArrayList<String> heroes = new ArrayList<>();
}
