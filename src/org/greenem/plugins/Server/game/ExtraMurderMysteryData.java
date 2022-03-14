package org.greenem.plugins.Server.game;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.greenem.plugins.Server.minigames.murderMystery.MurderMysterySpawnLocations;
import org.greenem.plugins.Server.util.SimpleLogger;

public class ExtraMurderMysteryData implements ExtraGameData {
	public GameManager gm;
	public int fullTime = -1;
	public int timePast = 0;
	public int amountOfMurderers = 1;
	public int amountOfDetectives = 1;
	
	public ArrayList<String> murderers = new ArrayList<>();
	public ArrayList<String> detectives = new ArrayList<>();
	public ArrayList<String> innocents = new ArrayList<>();
	public ArrayList<String> heroes = new ArrayList<>();
	
	@Override
	public void start() {
		ArrayList<Location> allLocs = MurderMysterySpawnLocations.locs.get(gm.map);
		for (String pName : gm.actualPlayers) {
			int spawnLocNum = gm.rand.nextInt(allLocs.size());
			float rotation = gm.rand.nextFloat(-180, 180);
			SimpleLogger.log(gm.map);
			Location loc = allLocs.get(spawnLocNum).clone().add(0.5, 0, 0.5);
			loc.setYaw(rotation);
			loc.setWorld(Bukkit.getWorld(gm.worldName));
			Bukkit.getPlayerExact(pName).teleport(loc);
		}
	}
	
	@Override
	public GameManager getGm() {
		return gm;
	}
	
	@Override
	public void setGm(GameManager gm) {
		this.gm = gm;
	}
}
