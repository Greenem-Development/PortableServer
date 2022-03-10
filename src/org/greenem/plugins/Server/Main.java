package org.greenem.plugins.Server;

import static org.greenem.plugins.Server.SimpleLogger.log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Logger;
import org.bukkit.event.Listener;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

//import com.mysql.jdbc.Connection;



public class Main extends JavaPlugin implements Listener {
	
	Logger log = getLogger();

	public File configsDirectory;
	public File mainConfigDirectory;
	
	Plugin plugin = this;
	
	@Override
	public void onDisable() {
		log(ChatColor.RED + "Your Portable Server plugin has been disabled.");
	}

	@Override
	public void onEnable() {
		log(ChatColor.GREEN + "Your Portable Server plugin has been enabled!");
		
		getServer().getPluginManager().registerEvents(this, this);
		RegisterAllCommands();
		InitFiles();
		
		loadConfig();
		
		//healTests();
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
				GmCommand(sender, args);
			}
		}
		else if(cmd.getName().equalsIgnoreCase("heal")){
			if(isSenderAPlayerOrConsole(sender)) {
				HealCommand(sender, p, null, args);
			}
		}
		return false;
	}
	
	public void logCastEx(String currentKey) {
		log("Wrong value type for \"" + currentKey + "\" in the config");
	}
	
	public void loadConfig() {
		File f = new File(mainConfigDirectory, "mainconfig.yml");
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
	
	public boolean isSenderAPlayer(CommandSender sender) {
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
	
	public boolean isSenderAPlayerOrConsole(CommandSender sender) {
		if(!(sender instanceof Player) && !(sender instanceof ConsoleCommandSender)) {
			sender.sendMessage(ChatColor.RED + "You can use it only as a player!");
			return false;
		}
		return true;
	}
	
	public void GmCommand(CommandSender sender, String[] args) { // what if it's already in some plugin?
		if(!ConfigData.allowGmCommandForOps) {
			sender.sendMessage(ChatColor.RED + "This command was disabled in the config.");
			return;
		}
		Player p = getServer().getPlayerExact(sender.getName());
		if(sender instanceof ConsoleCommandSender || p.isOp() == true){
			if(args.length == 0){
				sender.sendMessage(ChatColor.RED + "Please, insert gamemode number.");
			}
			else if(args.length == 1){
				if(sender instanceof ConsoleCommandSender) {
					sender.sendMessage(ChatColor.RED + "You can only change the players' gamemodes from console!");
					return;
				}
				if(args[0].equalsIgnoreCase("0")){
					p.setGameMode(GameMode.SURVIVAL);
				}
				else if(args[0].equalsIgnoreCase("1")){
					p.setGameMode(GameMode.CREATIVE);
				}
				else if(args[0].equalsIgnoreCase("2")){
					p.setGameMode(GameMode.ADVENTURE);
				}
				else if(args[0].equalsIgnoreCase("3")){
					p.setGameMode(GameMode.SPECTATOR);
				}
				else {
					sender.sendMessage(ChatColor.RED + "Please, insert correct gamemode number.");
				}
			}
			else if(args.length == 2){
				if(getServer().getPlayerExact(args[1]) != null){
					if(args[0].equalsIgnoreCase("0")){
						getServer().getPlayerExact(args[1]).setGameMode(GameMode.SURVIVAL);
					}
					else if(args[0].equalsIgnoreCase("1")){
						getServer().getPlayerExact(args[1]).setGameMode(GameMode.CREATIVE);
					}
					else if(args[0].equalsIgnoreCase("2")){
						getServer().getPlayerExact(args[1]).setGameMode(GameMode.ADVENTURE);
					}
					else if(args[0].equalsIgnoreCase("3")){
						getServer().getPlayerExact(args[1]).setGameMode(GameMode.SPECTATOR);
					}
					else {
						sender.sendMessage(ChatColor.RED + "Please, insert correct gamemode number.");
					}
				}
				else {
					if(args[1].equals("0") || args[1].equals("1") || args[1].equals("2") || args[1].equals("3")) {
						sender.sendMessage(ChatColor.RED + "Usage for other players: /gm <number 0-3> <player>");
					}
					else {
						sender.sendMessage(ChatColor.RED + "Such player isn't online.");
					}
				}
			}
			else {
				sender.sendMessage(ChatColor.RED + "Please, type the command in such order: /gm <number 0-3> <player>");
			}
		}
		else {
			sender.sendMessage(ChatColor.RED + "You can't use this command.");
		}
	}
	
	public void HealCommand(CommandSender sender, Player p, String pName, String[] args) {
		boolean console = sender instanceof ConsoleCommandSender;
		if(!ConfigData.allowHealCommandForOps) {
			if(p!=null) {
				p.sendMessage(ChatColor.RED + "This command was disabled in the config.");
			}
			return;
		}
		if(console) {
			pName = "[CONSOLE]";
		}
		else if(pName==null) {
			pName = p.getName();
		}
		if(p==null) {
			p = getServer().getPlayerExact(pName);
		}
		String pFrom = pName;
		String notifpersonal;
		String colorNameFrom = pFrom;
		String notifOthers;
		if(args.length == 0) {
			if(sender instanceof ConsoleCommandSender) {
				sender.sendMessage(ChatColor.RED + "You can't heal yourself as the console!");
				return;
			}
			if(p!=null) {
				p.setHealth(20);
				p.setFoodLevel(20);
			}
			notifpersonal = ChatColor.GRAY + "You have been healed";
			notifOthers = colorNameFrom + ChatColor.RESET + "" + ChatColor.GRAY + " was healed";
			if(p!=null) {
				p.sendMessage(notifpersonal);
			}
			/*if(Hunters.contains(pName) || Speedrunners.contains(pName)) { // LATER - IF IN MINAGAME CURRENTLY (to think before)
				if(logHealInfo) log("notifOthers: " + notifOthers);
				for (String pAlert : Hunters) {
					sendOthersTheAlert(pAlert, pName, notifOthers);
				}
				for (String pAlert : Speedrunners) {
					sendOthersTheAlert(pAlert, pName, notifOthers);
				}
			}*/
		}
		else if(args.length==1) {
			String pTo = args[0];
			Player pToP = getServer().getPlayerExact(pTo);
			if(pToP!=null) {
				String colorNameTo = pTo;
				boolean inMinigame = false; // IF IS IN MINIGAME
				
				if(pToP!=null) {
					pToP.setHealth(20);
					pToP.setFoodLevel(20);
				}
				
				if(pTo.equals(pFrom)) {
					notifpersonal = ChatColor.GRAY + "You have been healed";
					if(p!=null) {
						p.sendMessage(notifpersonal);
					}
					notifOthers = colorNameFrom + ChatColor.RESET + "" + ChatColor.GRAY + " was healed";
				}
				else {
					String notifToThatOne = ChatColor.GRAY + "You have been healed by " + colorNameFrom;
					notifpersonal = ChatColor.GRAY + "You have healed " + colorNameTo;
					if(pToP!=null) {
						pToP.sendMessage(notifToThatOne);
					}
					if(p!=null) {
						p.sendMessage(notifpersonal);
					}
					notifOthers = ChatColor.WHITE + colorNameFrom + ChatColor.RESET + " was healed";
					if(!colorNameFrom.equals(colorNameTo)) {
						notifOthers += " by " + ChatColor.WHITE + colorNameTo;
					}
				}
				
				if(inMinigame) {
					/*for (String pAlert : Hunters) {
						sendOthersTheAlert(pAlert, pName, notifOthers);
					}
					for (String pAlert : Speedrunners) {
						sendOthersTheAlert(pAlert, pName, notifOthers);
					}*/
				}
			}
			else {
				String problem = ChatColor.RED + "Such player isn't online.";
				if(p!=null) {
					p.sendMessage(problem);
				}
			}
		}
		else {
			String problem = ChatColor.RED + "Please, type the command in such order: /heal <player>.";
			if(p!=null) {
				p.sendMessage(problem);
			}
		}
	}

	public void AddItemStack(Player p, ItemStack ItemStack, int slot) {
		if(!p.getInventory().contains(ItemStack)) {
			if(p.getInventory().getItem(slot) == null) p.getInventory().setItem(slot, ItemStack);
			else p.getInventory().addItem(ItemStack);
		}
	}

	public void RemoveMaterial(Player p, Material material) {
		if(p.getInventory().contains(material)) {
			p.getInventory().remove(material);
		}
	}
	
	public void RemoveMaterial(Player p, int slot) {
		//p.getInventory().remove(p.getInventory().getItem(slot));
		p.getInventory().setItem(slot, new ItemStack(Material.AIR));
	}
	
	public static boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    } catch(NullPointerException e) {
	        return false;
	    }
	    return true;
	}
    
	public static void sendActionBar(Player player, String message){
//        CraftPlayer p = (CraftPlayer) player;
		
        //IChatBaseComponent cbc = ChatSerializer.a("{\"text\": \"" + message + "\"}");
        //PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc, null, null);
        //player.send
        //((CraftPlayer) p).getHandle().playerConnection.sendPacket(ppoc);
    }
	
	public void InitFiles() {
		configsDirectory = new File(getServer().getWorldContainer(), "configs");
		mainConfigDirectory = new File(configsDirectory, "Server");
		
		mainConfigDirectory.mkdirs();
	}
	
	public void RegisterAllCommands() {
//		getCommand("mhproblems").setTabCompleter(new MhProblemsCompleter());
	}

	public Location IsBlockNearby(Player p, Material material, int distanse) {
        ArrayList<Location> temp = new ArrayList<Location>();
        ArrayList<Double> dis = new ArrayList<Double>();
 
        Location loc = new Location(p.getWorld(), p.getLocation().getBlock().getX()-distanse, p.getLocation().getBlock().getY()-distanse, p.getLocation().getBlock().getZ()-distanse);
        //p.sendMessage("Drop event work");//For debug
        //p.sendMessage(loc.getX() + " " + loc.getZ());
        for(int i=1; i <= 2*distanse+1 ; i++) {
            for(int j =1; j <= 2*distanse+1 ; j++ ) {
            	for(int k =1; k <= 2*distanse+1 ; k++ ) {
            		//getServer().broadcastMessage(loc.getX() + " " + loc.getY() + " " + loc.getZ());
            		//getServer().broadcastMessage(loc.getBlock().getType().toString());
                	if(loc.getBlock().getType() == material) {
                		//getServer().broadcastMessage("Latern: " + loc);
                    	temp.add(loc.getBlock().getLocation());
                	}
            		loc.setX(loc.getX()+1);
            	}
            	loc.setX(p.getLocation().getBlock().getX()-distanse);
            	loc.setY(loc.getY()+1);
            }
            loc.setY(p.getLocation().getBlock().getY()-distanse);
        	loc.setZ(loc.getZ()+1);
        }
        for(int i = 0; i<temp.size(); i++) {
        	dis.add(temp.get(i).distance(p.getLocation()));
        }
        Collections.sort(dis);
        for(int i = 0; i<temp.size(); i++) {
        	if(temp.get(i).distance(p.getLocation()) == dis.get(0).doubleValue()) {
        		//p.sendMessage(temp.get(i) + "");
        		temp.get(i).add(0.5, 0, 0.5);
        		return temp.get(i);
        	}
        }
        //p.sendMessage("Temp :" + temp);//For debug
        //p.sendMessage("Dis :" + dis);//For debug
        //p.spawnParticle(Particle.BLOCK_CRACK, new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ()), 100, blood.createBlockData());
        return null;
	}
}