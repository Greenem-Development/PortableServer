package org.greenem.plugins.Server.game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.greenem.plugins.Server.data.MinigamesStaticData;
import org.greenem.plugins.Server.data.References;
import org.greenem.plugins.Server.data.Structure;
import org.greenem.plugins.Server.util.SimpleLogger;

public class GameCreation {
	public static void startNewGame(Player p, Minigame minigame, ExtraGameData data) {
		GameManager gameManager = new GameManager();
		data.setGm(gameManager);
		gameManager.minigameType = minigame;
		gameManager.extraGameData = data;
//		gameManager.players.add(sender.getName());
		int minPlayers = MinigamesStaticData.minimumPlayersForGame.get(minigame);
		if(minPlayers>1) {
			PartyManager pm = References.playersParty.get(p.getName());
			if(pm==null) {
				p.sendMessage("You can't start this minigame with no party!");
				return;
			}
			if(pm.players.size()<2) {
				p.sendMessage("You can't start this minigame alone!");
				return;
			}
			for (String serverPlayer : pm.players) { // for test
				gameManager.players.add(serverPlayer);
			}
		}
		int success = gameManager.start();
		if(success==0) {
			Structure.gameManagers.add(gameManager);
			References.playersInGame.add(p.getName());
			References.playersParty.get(p.getName()).inGame = true;
		}
		else if(success==-1) {
			p.sendMessage(ChatColor.RED + "There is currently no world avaliable to start the game");
		}
		else if(success==1) {
			p.sendMessage(ChatColor.RED + "An error occured while starting the game");
			SimpleLogger.log(ChatColor.RED + "STARTING A GAME DIDN'T SUCCEED! (Minigame type: " + gameManager.minigameType + ")");
		}
	}
}
