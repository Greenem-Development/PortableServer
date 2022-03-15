package org.greenem.plugins.Server.minigames.murderMystery;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import org.greenem.plugins.Server.data.MinigamesStaticData;
import org.greenem.plugins.Server.data.NamesAndLines;
import org.greenem.plugins.Server.data.References;
import org.greenem.plugins.Server.game.ExtraGameData;
import org.greenem.plugins.Server.game.GameManager;
import org.greenem.plugins.Server.miscellaneous.Preparations;
import org.greenem.plugins.Server.miscellaneous.Stash;
import org.greenem.plugins.Server.util.SimpleLogger;

public class ExtraMurderMysteryData implements ExtraGameData {
	public GameManager gm;
	
	ArrayList<String> actualPlayers = new ArrayList<>();
	
	Random rand = new Random();
	public int fullTime = -1;
	public int timePast = 0;
	public int amountOfMurderers = 1;
	public int amountOfDetectives = 1;
	
	public ArrayList<String> murderers = new ArrayList<>();
	public ArrayList<String> detectives = new ArrayList<>();
	public ArrayList<String> innocents = new ArrayList<>();
	public ArrayList<String> heroes = new ArrayList<>();
	public ArrayList<String> savedMurderers = new ArrayList<>();
	public World world;
	public boolean gameFinished = false;
	public ArrayList<ArmorStand> droppedBows = new ArrayList<>();
	public int distanceToBowToPickUp = 1;
	public BukkitTask repeatingTask;
	int goldCurrentlySpawned = 0; // can spawn twice in one place, but its's currently a feature
	public int murderMysteryMaxGoldSpawned = 15;
	public int bowDoesWhatPartOfMurderersHp = 1; // 1 - full, 2 - half
	public int swordDoesWhatPartOfMurderersHp = 2;
	public int goldSpawnChancePerSecond = 3; // 1 - 50% chance, 2 - 33% chance
	public int goldSkipSeconds = 1;
	private int skipTimer = 0;
	
	@Override
	public String start() {
		if(amountOfMurderers+amountOfDetectives > gm.players.size()) {
			return ChatColor.RED + "You don't have enough players for " + amountOfMurderers + " murderers and " + amountOfDetectives + " detectives!";
		}
		for (String s : gm.players) {
			actualPlayers.add(s);
		}
		for (String s : actualPlayers) {
			if(Bukkit.getPlayerExact(s)!=null) {
				Preparations.preparePlayerForMinigame(Bukkit.getPlayerExact(s), gm.minigameType, true);
			}
		}
		teleportEveryone();
		randomizeRoles();
		announceRoles();
		doOtherStuff();
		ballanceMechanics();
		startRepeatingRunnables();
		return null;
	}
	
	private void teleportEveryone() {
		ArrayList<Location> allLocs = MurderMysteryLocations.gameStartSpawnLocations.get(gm.map);
		for (String pName : actualPlayers) {
			if(Bukkit.getPlayerExact(pName)!=null) {
				int spawnLocNum = rand.nextInt(allLocs.size());
				float rotation = rand.nextFloat(-180, 180);
				Location loc = allLocs.get(spawnLocNum).clone().add(0.5, 0, 0.5);
				loc.setYaw(rotation);
				world = Bukkit.getWorld(gm.worldName);
				loc.setWorld(world);
				Bukkit.getPlayerExact(pName).teleport(loc);
				loc.getWorld().setTime(MinigamesStaticData.getTimeForMap(gm.minigameType, gm.map));
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void randomizeRoles() {
		ArrayList<String> playersNeedRole = (ArrayList<String>) actualPlayers.clone();
		for(int i = 0; i < amountOfMurderers; i++) {
			if(playersNeedRole.size() > 0) {
				int num = rand.nextInt(playersNeedRole.size());
				SimpleLogger.debug("Murderer " + num);
				murderers.add(playersNeedRole.get(num));
				playersNeedRole.remove(num);
			}
		}
		for(int i = 0; i < amountOfDetectives; i++) {
			if(playersNeedRole.size() > 0) {
				int num = rand.nextInt(playersNeedRole.size());
				SimpleLogger.debug("Detective " + num);
				detectives.add(playersNeedRole.get(num));
				playersNeedRole.remove(num);
			}
		}
		for(int i = 0; i < playersNeedRole.size(); i++) {
			if(playersNeedRole.size() > 0) { //maybe no innocents already
				SimpleLogger.debug("Innocent ");
				innocents.add(playersNeedRole.get(i));
				playersNeedRole.remove(i);
			}
		}
		savedMurderers = (ArrayList<String>) murderers.clone();
	}
	
	private void announceRoles() {
		for (String s : actualPlayers) {
			Player p = Bukkit.getPlayerExact(s);
			if(p!=null) {
				if(murderers.contains(p.getName())) {
					p.sendTitle(ChatColor.GRAY + "" + ChatColor.BOLD + "" + ChatColor.ITALIC + "You are a " + ChatColor.RED + "MURDERER", "", 5, 30, 10);
					p.getInventory().setHeldItemSlot(4);
					Stash.addItemStack(p, new ItemStack(Material.IRON_SWORD, 1), 0);
				}
				else if(detectives.contains(p.getName())) {
					p.sendTitle(ChatColor.GRAY + "" + ChatColor.BOLD + "" + ChatColor.ITALIC + "You are a " + ChatColor.YELLOW + "DETECTIVE", "", 5, 30, 10);
					p.getInventory().setHeldItemSlot(4);
					Stash.addItemStack(p, new ItemStack(Material.BOW, 1), 0);
					Stash.addItemStack(p, new ItemStack(Material.ARROW, 1), 9);
				}
				else if(innocents.contains(p.getName())) {
					p.sendTitle(ChatColor.GRAY + "" + ChatColor.BOLD + "" + ChatColor.ITALIC + "You are a " + ChatColor.GREEN + "INNOCENT", "", 5, 30, 10);
				}
			}
		}
	}
	
	private void doOtherStuff() {
		world.setGameRule(GameRule.NATURAL_REGENERATION, false);
	}
	
	@SuppressWarnings("deprecation")
	private boolean hasItemInSomeHand(Player p, Material m) {
		return (p.getItemInHand().getType()==m || p.getInventory().getItemInOffHand().getType()==m);
	}
	
	@SuppressWarnings("deprecation")
	private void damageMurderer(Player p, int minusMaxDevideByWhat, String message, EntityDamageByEntityEvent e) {
		double current = p.getHealth();
		if(current-p.getMaxHealth()/minusMaxDevideByWhat>0.1) {
			/*new BukkitRunnable() { // can have multihit tick problems, think about it later
				@Override
				public void run() {
					SimpleLogger.debug("-health");
					if(p.getHealth()-p.getMaxHealth()/minusMaxDevideByWhat>0.1) {
						p.setHealth(current-p.getMaxHealth()/minusMaxDevideByWhat);
					}
					else {
//						killPlayer(p, message);
					}
				}
			}.runTaskLater(References.plugin, 2);
			p.setHealth(current-p.getMaxHealth()/minusMaxDevideByWhat);*/
			e.setDamage(p.getMaxHealth()/minusMaxDevideByWhat);
		}
		else {
			killPlayer(p, message);
		}
	}
	
	private void doDamageFromMurderer(Player m, Player o, EntityDamageByEntityEvent e) {
		// some config ballance stuff here
		if(murderers.contains(o.getName())) {
			// more ballance here
			String killedAMurderer = "You have killed another murderer!";
			damageMurderer(m, swordDoesWhatPartOfMurderersHp, killedAMurderer, e); // maybe they should be different
			damageMurderer(o, swordDoesWhatPartOfMurderersHp, "You were killed by another murderer", e); // "for some reason"
			if(!actualPlayers.contains(o.getName())) { // if victim nurderer died, but not the damager (he was at higher hp before)
				damageMurderer(m, 1, killedAMurderer, e);
			}
		}
		else {
			killPlayer(o, NamesAndLines.MmMessageKilledByUnknownPlayer);
		}
	}
	
	private void doDamageFromNonMurderer(Player m, Player o, EntityDamageByEntityEvent e) {
		// some config ballance stuff here
		SimpleLogger.log("doDamageFromNonMurderer");
		if(murderers.contains(o.getName())) {
			// also ballance here
			SimpleLogger.log("doDamageFromNonMurderer murderer");
			String message = "You were killed";
			damageMurderer(o, bowDoesWhatPartOfMurderersHp, message, e); // maybe shot info
		}
		else {
			killPlayer(o, NamesAndLines.MmMessageKilledByUnknownPlayer);
			String message = "You have killed a wrong player"; 
			if(m.equals(o)) {
				message = "You have killed yourself";
			}
			killPlayer(m, message);
		}
	}
	
	private void killPlayer(Player p, String message) {
		Location deathLocation = p.getLocation().clone();
		p.sendTitle(ChatColor.ITALIC + "" + ChatColor.BOLD + "" + ChatColor.DARK_RED + "YOU DIED", ChatColor.ITALIC + "" + ChatColor.GRAY + message, 5, 40, 30);
		spawnBloodParticles(p.getLocation());
		Preparations.turnVanishOn(p);
		Preparations.removeAllMaterialsAfterDeath(p, gm.minigameType);
		Location l = MurderMysteryLocations.getMurderMysterySpectatorSpawn(gm.map);
		l.setWorld(world);
		p.teleport(l);
		
		murderers.remove(p.getName());
		detectives.remove(p.getName());
		innocents.remove(p.getName());
		heroes.remove(p.getName());
		actualPlayers.remove(p.getName());
		checkIfItsAWin();
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(References.plugin, new Runnable() {
			@Override
			public void run() {
				p.setHealth(20.0);
			}
		}, 1);
		
		spawnBow(p, deathLocation);
	}
	
	private void spawnBloodParticles(Location loc) {
		Material blood = Material.REDSTONE_BLOCK;
		Bukkit.getOnlinePlayers().forEach(p -> {
			if(p.getWorld().getName().equals(loc.getWorld().getName())) {
				spawnBloodParticle(p, loc, blood, 0);
				spawnBloodParticle(p, loc, blood, 0.5f);
				spawnBloodParticle(p, loc, blood, 1f);
				spawnBloodParticle(p, loc, blood, 1.5f);
			}
		});
	}
	
	private void spawnBloodParticle(Player p, Location loc, Material blood, float d) {
		p.spawnParticle(Particle.BLOCK_CRACK, new Location(loc.getWorld(), loc.getX(), loc.getY() + d, loc.getZ()), 100, blood.createBlockData());
	}
	
	@EventHandler
	public void MurderMysterySwordDamage(EntityDamageByEntityEvent e) {
		if(e.getEntity().getWorld().equals(world)) {
			Player victim = null;
			Player damager = null;
			Arrow arrow = null;
			if(e.getEntity() instanceof Player) {
				victim = (Player) e.getEntity();
				if(e.getDamager() instanceof Player) {
					damager = (Player) e.getDamager();
				}
				else if (e.getDamager() instanceof Arrow) {
					arrow = (Arrow) e.getDamager();
					if(arrow.getShooter() instanceof Player) {
						damager = (Player) arrow.getShooter();
					}
				}
			}
			if(victim!=null && victim.getGameMode()!=GameMode.CREATIVE) {
				if(murderers.contains(damager.getName())) {
					if(hasItemInSomeHand(damager, Material.IRON_SWORD) || arrow!=null) {
						doDamageFromMurderer(damager, victim, e);
					}
				}
				else if(detectives.contains(damager.getName())) {
					if(arrow!=null) {
						doDamageFromNonMurderer(damager, victim, e);
					}
				}
				else if(heroes.contains(damager.getName())) {
					if(arrow!=null) {
						doDamageFromNonMurderer(damager, victim, e);
					}
				}
				else if(innocents.contains(damager.getName())) {
					if(arrow!=null) {
						doDamageFromNonMurderer(damager, victim, e);
					}
				}
			}
			if(arrow!=null && !damager.isDead()) {
				Stash.addItemStack(damager, new ItemStack(Material.ARROW, 1), 9);
			}
		}
	}
	
	@EventHandler
	public void MurderMysteryArrowHitABlock(ProjectileHitEvent e) {
		if(e.getEntity().getWorld().equals(world)){
			if(e.getEntity().getShooter() instanceof Player) {
				if(e.getEntity() instanceof Arrow) {
					SimpleLogger.debug(e.getHitEntity());
					Player p = (Player) e.getEntity().getShooter();
					if(detectives.contains(p.getName()) || heroes.contains(p.getName())) {
						e.getEntity().remove();
						Stash.addItemStack(p, new ItemStack(Material.ARROW, 1), 9);
					}
				}
			}
		}
	}
	
	private void checkIfItsAWin() {
		if(murderers.size()==0) { // innocents win
			gm.players.forEach(pN -> {
				Player p = Bukkit.getPlayerExact(pN);
				if(p!=null) {
					if(!savedMurderers.contains(pN)) {
						String message = ChatColor.GREEN + "You won!";
						String bottomMesage = "The murderer died!";
						if(amountOfMurderers>1) {
							message+= "All murderers died!"; // the??
							bottomMesage = "All murderers died!";
						}
						p.sendMessage(ChatColor.GRAY + message);
						
						p.sendTitle(ChatColor.GREEN + "YOU WON!", ChatColor.GRAY + bottomMesage, 2, 100, 10);
					}
					else {
						String message = ChatColor.RED + "You lost!";
						String bottomMesage = "You died!";
						if(amountOfMurderers>1) {
							message+= "All murderers died!";
							bottomMesage = "All murderers died!";
						}
						p.sendMessage(ChatColor.GRAY + message);
						p.sendTitle(ChatColor.RED + "YOU LOST!", ChatColor.GRAY + bottomMesage, 2, 100, 10);
					}
				}
			});
			gameFinished = true;
			onGameEnd();
		}
		else if(timePast>=fullTime) { // innocents win by time
			gm.players.forEach(pN -> {
				Player p = Bukkit.getPlayerExact(pN);
				if(p!=null) {
					if(!savedMurderers.contains(pN)) {
						String message = ChatColor.GREEN + "You won!";
						String bottomMesage = "Innocents survived!";
						if(amountOfMurderers>1) {
							message+= "The murderers didn't kill everyone in time!";
						} else {
							message+= "The murderer didn't kill everyone in time!";
						}
						p.sendMessage(ChatColor.GRAY + message);
						
						p.sendTitle(ChatColor.GREEN + "YOU WON!", ChatColor.GRAY + bottomMesage, 2, 100, 10);
					}
					else {
						String message = ChatColor.RED + "You lost!";
						String bottomMesage = "You ran out of time!";
						message+="You didn't kill everyone in time.";
						p.sendMessage(ChatColor.GRAY + message);
						p.sendTitle(ChatColor.RED + "YOU LOST!", ChatColor.GRAY + bottomMesage, 2, 100, 10);
					}
				}
			});
			gameFinished = true;
			onGameEnd();
		}
		else if(detectives.size()==0 && innocents.size()==0 && heroes.size()==0) { // murderers win
			//String message = ChatColor.GREEN + "You won! You have successfully killed everyone!";
			actualPlayers.forEach(pN -> {
				Player p = Bukkit.getPlayerExact(pN);
				if(p!=null) {
					if(savedMurderers.contains(pN)) {
						String message = ChatColor.GREEN + "You won!";
						String bottomMesage = "The murderer died!";
						if(amountOfMurderers>1) {
							message+= "You and other murderers have successfully killed everyone!";
							bottomMesage = "You have killed everyone!";
						} else {
							message += "You have successfully killed everyone!";
						}
						p.sendMessage(ChatColor.GRAY + message);
						p.sendTitle(ChatColor.GREEN + "YOU WON!", ChatColor.GRAY + bottomMesage, 2, 100, 10);
					}
					else {
						String message = ChatColor.RED + "You lost!";
						String bottomMesage = "The murderer has killed everyone!";
						if(amountOfMurderers>1) {
							message+= "The murderers have killed everyone!";
							bottomMesage = "The murderers have killed everyone!";
						}
						p.sendMessage(ChatColor.GRAY + message);
						p.sendTitle(ChatColor.RED + "YOU LOST!", ChatColor.GRAY + bottomMesage, 2, 100, 10);
					}
				}
			});
			gameFinished = true;
			onGameEnd();
		}
	}
	
	@SuppressWarnings("deprecation")
	private void spawnBow(Player p, Location loc) {
		if(!detectives.contains(p.getName()) && !heroes.contains(p.getName())) {
			return;
		}
		loc = loc.getBlock().getLocation(); // is that a copy already or bit, but doesn't matter already actually
		loc.setX(loc.getX()+0.5);
		loc.setZ(loc.getZ()+0.5);
		ArmorStand stand = world.spawn(loc, ArmorStand.class);
		stand.setVisible(false);
		stand.setBasePlate(false);
		stand.setCanPickupItems(false);
        stand.setCollidable(false);
        stand.setGravity(false);
        stand.setRemoveWhenFarAway(false);
        stand.setRightArmPose(new EulerAngle(4.69, 0, 0));
        stand.setItemInHand(new ItemStack(Material.BOW));
        droppedBows.add(stand);
        ItemStack compass = new ItemStack(Material.COMPASS, 1);
		for (String s : innocents) {
        	Player inn = Bukkit.getPlayerExact(s);
        	if(inn!=null) {
        		Stash.addItemStack(inn, compass, 4);
        	}
        }
        updateCompasses();
        final Location center = loc;
        final float radius = 0.2f;
        final float radPerSec = 3f;
        final float radPerTick = radPerSec / 20f;
        	new BukkitRunnable() {
        		int tick = 0;
        		@Override
        		public void run() {
        			if(gameFinished || stand==null) {
        				SimpleLogger.log("stand got null value");
        				cancel();
        				if(stand!=null && !stand.isDead()) {
        					stand.remove();
        				}
        			}
        			++tick;
        			Location loc = getLocationAroundCircle(center, radius, radPerTick * tick);
                    stand.setVelocity(new Vector(0, 0, 0));
                    stand.teleport(loc);
        		}
        	}.runTaskTimer(References.plugin, 0, 2);
	}
	
	private Location getLocationAroundCircle(Location center, double radius, double angleInRadian) {
        double x = center.getX() - radius * Math.cos(angleInRadian);
//        double y = center.getY() - radius * Math.sin(angleInRadian);
        double z = center.getZ() - radius * Math.sin(angleInRadian);

        Location loc = new Location(center.getWorld(), x, center.getY(), z);
        Vector difference = center.toVector().clone().subtract(loc.toVector()); // this sets the returned location's direction toward the center of the circle
        loc.setDirection(difference);

        return loc;
    }
	
	@EventHandler
	public void MurderMysteryPickingUpBow(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if(p.getWorld().equals(world)) {
			if(innocents.contains(p.getName())) {
				for(int i = 0; i < droppedBows.size(); i++) {
					if(droppedBows.get(i).getLocation().distance(p.getLocation()) <= distanceToBowToPickUp) {
						heroes.add(p.getName());
						innocents.remove(p.getName());
						ArmorStand stand = droppedBows.get(i);
						droppedBows.remove(i);
						stand.remove();
						Stash.addItemStack(p, new ItemStack(Material.BOW, 1), 0);
						Stash.addItemStack(p, new ItemStack(Material.ARROW, 1), 9);
						if(droppedBows.size()==0) {
							for (String inn : innocents) {
								if(Bukkit.getPlayerExact(inn)!=null) {
									Stash.removeMaterial(Bukkit.getPlayerExact(inn), Material.COMPASS);
								}
					        }
							Stash.removeMaterial(p, Material.COMPASS);
						} else {
							updateCompasses();
						}
					}
				}
			}
		}
	}
	
	private void updateCompasses() {
		for (String s : innocents) {
        	Player inn = Bukkit.getPlayerExact(s);
        	if(inn!=null) {
        		inn.setCompassTarget(getNearestBow(inn));
        	}
        }
	}
	
	private Location getNearestBow(Player p) {
		if(droppedBows.size()==1) {
			return droppedBows.get(0).getLocation();
		}
		else if(droppedBows.size()>1) {
			double minDist = -1;
			Location bestLocation = null;
			for (int i = 0; i < droppedBows.size(); i++) {
				Location currentLocation = droppedBows.get(i).getLocation();
				double currentDist = p.getLocation().distance(currentLocation);
				if(minDist==-1 || currentDist < minDist) {
					minDist = currentDist;
					bestLocation = currentLocation;
				}
			}
			return bestLocation;
		}
		return null;
	}
	
	@Override
	public GameManager getGm() {
		return gm;
	}
	
	@Override
	public void setGm(GameManager gm) {
		this.gm = gm;
	}
	
	@EventHandler
	public void disablePlayerDamage(EntityDamageEvent e) {
		if(e.getEntity() instanceof Player) {
			if(e.getEntity().getWorld().equals(world)) {
				switch (e.getCause()) {
					case FALL: {
						e.setCancelled(true);
						break;
					}
					case DROWNING: {
						e.setCancelled(true);
						break;
					}
					case FIRE_TICK: {
						e.setCancelled(true);
						break;
					}
					case FIRE: { // what about lava, maybe very fast damage, almost like in fundy's difficulties
						e.setCancelled(true);
						break;
					}
					case HOT_FLOOR: {
						e.setCancelled(true);
						break;
					}
					case VOID: {
						if(e.getEntity().getLocation().getY() < -20) {
							e.setCancelled(true);
						}
						break;
					}
					case CONTACT: {
						e.setCancelled(true);
						break;
					}
				default:
					break;
				}
			}
		}
	}
	
	private void startRepeatingRunnables() {
		repeatingTask = Bukkit.getScheduler().runTaskTimer(References.plugin, new Runnable() {
			@Override
			public void run() {
				if(!gameFinished) {
					timePast++;
					spawnGoldIfNeeded();
					checkIfTimeIsUp();
				}
			}
		}, 0, 20);
	}
	
	private void spawnGoldIfNeeded() {
		if(skipTimer==goldSkipSeconds) {
			if(goldCurrentlySpawned<murderMysteryMaxGoldSpawned) {
				int r = rand.nextInt(goldSpawnChancePerSecond);
				if(r==0) {
					ArrayList<Location> locs = MurderMysteryLocations.goldSpawnLocations.get(gm.map);
					int number = rand.nextInt(locs.size());
					Location l = locs.get(number);
					l.setWorld(world);
					ItemStack is = new ItemStack(Material.GOLD_INGOT);
					is.getItemMeta().setDisplayName(NamesAndLines.goldIngotName);
					world.dropItemNaturally(l, is);
					goldCurrentlySpawned++;
				}
			}
			skipTimer = 0;
		}
		skipTimer++; // skips first time too, maybe ballance that
	}
	
	private void checkIfTimeIsUp() {
		checkIfItsAWin();
	}
	
	@EventHandler public void onItemPickup(EntityPickupItemEvent e) {
		if(e.getEntity() instanceof Player) {
			if(e.getEntity().getWorld().equals(world)) {
				Player p = (Player) e.getEntity();
				ItemStack item = e.getItem().getItemStack();
				if(item.getType()==Material.GOLD_INGOT) {
					if(item.getItemMeta().getDisplayName().equals(NamesAndLines.goldIngotName)) {
						SimpleLogger.debug(item.getAmount());
//						p.getInventory().remove(item);
						goldCurrentlySpawned--;
						// do stuff
					}
				}
			}
		}
	}
	
	private void onGameEnd() {
		repeatingTask.cancel(); // sure? right here?
		
		
		Bukkit.getScheduler().runTaskLater(References.plugin, new Runnable() {
			
			@Override
			public void run() {
				// tp everyone to spawn
				// clear gold
				clearGoldOnFloor();
			}
		}, 20*5);
	}
	
	private void clearGoldOnFloor() {
		for (Entity e : world.getEntities()) {
			if(e.getType()==EntityType.DROPPED_ITEM) {
				e.remove();
			}
		}
	}
	
	private void ballanceMechanics() {
		// gold
		if(actualPlayers.size() < 5) {
			murderMysteryMaxGoldSpawned = 5;
		} else if(actualPlayers.size() < 10) {
			murderMysteryMaxGoldSpawned = 10;
		} else if(actualPlayers.size() < 15) {
			murderMysteryMaxGoldSpawned = 15;
		} else {
			murderMysteryMaxGoldSpawned = 20;
		}
		
		// murderer's hp
		if(murderers.size() == 1) {
			bowDoesWhatPartOfMurderersHp = 1;
		} else {
			bowDoesWhatPartOfMurderersHp = 2;
		}
	}
}
