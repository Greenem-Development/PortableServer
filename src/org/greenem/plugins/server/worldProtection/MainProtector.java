package org.greenem.plugins.server.worldProtection;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class MainProtector implements Listener {
	@EventHandler
	public static void onBlockBreak(BlockBreakEvent e) {
		ProtectionType pt = ProtectionNames.getProtectionType(e.getBlock().getWorld().getName());
		if(pt==null) {
			return;
		}	
		switch (pt) {
		case HUB: {
			usualCancelBlockBreak(e);
			break;
		}
		case MURDER_MUSTERY_GAME: {
			usualCancelBlockBreak(e);
			break;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + pt);
		}
	}
	
	private static void usualCancelBlockBreak(BlockBreakEvent e) { // what about already canseled ones, meybe there is need to ckeck that
		e.setCancelled(true);
	}
	
	@EventHandler
	public static void onBlockPlace(BlockPlaceEvent e) {
		ProtectionType pt = ProtectionNames.getProtectionType(e.getBlock().getWorld().getName());
		if(pt==null) {
			return;
		}	
		switch (pt) {
		case HUB: {
			usualCancelBlockPlace(e);
			break;
		}
		case MURDER_MUSTERY_GAME: {
			usualCancelBlockPlace(e);
			break;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + pt);
		}
	}
	
	private static void usualCancelBlockPlace(BlockPlaceEvent e) {
		e.setCancelled(true);
	}
}
