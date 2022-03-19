package org.greenem.plugins.server.minigames.murderMystery;

import org.bukkit.Location;
import org.bukkit.entity.Item;

public class SpawnedGold {
	public Item item;
	public Location location;
	public int number;
	
	@Override
	public String toString() {
//		return "SpawnedGold [item=" + item + ", location=" + location + ", number=" + number + "]\n";
		return "SpawnedGold [" + number + "]\n";
	}
}
