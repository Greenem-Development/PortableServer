package org.greenem.plugins.server.worldProtection;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class PlaceDependentEvents implements Listener {
	@EventHandler
	public static void onBlockPlace(FoodLevelChangeEvent e) {
		ProtectionType pt = ProtectionNames.getProtectionType(e.getEntity().getWorld().getName()); // can be done here, it's more for players lol
		if(pt==null) return;
		if(e.getEntity() instanceof Player) { // what if it's not? Is that possible?
			switch (pt) {
				case HUB: {
					usualCancelFoodLevelChangeEvent(e);
					break;
				}
				case MURDER_MUSTERY_GAME: {
					usualCancelFoodLevelChangeEvent(e);
					break;
				}
			}
		}
	}
	
	private static void usualCancelFoodLevelChangeEvent(FoodLevelChangeEvent e) {
		e.setCancelled(true);
	}
}
