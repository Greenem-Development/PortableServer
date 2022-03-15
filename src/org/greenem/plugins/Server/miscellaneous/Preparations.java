package org.greenem.plugins.Server.miscellaneous;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.greenem.plugins.Server.data.NamesAndLines;
import org.greenem.plugins.Server.game.Minigame;

public class Preparations {
	public static void preparePlayerInHub(Player p) {
		turnVanishOff(p);
		heal(p);
		removeAllMaterialsAfterDeath(p, null);
		Stash.addItemStack(p, Stash.createGuiItem(Material.NETHER_STAR, NamesAndLines.gameMenuItemName), 0);
	}
	
	public static void preparePlayerForMinigame(Player p, Minigame m, boolean fromSpawn) {
		Preparations.turnVanishOff(p);
		heal(p);
		removeAllMaterialsAfterDeath(p, m);
	}
	
	public static void removeAllMaterialsAfterDeath(Player p, Minigame m) {
		if(m==null) { // make the list better
			Stash.removeMaterial(p, Material.NETHER_STAR); // , NamesAndLines.gameMenuItemName
			Stash.removeMaterial(p, Material.BOW);
			Stash.removeMaterial(p, Material.IRON_SWORD);
			Stash.removeMaterial(p, Material.ARROW);
			Stash.removeMaterial(p, Material.SPECTRAL_ARROW);
			Stash.removeMaterial(p, Material.COMPASS);
		}
		else {
			switch (m) {
			case MURDER_MYSTERY: {
				Stash.removeMaterial(p, Material.NETHER_STAR); // , NamesAndLines.gameMenuItemName
				Stash.removeMaterial(p, Material.BOW);
				Stash.removeMaterial(p, Material.IRON_SWORD);
				Stash.removeMaterial(p, Material.ARROW);
				Stash.removeMaterial(p, Material.SPECTRAL_ARROW);
				Stash.removeMaterial(p, Material.COMPASS);
				break;
			}
			default:
				throw new IllegalArgumentException("Unexpected value: " + m);
			}
		}
	}
	
	public static void turnVanishOn(Player p) {
		p.setAllowFlight(true);
		p.setFlying(true);
		for (Player p1 : Bukkit.getOnlinePlayers()) {
            p1.hidePlayer(p);
		}
		p.addPotionEffect(PotionEffectType.INVISIBILITY.createEffect(99999, 1));
        p.setCanPickupItems(false);
	}
	
	public static void turnVanishOff(Player p) {
		if(p.getGameMode()==GameMode.SURVIVAL || p.getGameMode()==GameMode.ADVENTURE) {
			p.setAllowFlight(false);
			p.setFlying(false);
		}
		for (Player p1 : Bukkit.getOnlinePlayers()) {
            p1.showPlayer(p);
		}
		p.removePotionEffect(PotionEffectType.INVISIBILITY);
        p.setCanPickupItems(true);
	}
	
	public static void heal(Player p) {
		p.setHealth(20);
		p.setFoodLevel(20);
	}
}
