package org.greenem.plugins.Server.main;

import static org.greenem.plugins.Server.util.SimpleLogger.log;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import org.bukkit.event.Listener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.greenem.plugins.Server.commands.AcceptParty;
import org.greenem.plugins.Server.commands.LeaveParty;
import org.greenem.plugins.Server.data.ConfigData;
import org.greenem.plugins.Server.data.MinigamesStaticData;
import org.greenem.plugins.Server.data.OutsideData;
import org.greenem.plugins.Server.data.References;
import org.greenem.plugins.Server.data.Structure;
import org.greenem.plugins.Server.events.MainEvents;
import org.greenem.plugins.Server.game.GameManager;
import org.greenem.plugins.Server.game.GameSelectScreen;
import org.greenem.plugins.Server.game.CreatePartyManager;
import org.greenem.plugins.Server.game.Minigame;
import org.greenem.plugins.Server.minigames.murderMystery.MurderMysterySpawnLocations;
import org.greenem.plugins.Server.miscellaneous.Stash;
import org.greenem.plugins.Server.util.SimpleLogger;

public class Main extends JavaPlugin implements Listener {
	
	public Plugin plugin = this;
	public Logger log = getLogger();
	private String coordsStr = "";

	@Override
	public void onEnable() {
		log(ChatColor.GREEN + "Your Portable Server plugin has been enabled!");
		
		getServer().getPluginManager().registerEvents(this, this);
		getServer().getPluginManager().registerEvents(new MainEvents(), this);
		initAll();
		
		loadConfig();
	}
	
	@Override
	public void onDisable() {
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
					SimpleLogger.log(ChatColor.RED + "STARTING A GAME DIDN'T SUCCEED! (Minigame type: " + gameManager.minigameType + ")");
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
		MinigamesStaticData.init();
		MurderMysterySpawnLocations.init();
		OutsideData.init();
		GameSelectScreen.init();
	}
	
	public void InitFiles() {
		OutsideData.configsDirectory = new File(getServer().getWorldContainer(), "configs");
		OutsideData.mainConfigDirectory = new File(OutsideData.configsDirectory, "Server");
		
		OutsideData.mainConfigDirectory.mkdirs();
	}
	
	public void RegisterAllCommands() {
		getCommand("acceptparty").setExecutor(new AcceptParty());
		getCommand("leaveparty").setExecutor(new LeaveParty());		
		getCommand("startgame").setExecutor(new CreatePartyManager());
//		getCommand("command").setTabCompleter(new EmptyCompleterNoArgs());
	}
}