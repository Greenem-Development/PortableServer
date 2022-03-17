package org.greenem.plugins.server.miscellaneous;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.greenem.plugins.server.data.ConfigData;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class Stash {
	public static int getSlotNumber(int row, int column) {
		return (row-1)*9-1 + column;
	}
	
	public static ItemStack createGuiItem(final Material material, final String name, final String... lore) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();

        // Set the name of the item
        meta.setDisplayName(name);

        // Set the lore of the item
        meta.setLore(Arrays.asList(lore));

        item.setItemMeta(meta);

        return item;
    }
	
	public static ItemStack createGuiItem(final Material material, final String name, final ArrayList<String> lore) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();
        
        String[] loreArray = new String[lore.size()];
        for(int i = 0; i < lore.size(); i++) {
        	loreArray[i] = lore.get(i);
        }
        
        // Set the name of the item
        meta.setDisplayName(name);

        // Set the lore of the item
        meta.setLore(Arrays.asList(loreArray));

        item.setItemMeta(meta);

        return item;
    }
	
	public static void sendClickableMessage(Player p) {
        TextComponent message = new TextComponent("Click me");
        message.setClickEvent( new ClickEvent( ClickEvent.Action.RUN_COMMAND, "/tp @s ~ ~2 ~" ) );
        p.spigot().sendMessage( message );
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
            		//Bukkit.broadcastMessage(loc.getX() + " " + loc.getY() + " " + loc.getZ());
            		//Bukkit.broadcastMessage(loc.getBlock().getType().toString());
                	if(loc.getBlock().getType() == material) {
                		//Bukkit.broadcastMessage("Latern: " + loc);
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

	public static void addItemStack(Player p, ItemStack ItemStack, int slot) {
		if(!p.getInventory().contains(ItemStack)) {
			if(p.getInventory().getItem(slot) == null) {
				p.getInventory().setItem(slot, ItemStack);
			}
			else if(p.getInventory().getItem(slot).isSimilar(ItemStack)) {
				ItemStack.setAmount(ItemStack.getAmount()+p.getInventory().getItem(slot).getAmount());
				p.getInventory().setItem(slot, ItemStack); // what about enchantments atc.?
			}
			else {
				p.getInventory().addItem(ItemStack);
			}
		}
	}

	public static void removeMaterial(Player p, Material material) {
		if(p.getInventory().contains(material)) {
			p.getInventory().remove(material);
		}
	}
	
	public static void RemoveMaterial(Player p, int slot) {
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
	
	public static boolean isLong(String s) {
	    try { 
	        Long.parseLong(s); 
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
	
	public static void GmCommand(CommandSender sender, String[] args) { // what if it's already in some plugin?
		if(!ConfigData.allowGmCommandForOps) {
			sender.sendMessage(ChatColor.RED + "This command was disabled in the config.");
			return;
		}
		Player p = Bukkit.getPlayerExact(sender.getName());
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
				if(Bukkit.getPlayerExact(args[1]) != null){
					if(args[0].equalsIgnoreCase("0")){
						Bukkit.getPlayerExact(args[1]).setGameMode(GameMode.SURVIVAL);
					}
					else if(args[0].equalsIgnoreCase("1")){
						Bukkit.getPlayerExact(args[1]).setGameMode(GameMode.CREATIVE);
					}
					else if(args[0].equalsIgnoreCase("2")){
						Bukkit.getPlayerExact(args[1]).setGameMode(GameMode.ADVENTURE);
					}
					else if(args[0].equalsIgnoreCase("3")){
						Bukkit.getPlayerExact(args[1]).setGameMode(GameMode.SPECTATOR);
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
	
	public static void HealCommand(CommandSender sender, Player p, String pName, String[] args) {
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
			p = Bukkit.getPlayerExact(pName);
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
			Player pToP = Bukkit.getPlayerExact(pTo);
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
}
