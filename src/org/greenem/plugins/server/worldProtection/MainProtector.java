package org.greenem.plugins.server.worldProtection;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.greenem.plugins.server.data.Locs;
import org.greenem.plugins.server.miscellaneous.Stash;

import static org.greenem.plugins.server.util.SimpleLogger.log;
import static org.greenem.plugins.server.util.SimpleLogger.debug;

public class MainProtector implements Listener {
	private float voidHeight = -20;
	
	@EventHandler
	public static void onBlockBreak(BlockBreakEvent e) {
		ProtectionType pt = ProtectionNames.getProtectionType(e.getBlock().getWorld().getName());
		if(pt==null) return;
		Player p = e.getPlayer();
		if(Stash.isInSurvivalOrAdv(p)) {
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
	}
	
	private static void usualCancelBlockBreak(BlockBreakEvent e) { // what about already canseled ones, meybe there is need to ckeck that
		e.setCancelled(true);
	}
	
	@EventHandler
	public static void onBlockPlace(BlockPlaceEvent e) {
		ProtectionType pt = ProtectionNames.getProtectionType(e.getBlock().getWorld().getName());
		if(pt==null) return;
		Player p = e.getPlayer();
		if(Stash.isInSurvivalOrAdv(p)) {
			switch (pt) {
				case HUB: {
					usualCancelBlockPlace(e);
					break;
				}
				case MURDER_MUSTERY_GAME: {
					usualCancelBlockPlace(e);
					break;
				}
			}
		}
	}
	
	private static void usualCancelBlockPlace(BlockPlaceEvent e) {
		e.setCancelled(true);
	}
	
	@EventHandler()
	public void PlayerInteractEvent(PlayerInteractEvent e){
		Player p = e.getPlayer();
		Block b = e.getClickedBlock();
		ProtectionType pt = ProtectionNames.getProtectionType(e.getPlayer().getWorld().getName());
		if(pt==null) return;
        if(Stash.isInSurvivalOrAdv(p)) {
        	if(e.getClickedBlock() != null) {
        		if(e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
        			if(isAPot(b.getType())) {
        				switch (pt) {
	        				case HUB: {
	        					usualCancelPotPlayerInteractEvent(e);
	        					break;
	        				}
	        				case MURDER_MUSTERY_GAME: {
	        					usualCancelPotPlayerInteractEvent(e);
	        					break;
	        				}		
        				}
        			}
        			if(isATrapdoor(b.getType())) {
        				switch (pt) {
	        				case HUB: {
	        					usualCancelTrapdoorPlayerInteractEvent(e);
	        					break;
	        				}
	        				case MURDER_MUSTERY_GAME: {
	        					usualCancelTrapdoorPlayerInteractEvent(e);
	        					break;
	        				}		
        				}
        			}
        		}
        	}
        }
	}
	
	private static void usualCancelPotPlayerInteractEvent(PlayerInteractEvent e) {
		e.setCancelled(true);		
	}
	
	private static void usualCancelTrapdoorPlayerInteractEvent(PlayerInteractEvent e) {
		e.setCancelled(true);
	}
	
	private static boolean isAPot(Material m) {
		if(m==null) return false;
		if(m==Material.FLOWER_POT) {
			return true;
		}
		String[] parts = m.toString().split("_"); // could be done with contains
		if(parts[0].equals("POTTED")) {
			return true;
		}
		return false;
	}
	
	private static boolean isATrapdoor(Material m) {
		if(m==null) return false;
		return m.toString().contains("TRAPDOOR");
	}
	
	@EventHandler
	public void DamageRulesManager(EntityDamageEvent e) {
		ProtectionType pt = ProtectionNames.getProtectionType(e.getEntity().getWorld().getName());
		if(pt==null) return;
		if (e.getEntity() instanceof Player) { // what if I need not a player
			if(e.getCause()!=DamageCause.VOID) { // may be moved or removed later
				switch (pt) {
					case HUB: {
						e.setCancelled(true); // not a special method for this because damage event here is kinda weird, it's controlled from game
						break;
					}
					default:
						break;
				}
			}
		}
	}
	
	@EventHandler
	public void antiVoid(PlayerMoveEvent e) {
		if(e.getPlayer().getLocation().getY() < voidHeight) {
			ProtectionType pt = ProtectionNames.getProtectionType(e.getPlayer().getWorld().getName());
			if(pt==null) return;
			switch (pt) {
				case HUB: {
					e.getPlayer().teleport(Locs.mainWorldSpawn);
					break;
				}
				default:
					break;
			}
		}
	}
	
	private void cancelPaintingPlayerDamage(HangingBreakByEntityEvent e, World w){
		ProtectionType pt = ProtectionNames.getProtectionType(w.getName());
		if(pt==null) return;
		switch (pt) {
			case HUB: {
				e.setCancelled(true);
				break;
			}
			case MURDER_MUSTERY_GAME: {
				e.setCancelled(true);
				break;
			}
		}
	}
	
	private void cancelBreakingPaintingByArrowFromPlayer(HangingBreakByEntityEvent e, World world) {
		ProtectionType pt = ProtectionNames.getProtectionType(world.getName());
		if(pt==null) return;
		switch (pt) {
			case HUB: {
				e.setCancelled(true);
				break;
			}
			case MURDER_MUSTERY_GAME: {
				e.setCancelled(true);
				break;
			}
		}
	}
	
	private void cancelBreakingPaintingByArrowFromMob(HangingBreakByEntityEvent e, World world) {
		ProtectionType pt = ProtectionNames.getProtectionType(world.getName());
		if(pt==null) return;
		switch (pt) {
			case HUB: {
				e.setCancelled(true);
				break;
			}
			case MURDER_MUSTERY_GAME: {
				e.setCancelled(true);
				break;
			}
		}
	}
	
	private void cancelBreakingItemFrameByArrowFromMob(HangingBreakByEntityEvent e, World world) {
		ProtectionType pt = ProtectionNames.getProtectionType(world.getName());
		if(pt==null) return;
		switch (pt) {
			case HUB: {
				e.setCancelled(true);
				break;
			}
			case MURDER_MUSTERY_GAME: {
				e.setCancelled(true);
				break;
			}
		}
	}
	
	private void cancelBreakingItemFrameByArrowFromPlayer(HangingBreakByEntityEvent e, World world) {
		ProtectionType pt = ProtectionNames.getProtectionType(world.getName());
		if(pt==null) return;
		switch (pt) {
			case HUB: {
				e.setCancelled(true);
				break;
			}
			case MURDER_MUSTERY_GAME: {
				e.setCancelled(true);
				break;
			}
		}
	}
	
	private void cancelItemFramePlayerDamage(HangingBreakByEntityEvent e, World w){
		ProtectionType pt = ProtectionNames.getProtectionType(w.getName());
		if(pt==null) return;
		switch (pt) {
			case HUB: {
				e.setCancelled(true);
				break;
			}
			case MURDER_MUSTERY_GAME: {
				e.setCancelled(true);
				break;
			}
		}
	}
	
	@EventHandler
	public void onArmorstandManipulation(PlayerArmorStandManipulateEvent e) {
		if(Stash.isInSurvivalOrAdv(e.getPlayer())) {
			ProtectionType pt = ProtectionNames.getProtectionType(e.getPlayer().getWorld().getName());
			if(pt==null) return;
			switch (pt) {
				case HUB: {
					e.setCancelled(true);
					break;
				}
				case MURDER_MUSTERY_GAME: {
					e.setCancelled(true);
					break;
				}
			}
		}
	}
	
	private void checkArmorStandDamage(EntityDamageByEntityEvent e) {
		if(e.getEntity() instanceof ArmorStand) {
			if(e.getDamager() instanceof Player) {
				Player p = (Player) e.getDamager();
				if(Stash.isInSurvivalOrAdv(p)) {
					ProtectionType pt = ProtectionNames.getProtectionType(e.getEntity().getWorld().getName());
					if(pt==null) return;
					switch (pt) {
						case HUB: {
							e.setCancelled(true);
							break;
						}
						default:
							break;
					}
				}
			}
		}
	}	
	
	// System stuff --------------------------------------------------------------------------------------------------------------------------------------
	
	@EventHandler
	public void preventPaintingOrItemFrameDamage(HangingBreakByEntityEvent e) {
		checkPaintingDamage(e);
		checkItemFrameDamage(e);
	}	

	private boolean checkPaintingDamage(HangingBreakByEntityEvent e){
		if(e.getEntity() instanceof Painting) {
			onPainingDamage(e);
			return true;
		}
		return false;
	}
	
	private boolean checkItemFrameDamage(HangingBreakByEntityEvent e){ // onItemFrameDamage
		if(e.getEntity() instanceof ItemFrame) {
			onItemFrameDamage(e);
			return true;
		}
		return false;
	}
	
	private void onPainingDamage(HangingBreakByEntityEvent e) {
		if(e.getRemover() instanceof Player) {
			Player p = (Player) e.getRemover();
			if(Stash.isInSurvivalOrAdv(p) || Stash.hasItemInHand(p, Material.IRON_SWORD)
					 || Stash.hasItemInHand(p, Material.DIAMOND_SWORD)) {
				cancelPaintingPlayerDamage(e, p.getWorld());
			}
		}
		else if(e.getRemover() instanceof Projectile) {
			if(((Projectile) e.getRemover()).getShooter() instanceof Player) {
				Player p = (Player) ((Projectile) e.getRemover()).getShooter();
				if(Stash.isInSurvivalOrAdv(p)) {
					cancelBreakingPaintingByArrowFromPlayer(e, p.getWorld());
				}
			}
			else {
				cancelBreakingPaintingByArrowFromMob(e, e.getEntity().getWorld());
			}
		}
	}
	
	private void onItemFrameDamage(HangingBreakByEntityEvent e) {
		if(e.getRemover() instanceof Player) {
			Player p = (Player) e.getRemover();
			if(Stash.isInSurvivalOrAdv(p) || Stash.hasItemInHand(p, Material.IRON_SWORD)
					 || Stash.hasItemInHand(p, Material.DIAMOND_SWORD)) {
				cancelItemFramePlayerDamage(e, p.getWorld());
			}
		}
		else if (e.getRemover() instanceof Projectile) {
			if(((Projectile) e.getRemover()).getShooter() instanceof Player) {
				Player p = (Player) ((Projectile) e.getRemover()).getShooter();
				if(Stash.isInSurvivalOrAdv(p)) {
					cancelBreakingItemFrameByArrowFromPlayer(e, p.getWorld());
				}
			}
			else {
				cancelBreakingItemFrameByArrowFromMob(e, e.getEntity().getWorld());
			}
		}
	}
	
	@EventHandler
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent e){
		checkArmorStandDamage(e);
	}
}
