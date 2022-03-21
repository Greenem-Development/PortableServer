package org.greenem.plugins.server.miscellaneous;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.greenem.plugins.server.data.NamesAndLines;
import org.greenem.plugins.server.game.Minigame;

public class Preparations {
	public static void preparePlayerInHub(Player p) {
		turnVanishOff(p);
		heal(p);
		removeFromHideNicknamesTeam(p);
		removeAllMaterialsAfterDeath(p, null);
		Stash.addItemStack(p, Stash.createGuiItem(Material.NETHER_STAR, NamesAndLines.gameMenuItemName), 0);
	}
	
	public static void preparePlayerForMinigame(Player p, Minigame m, boolean fromSpawn) {
		p.setGameMode(GameMode.SURVIVAL); // customise later
		Preparations.turnVanishOff(p);
		heal(p);
		removeAllMaterialsAfterDeath(p, m);
	}
	
	public static void removeAllMaterialsAfterDeath(Player p, Minigame m) {
		if(m==null) { // make the list better
			basicRemoveItems(p);
		}
		else {
			switch (m) {
			case MURDER_MYSTERY: {
				basicRemoveItems(p);
				break;
			}
			default:
				throw new IllegalArgumentException("Unexpected value: " + m);
			}
		}
	}
	
	private static void basicRemoveItems(Player p) {
		Stash.removeMaterial(p, Material.NETHER_STAR); // , NamesAndLines.gameMenuItemName
		Stash.removeMaterial(p, Material.BOW);
		Stash.removeMaterial(p, Material.IRON_SWORD);
		Stash.removeMaterial(p, Material.ARROW);
		Stash.removeMaterial(p, Material.SPECTRAL_ARROW);
		Stash.removeMaterial(p, Material.COMPASS);
		Stash.removeMaterial(p, Material.GOLD_INGOT);
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
	
	public static void removeFromHideNicknamesTeam(Player p) {
		removeFromTeam(p, NamesAndLines.hideNicknamesTeamName);
	}
	
	public static void removeFromTeam(Player p, String team) {
		Scoreboard mainScoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
		Team t = mainScoreboard.getTeam(team);
		if(t!=null) {
			t.removeEntry(p.getName());
		}
	}
}
