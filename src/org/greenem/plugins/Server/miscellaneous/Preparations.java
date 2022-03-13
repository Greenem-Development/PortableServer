package org.greenem.plugins.Server.miscellaneous;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.greenem.plugins.Server.data.NamesAndLines;
import org.greenem.plugins.Server.game.PartyManager;

public class Preparations {
	public static void preparePlayerInHub(Player p) {
		Stash.AddItemStack(p, Stash.createGuiItem(Material.NETHER_STAR, NamesAndLines.gameMenuItemName), 0);
	}
}
