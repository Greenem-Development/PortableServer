package org.greenem.plugins.server.main;

import static org.greenem.plugins.server.util.SimpleLogger.log;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import org.bukkit.event.Listener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.greenem.plugins.server.commands.AcceptTeam;
import org.greenem.plugins.server.commands.LeaveTeam;
import org.greenem.plugins.server.data.ConfigData;
import org.greenem.plugins.server.data.MinigamesStaticData;
import org.greenem.plugins.server.data.OutsideData;
import org.greenem.plugins.server.data.References;
import org.greenem.plugins.server.data.Structure;
import org.greenem.plugins.server.events.MainEvents;
import org.greenem.plugins.server.game.GameManager;
import org.greenem.plugins.server.game.GameSelectScreen;
import org.greenem.plugins.server.game.CreateTeamManager;
import org.greenem.plugins.server.game.ExtraGameData;
import org.greenem.plugins.server.game.Minigame;
import org.greenem.plugins.server.minigames.murderMystery.MurderMysteryLocations;
import org.greenem.plugins.server.miscellaneous.Stash;
import static org.greenem.plugins.server.util.SimpleLogger.log;import static org.greenem.plugins.server.util.SimpleLogger.debug;
import org.greenem.plugins.server.worldProtection.MainProtector;
import org.greenem.plugins.server.worldProtection.PlaceDependentEvents;

public class Main extends JavaPlugin implements Listener {
	
	public Plugin plugin = this;
	public Logger log = getLogger();
	private String coordsStr = "";

	@Override
	public void onEnable() {
		log(ChatColor.GREEN + "Your Portable Server plugin has been enabled!");
		
		initAll();
		
		loadConfig();
	}
	
	@Override
	public void onDisable() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.closeInventory();
		}
		log(ChatColor.RED + "Your Portable Server plugin has been disabled.");
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		Player p = null;
		String pName = "";
		try {
			p = (Player) sender;
			pName = p.getName();
		} catch (ClassCastException e) {}
		
		if (cmd.getName().equalsIgnoreCase("mhreloadconfig")) {
			if(!isSenderAPlayerOrConsole(sender)) {
				return true;
			}
			loadConfig();
		}
		else if(cmd.getName().equalsIgnoreCase("gm")){
			if(isSenderAPlayerOrConsole(sender)) {
				Stash.GmCommand(sender, args);
			}
		}
		else if(cmd.getName().equalsIgnoreCase("heal")){
			if(isSenderAPlayerOrConsole(sender)) {
				Stash.HealCommand(sender, p, null, args);
			}
		}
		else if(cmd.getName().equalsIgnoreCase("minigametest1")){
			if(isSenderAPlayer(sender)) {
				GameManager gameManager = new GameManager();
				gameManager.minigameType = Minigame.MURDER_MYSTERY;
//				gameManager.players.add(sender.getName());
				for (Player serverPlayer : Bukkit.getOnlinePlayers()) { // for test
					gameManager.players.add(serverPlayer.getName());
				}
				int success = gameManager.start();
				if(success==0) {
					Structure.gameManagers.add(gameManager);
					References.playersInGame.add(pName);
				}
				else if(success==-1) {
					sender.sendMessage(ChatColor.RED + "There is currently no world avaliable to start the game");
				}
				else if(success==1) {
					sender.sendMessage(ChatColor.RED + "An error occured while starting the game");
					log(ChatColor.RED + "STARTING A GAME DIDN'T SUCCEED! (Minigame type: " + gameManager.minigameType + ")");
				}
			}
		}
		else if(cmd.getName().equalsIgnoreCase("minigametest2")){
			if(isSenderAPlayer(sender)) {
				Structure.gameManagers.get(0).stop();
				Structure.gameManagers.remove(0);
				References.playersInGame.remove(pName);
			}
		}
		else if(cmd.getName().equalsIgnoreCase("coordstest1")){
			if(isSenderAPlayer(sender)) {
				Location l = ((Player)sender).getLocation();
				coordsStr += "current.add(new Location(Bukkit.getWorld(\"\"), " + l.getBlockX() + ", " + l.getBlockY() + ", " + l.getBlockZ() + "));\n";
			}
		}
		else if(cmd.getName().equalsIgnoreCase("coordstest2")){
			if(isSenderAPlayer(sender)) {
				log.info(coordsStr);
				coordsStr = "";
			}
		}
		else if(cmd.getName().equalsIgnoreCase("dogoodgamerules")){
			if(isSenderAPlayer(sender)) {
				Stash.setGoodGamerules(((Player) sender).getWorld());
			}
		}
		return false;
	}
	
	public void logCastEx(String currentKey) {
		log("Wrong value type for \"" + currentKey + "\" in the config");
	}
	
	public void loadConfig() {
		File f = new File(OutsideData.mainConfigDirectory, "mainconfig.yml");
		f.getParentFile().mkdirs();
		
		YamlConfiguration c = YamlConfiguration.loadConfiguration(f);
		YamlConfiguration cOld = YamlConfiguration.loadConfiguration(f);
		String currentKey = null;

		try {
			currentKey = "allow-gm-command-for-ops";
			if (c.get(currentKey) == null) {
				c.set(currentKey, true);
			} else {
				try {
					ConfigData.allowGmCommandForOps = (boolean) c.get(currentKey);
				} catch (ClassCastException e) {
					logCastEx(currentKey);
				}
			}
			currentKey = "allow-heal-command-for-ops";
			if (c.get(currentKey) == null) {
				c.set(currentKey, true);
			} else {
				try {
					ConfigData.allowHealCommandForOps = (boolean) c.get(currentKey);
				} catch (ClassCastException e) {
					logCastEx(currentKey);
				}
			}
			currentKey = "allow-invites-before-reload";
			if (c.get(currentKey) == null) {
				c.set(currentKey, true);
			} else {
				try {
					ConfigData.allowInvitesBeforeReload = (boolean) c.get(currentKey);
				} catch (ClassCastException e) {
					logCastEx(currentKey);
				}
			}
			currentKey = "allow-invites-nickname-mismatch";
			if (c.get(currentKey) == null) {
				c.set(currentKey, false);
			} else {
				try {
					ConfigData.allowInvitesNicknameMismatch = (boolean) c.get(currentKey);
				} catch (ClassCastException e) {
					logCastEx(currentKey);
				}
			}
		} catch (Throwable t) {
			log("Error while reading \"" + currentKey + "\" from the config");
		}
		
		// DONE
		
		boolean same = true;
	    for(String key : c.getKeys(false)) {
	        if(!cOld.contains(key)) {
	        	same = false;
	        }
	    }
	    
		if (!same) {
			try {
				c.save(f);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static boolean isSenderAPlayer(CommandSender sender) {
		if(sender instanceof ConsoleCommandSender) {
			sender.sendMessage(ChatColor.RED + "You can't do it through console!");
			return false;
		}
		if(!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "You can use it only as a player!");
			return false;
		}
		return true;
	}
	
	public static boolean isSenderAPlayerOrConsole(CommandSender sender) {
		if(!(sender instanceof Player) && !(sender instanceof ConsoleCommandSender)) {
			sender.sendMessage(ChatColor.RED + "You can use it only as a player!");
			return false;
		}
		return true;
	}
	
	public void initAll() {
		RegisterAllCommands();
		InitFiles();
		RegisterAllListeners();
		MinigamesStaticData.init();
		MurderMysteryLocations.init();
		OutsideData.init();
		GameSelectScreen.init();
	}
	
	public void InitFiles() {
		OutsideData.configsDirectory = new File(getServer().getWorldContainer(), "configs");
		OutsideData.mainConfigDirectory = new File(OutsideData.configsDirectory, "Server");
		
		OutsideData.mainConfigDirectory.mkdirs();
	}
	
	public void RegisterAllCommands() {
		getCommand("acceptteam").setExecutor(new AcceptTeam());
		getCommand("leaveteam").setExecutor(new LeaveTeam());		
		getCommand("createteam").setExecutor(new CreateTeamManager());
//		getCommand("command").setTabCompleter(new EmptyCompleterNoArgs());
	}
	
	public void RegisterAllListeners() {
		PluginManager manager = Bukkit.getPluginManager();
		manager.registerEvents(this, this);
		manager.registerEvents(new MainEvents(), this);
		manager.registerEvents(new GameSelectScreen(), this);
		manager.registerEvents(new GameSelectScreen(), this);
		manager.registerEvents(new MainProtector(), this);
		manager.registerEvents(new PlaceDependentEvents(), this);
	}
}