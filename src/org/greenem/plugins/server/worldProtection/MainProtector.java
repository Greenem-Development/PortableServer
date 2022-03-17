package org.greenem.plugins.server.worldProtection;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.greenem.plugins.server.data.Locs;
import org.greenem.plugins.server.util.SimpleLogger;

public class MainProtector implements Listener {
	private float voidHeight = -20;
	
	@EventHandler
	public static void onBlockBreak(BlockBreakEvent e) {
		ProtectionType pt = ProtectionNames.getProtectionType(e.getBlock().getWorld().getName());
		if(pt==null) return;
		Player p = e.getPlayer();
		if(p.getGameMode()==GameMode.SURVIVAL || p.getGameMode()==GameMode.ADVENTURE) {
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
		if(p.getGameMode()==GameMode.SURVIVAL || p.getGameMode()==GameMode.ADVENTURE) {
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
        if(p.getGameMode()==GameMode.SURVIVAL) {
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
	
	@EventHandler
	public void PaintingArrow (HangingBreakByEntityEvent e) {
		checkPaintingRemovedByArrow(e);
		checkItemFrameDamage(e);
	}

	private void checkPaintingRemovedByArrow(HangingBreakByEntityEvent e){
		if(e.getEntity() instanceof Painting) {
			onPainingDamage(e);
		}
	}
	
	private void checkItemFrameDamage(HangingBreakByEntityEvent e){ // onItemFrameDamage
		if(e.getEntity() instanceof ItemFrame) {
			onItemFrameDamage(e);
		}
	}
	
	private void onPainingDamage(HangingBreakByEntityEvent e) {
		if(e.getRemover() instanceof Projectile) {
			if(((Projectile) e.getRemover()).getShooter() instanceof Player) {
				Player p = (Player) ((Projectile) e.getRemover()).getShooter();
				if(p.getGameMode()==GameMode.SURVIVAL || p.getGameMode()==GameMode.ADVENTURE) {
					cancelBreakingPaintingOrFrameByArrowFromPlayer(e, p.getWorld());
				}
			}
			else {
				cancelBreakingPaintingOrFrameByArrowFromMob(e, e.getEntity().getWorld());
			}
		}
	}
	
	private void cancelBreakingPaintingOrFrameByArrowFromPlayer(HangingBreakByEntityEvent e, World world) {
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
	
	private void cancelBreakingPaintingOrFrameByArrowFromMob(HangingBreakByEntityEvent e, World world) {
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
	
	private void onItemFrameDamage(HangingBreakByEntityEvent e) {
		if(e.getRemover() instanceof Player) {
			Player p = (Player) e.getRemover();
			if(p.getGameMode()==GameMode.SURVIVAL || p.getGameMode()==GameMode.ADVENTURE) {
				ProtectionType pt = ProtectionNames.getProtectionType(p.getWorld().getName());
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
	}
}
