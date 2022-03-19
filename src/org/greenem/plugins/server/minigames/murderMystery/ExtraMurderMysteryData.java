package org.greenem.plugins.server.minigames.murderMystery;

import java.util.ArrayList;
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
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import org.greenem.plugins.server.data.MinigamesStaticData;
import org.greenem.plugins.server.data.NamesAndLines;
import org.greenem.plugins.server.data.References;
import org.greenem.plugins.server.game.ExtraGameData;
import org.greenem.plugins.server.game.GameManager;
import org.greenem.plugins.server.miscellaneous.Preparations;
import org.greenem.plugins.server.miscellaneous.Stash;
import static org.greenem.plugins.server.util.SimpleLogger.log;
import static org.greenem.plugins.server.util.SimpleLogger.debug;

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
	public int murderMysteryMaxGoldSpawned = 0; // auto
	public int bowDoesWhatPartOfMurderersHp = 1; // auto (1 - full, 2 - half)
	public int swordDoesWhatPartOfMurderersHp = 2;
	public int goldSpawnChancePerSecond = 1; // 1 - 50% chance, 2 - 33% chance           3
	public int goldSkipSeconds = 1;
	private int skipTimer = 0;
	private ArrayList<Location> localGoldSpawnLocations = null;
	private ArrayList<Integer> GoldSpawnLocationsIndexesAlreayUsed = new ArrayList<>();
	private ArrayList<SpawnedGold> GoldSpawnExistingItems = new ArrayList<>();
	private float goldPickupDeleteFromUsedThreshold = 1.5f;
	public Scoreboard mainScoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
	
	@Override
	public String start() {
		localGoldSpawnLocations = MurderMysteryLocations.goldSpawnLocations.get(gm.map);
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
		hideNicknames();
		teleportEveryone();
		randomizeRoles();
		announceRoles();
		doOtherStuff();
		ballanceMechanics();
		startRepeatingRunnables();
		return null;
	}
	
	private void hideNicknames() {
		String hideNicknameTeamName = "hidenicknames";
		Team t = mainScoreboard.getTeam(hideNicknameTeamName);
		if(t==null) {
			t = mainScoreboard.registerNewTeam(hideNicknameTeamName); // works with _ ?
		}
	    t.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
		for (String s : actualPlayers) {
			t.addEntry(s);
		}
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
				debug("Murderer " + num);
				murderers.add(playersNeedRole.get(num));
				playersNeedRole.remove(num);
			}
		}
		for(int i = 0; i < amountOfDetectives; i++) {
			if(playersNeedRole.size() > 0) {
				int num = rand.nextInt(playersNeedRole.size());
				debug("Detective " + num);
				detectives.add(playersNeedRole.get(num));
				playersNeedRole.remove(num);
			}
		}
		for(int i = 0; i < playersNeedRole.size(); i++) {
			if(playersNeedRole.size() > 0) { //maybe no innocents already
				debug("Innocent ");
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
					Stash.addItemStack(p, getMurdererSword(), 0);
				}
				else if(detectives.contains(p.getName())) {
					p.sendTitle(ChatColor.GRAY + "" + ChatColor.BOLD + "" + ChatColor.ITALIC + "You are a " + ChatColor.YELLOW + "DETECTIVE", "", 5, 30, 10);
					p.getInventory().setHeldItemSlot(4);
					Stash.addItemStack(p, getDetectiveBow(), 0);
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
					debug("-health");
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
			killPlayer(o, NamesAndLines.murderMysteryMessageKilledByUnknownPlayer);
		}
	}
	
	private void doDamageFromNonMurderer(Player m, Player o, EntityDamageByEntityEvent e) {
		// some config ballance stuff here
		log("doDamageFromNonMurderer");
		if(murderers.contains(o.getName())) {
			// also ballance here
			log("doDamageFromNonMurderer murderer");
			String message = "You were killed";
			damageMurderer(o, bowDoesWhatPartOfMurderersHp, message, e); // maybe shot info
		}
		else {
			killPlayer(o, NamesAndLines.murderMysteryMessageKilledByUnknownPlayer);
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
		
		spawnBow(p, deathLocation);
		
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
			if(gameFinished) {
				e.setCancelled(true);
				return;
			}
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
				if(victim!=null && victim.getGameMode()!=GameMode.CREATIVE) {
					if(murderers.contains(damager.getName()) && (hasItemInSomeHand(damager, Material.IRON_SWORD) || arrow!=null)) {
						doDamageFromMurderer(damager, victim, e);
					}
					else if(detectives.contains(damager.getName()) && arrow!=null) {
						if(arrow!=null) {
							doDamageFromNonMurderer(damager, victim, e);
						}
					}
					else if(heroes.contains(damager.getName()) && arrow!=null) {
						doDamageFromNonMurderer(damager, victim, e);
					}
					else if(innocents.contains(damager.getName()) && arrow!=null) {
						doDamageFromNonMurderer(damager, victim, e);
					}
					else {
						e.setCancelled(true);
					}
				}
				if(arrow!=null && !damager.isDead()) {
					Stash.addItemStack(damager, new ItemStack(Material.ARROW, 1), 9);
				}
			}
		}
	}
	
	@EventHandler
	public void DisableArmorStandIntaraction (PlayerInteractAtEntityEvent e) {
		if(e.getPlayer().getWorld().equals(world)) {
			if(e.getRightClicked() instanceof ArmorStand) {
//				if(e.getPlayer().getGameMode().equals(GameMode.ADVENTURE) || e.getPlayer().getGameMode().equals(GameMode.SURVIVAL)) {
				e.setCancelled(true);
//				}
			}
		}
	}
	
	@EventHandler
	public void MurderMysteryArrowHitABlock(ProjectileHitEvent e) {
		if(e.getEntity().getWorld().equals(world)){
			if(e.getEntity().getShooter() instanceof Player) {
				if(e.getEntity() instanceof Arrow) {
					debug(e.getHitEntity());
					Player p = (Player) e.getEntity().getShooter();
					if(actualPlayers.contains(p.getName())) {
						e.getEntity().remove();
						if(detectives.contains(p.getName()) || heroes.contains(p.getName())) {
							Stash.addItemStack(p, new ItemStack(Material.ARROW, 1), 9);
						}
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
						//String message = ChatColor.GREEN + "You won!";
						String bottomMesage = "The murderer died!";
						if(amountOfMurderers>1) {
							//message+= "All murderers died!"; // the??
							bottomMesage = "All murderers died!";
						}
						//p.sendMessage(ChatColor.GRAY + message);
						p.sendTitle(ChatColor.GREEN + "YOU WON!", ChatColor.GRAY + bottomMesage, 2, 100, 10);
					}
					else {
						//String message = ChatColor.RED + "You lost!";
						String bottomMesage = "You died!";
						if(amountOfMurderers>1) {
							//message+= "All murderers died!";
							bottomMesage = "All murderers died!";
						}
						//p.sendMessage(ChatColor.GRAY + message);
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
						//String message = ChatColor.GREEN + "You won!";
						String bottomMesage = "Innocents survived!";
						/*if(amountOfMurderers>1) {
							message+= "The murderers didn't kill everyone in time!";
						} else {
							message+= "The murderer didn't kill everyone in time!";
						}*/
						//p.sendMessage(ChatColor.GRAY + message);
						p.sendTitle(ChatColor.GREEN + "YOU WON!", ChatColor.GRAY + bottomMesage, 2, 100, 10);
					}
					else {
						//String message = ChatColor.RED + "You lost!";
						String bottomMesage = "You ran out of time!";
						//message+="You didn't kill everyone in time.";
						//p.sendMessage(ChatColor.GRAY + message);
						p.sendTitle(ChatColor.RED + "YOU LOST!", ChatColor.GRAY + bottomMesage, 2, 100, 10);
					}
				}
			});
			gameFinished = true;
			onGameEnd();
		}
		else if(detectives.size()==0 && innocents.size()==0 && heroes.size()==0) { // murderers win
			//String message = ChatColor.GREEN + "You won! You have successfully killed everyone!";
			gm.players.forEach(pN -> {
				Player p = Bukkit.getPlayerExact(pN);
				if(p!=null) {
					if(savedMurderers.contains(pN)) {
						//String message = ChatColor.GREEN + "You won!";
						String bottomMesage = "You have killed everyone!";
						/*if(amountOfMurderers>1) {
							message+= "You and other murderers have successfully killed everyone!";
							bottomMesage = "You have killed everyone!";
						} else {
							message += "You have successfully killed everyone!";
						}*/
						//p.sendMessage(ChatColor.GRAY + message);
						p.sendTitle(ChatColor.GREEN + "YOU WON!", ChatColor.GRAY + bottomMesage, 2, 100, 10);
					}
					else {
						//String message = ChatColor.RED + "You lost!";
						String bottomMesage = "The murderer has killed everyone!";
						if(amountOfMurderers>1) {
							//message+= "The murderers have killed everyone!";
							bottomMesage = "The murderers have killed everyone!";
						}
						//p.sendMessage(ChatColor.GRAY + message);
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
			debug("no detective");
			return;
		}
		debug("detective");
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
        ItemStack compass = getCompass();
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
        				log("stand got null value");
        				if(stand!=null && !stand.isDead()) {
        					stand.remove();
        				}
        				cancel();
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
						Stash.addItemStack(p, getDetectiveBow(), 0);
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
			if(goldCurrentlySpawned<=murderMysteryMaxGoldSpawned) {
				int r = rand.nextInt(goldSpawnChancePerSecond);
				if(r==0) {
					boolean foundUnused = false;
					int number = 0;
					while (!foundUnused) {
						number = rand.nextInt(localGoldSpawnLocations.size());
						if(!GoldSpawnLocationsIndexesAlreayUsed.contains(Integer.valueOf(number))) {
							foundUnused = true;
						}
					}
					Location l = localGoldSpawnLocations.get(number);
					l.setWorld(world);
					ItemStack is = getSpecialGoldItemStack();
					Item item = world.dropItemNaturally(l, is);
					SpawnedGold sg = new SpawnedGold();
					sg.item = item;
					sg.location = l;
					sg.number = number;
					GoldSpawnExistingItems.add(sg);
					goldCurrentlySpawned++;
					GoldSpawnLocationsIndexesAlreayUsed.add(number);
				}
			}
			skipTimer = 0;
		}
		skipTimer++; // skips first time too, maybe ballance that
	}
	
	private ItemStack getSpecialGoldItemStack() {
		ItemStack is = new ItemStack(Material.GOLD_INGOT);
		ItemMeta meta = is.getItemMeta();
		meta.setDisplayName(NamesAndLines.murderMysteryGoldIngotName);
		is.setItemMeta(meta);
		return is;
	}
	
	private ItemStack getNormalBow() {
		ItemStack is = new ItemStack(Material.BOW);
		ItemMeta metaExtra = is.getItemMeta();
		metaExtra.setDisplayName(NamesAndLines.murderMysteryNormalBowName);
		metaExtra.setUnbreakable(true);
		metaExtra.addItemFlags(ItemFlag.HIDE_UNBREAKABLE); // is it still nessessary?
		metaExtra.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		is.setItemMeta(metaExtra);
		return is;
	}
	
	private ItemStack getDetectiveBow() {
		ItemStack is = new ItemStack(Material.BOW);
		ItemMeta metaExtra = is.getItemMeta();
		metaExtra.setDisplayName(NamesAndLines.murderMysteryDetectiveBowName);
		metaExtra.setUnbreakable(true);
		metaExtra.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		metaExtra.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		is.setItemMeta(metaExtra);
		return is;
	}
	
	private ItemStack getMurdererSword() {
		ItemStack is = new ItemStack(Material.IRON_SWORD);
		ItemMeta metaExtra = is.getItemMeta();
		metaExtra.setDisplayName(NamesAndLines.murderMysteryMurdererSwordName);
		metaExtra.setUnbreakable(true);
		metaExtra.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		metaExtra.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		is.setItemMeta(metaExtra);
		return is;
	}
	
	private ItemStack getCompass() {
		ItemStack is = new ItemStack(Material.COMPASS);
		ItemMeta metaExtra = is.getItemMeta();
		metaExtra.setDisplayName(NamesAndLines.murderMysteryCompassName);
		metaExtra.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		is.setItemMeta(metaExtra);
		return is;
	}
	
	private void checkIfTimeIsUp() {
		checkIfItsAWin();
	}
	
	private boolean isItemStackTheGold(ItemStack itemStack) {
		if(itemStack==null) {
			return false;
		}
		if(itemStack.getType()==Material.GOLD_INGOT) {
			if(itemStack.getItemMeta().getDisplayName().equals(NamesAndLines.murderMysteryGoldIngotName)) {
				return true;
			}
		}
		return false;
	}
	
	@EventHandler public void onItemPickup(EntityPickupItemEvent e) {
		if(e.getEntity() instanceof Player) {
			if(e.getEntity().getWorld().equals(world)) {
				Player p = (Player) e.getEntity();
				if(murderers.contains(p.getName()) || innocents.contains(p.getName())) {
					ItemStack itemPickedUp = e.getItem().getItemStack();
					if(isItemStackTheGold(itemPickedUp)) {
						debug(itemPickedUp.getAmount());
						goldCurrentlySpawned--;
						debug("goldCurrentlySpawned " + goldCurrentlySpawned);
						int hasGold = 0;
						for (int i = 0; i < p.getInventory().getSize(); i++) {
							ItemStack itemStack = p.getInventory().getItem(i);
							if(isItemStackTheGold(itemStack)) {
								hasGold += itemStack.getAmount();
							}
						}
						hasGold+=itemPickedUp.getAmount(); // just picked up
						debug("hasGold " + hasGold);
						if(hasGold>=10) {
							int getsArrows = hasGold/10; // 10 can be customisable
							debug("getsArrows " + getsArrows);
							int hasArrows = 0;
							for (int i = 0; i < p.getInventory().getSize(); i++) {
								ItemStack itemStack = p.getInventory().getItem(i);
								if(itemStack!=null && itemStack.getType()==Material.ARROW) {
									hasArrows += itemStack.getAmount();
								}
							}
							debug("hasArrows " + hasArrows);
							Stash.addItemStack(p, new ItemStack(Material.ARROW, hasArrows + getsArrows), 8);
							Stash.removeMaterial(p, Material.GOLD_INGOT);
							if(!p.getInventory().contains(Material.BOW)) {
								Stash.addItemStack(p, getNormalBow(), 0);
							}
							ItemStack is = getSpecialGoldItemStack();
							if(hasGold%10!=0) {
								is.setAmount(hasGold%10);
								Stash.addItemStack(p, is, 0);
							}
							else {
								Stash.removeMaterial(p, Material.GOLD_INGOT);
							}
						}
					}
					for (int i = 0; i < GoldSpawnExistingItems.size(); i++) {
						if(GoldSpawnExistingItems.get(i).location.distance(e.getItem().getLocation()) < goldPickupDeleteFromUsedThreshold) {
							debug("removing num " + GoldSpawnExistingItems.get(i).number);
							debug("the loc is " + GoldSpawnExistingItems.get(i).location.toString());
							GoldSpawnLocationsIndexesAlreayUsed.remove(Integer.valueOf(GoldSpawnExistingItems.get(i).number));
							GoldSpawnExistingItems.remove(i);
						}
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
			murderMysteryMaxGoldSpawned = 12;
		} else if(actualPlayers.size() < 10) {
			murderMysteryMaxGoldSpawned = 25;
		} else if(actualPlayers.size() < 15) {
			murderMysteryMaxGoldSpawned = 40;
		} else {
			murderMysteryMaxGoldSpawned = 70;
		}
		
		// murderer's hp
		if(murderers.size() == 1) {
			bowDoesWhatPartOfMurderersHp = 1;
		} else {
			bowDoesWhatPartOfMurderersHp = 2;
		}
	}
}
