package org.greenem.plugins.server.game;

import java.util.ArrayList;

import org.bukkit.event.Listener;

public interface ExtraGameData extends Listener {
	public GameManager gm = null;
	public static final ArrayList<String> actualPlayers = new ArrayList<>();
	
	public GameManager getGm();
	public void setGm(GameManager gm);
	
	public String start();
}
