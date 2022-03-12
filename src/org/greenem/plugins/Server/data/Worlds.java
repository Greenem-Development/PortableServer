package org.greenem.plugins.Server.data;

import java.util.ArrayList;

public class Worlds {
	public static ArrayList<String> temporaryUnaliableWorlds = new ArrayList<>(); // for later, also maybe not a String
	
	public static ArrayList<String> usedWorlds = new ArrayList<>(); // delete from here only when unloaded
	
	// and also maybe a list of worlds with ignored temporaryUnaliableWorlds for some cases
}
