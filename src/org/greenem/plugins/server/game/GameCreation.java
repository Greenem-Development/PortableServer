package org.greenem.plugins.server.game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.greenem.plugins.server.data.DevelopmentTests;
import org.greenem.plugins.server.data.MinigamesStaticData;
import org.greenem.plugins.server.data.NamesAndLines;
import org.greenem.plugins.server.data.References;
import org.greenem.plugins.server.data.Structure;
import org.greenem.plugins.server.util.SimpleLogger;

public class GameCreation {
	public static void startNewGame(Player p, Minigame minigame, String map, ExtraGameData data) {
		GameManager gameManager = new GameManager();
		data.setGm(gameManager);
		gameManager.minigameType = minigame;
		gameManager.extraGameData = data;
		gameManager.map = map;
//		gameManager.players.add(sender.getName());
		int minPlayers = MinigamesStaticData.minimumPlayersForGame.get(minigame);
		if(minPlayers>1) {
			TeamManager tm = References.playersTeam.get(p.getName());
			if(tm==null) {
				p.sendMessage("You can't start this minigame with no team!");
				return;
			}
			if(tm.players.size()<2) {
				p.sendMessage("You can't start this minigame alone!");
				return;
			}
			if(tm.players.size()<minPlayers) {
				p.sendMessage("You can't start this minigame with less than " + minPlayers + " players!");
				return;
			}
			for (String serverPlayer : tm.players) { // for test
				gameManager.players.add(serverPlayer);
			}
			gameManager.tm = tm;
		}
		int success = gameManager.start();
		if(success==0) {
			SimpleLogger.debug("add gameManagers");
			Structure.gameManagers.add(gameManager);
			References.playersInGame.add(p.getName());
			References.playersTeam.get(p.getName()).inGame = true;
		}
		else if(success==-1) {
			p.sendMessage(NamesAndLines.noMapsAvaliable);
		}
		else if(success==1) {
			p.sendMessage(ChatColor.RED + "An error occured while starting the game");
			SimpleLogger.log(ChatColor.RED + "STARTING A GAME DIDN'T SUCCEED! (Minigame type: " + gameManager.minigameType + ")");
		}
	}
}
