package org.greenem.plugins.Server.game;

import org.bukkit.event.Listener;

public interface ExtraGameData extends Listener {
	public GameManager gm = null;

	public GameManager getGm();
	public void setGm(GameManager gm);
	
	public void start();
}
