package org.greenem.plugins.Server.miscellaneous;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.greenem.plugins.Server.data.NamesAndLines;
import org.greenem.plugins.Server.game.Minigame;
import org.greenem.plugins.Server.game.TeamManager;

public class Preparations {
	public static void preparePlayerInHub(Player p) {
		turnVanishOff(p);
		Stash.addItemStack(p, Stash.createGuiItem(Material.NETHER_STAR, NamesAndLines.gameMenuItemName), 0);
	}
	
	public static void preparePlayerForMinigame(Player p, Minigame m, boolean fromSpawn) {
		Preparations.turnVanishOff(p);
		removeAllMaterialsAfterDeath(p, m);
	}
	
	public static void removeAllMaterialsAfterDeath(Player p, Minigame m) {
		removeAllMaterialsAfterDeath(p, m, false);
	}
	
	public static void removeAllMaterialsAfterDeath(Player p, Minigame m, boolean thereWasNoDeath) {
		if(thereWasNoDeath) {
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
}
