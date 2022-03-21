package org.greenem.plugins.server.worldsVersionControl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import static org.greenem.plugins.server.util.SimpleLogger.log;
import static org.greenem.plugins.server.util.SimpleLogger.debug;;

public class ReplaceWorldCompleter implements TabCompleter {
	private boolean filterNotMatchingResults = true;

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		List<String> arguments = new ArrayList<>();
		List<String> nothing = new ArrayList<>();
		//sender.sendMessage(commandLabel);
		if(cmd.getName().equalsIgnoreCase("replaceworld")) { // IS THIS NESSESSARY????
			int currentLength = 0;
			if(args.length==1) {
				if(args[0].startsWith("•")) return nothing;
				arguments.add("• usage: <name from> <optional name to>  (don't specify copy id)");
				for (String s : BuildingServerOperations.buildingServerWorlds) {
					arguments.add(s);
				}
				arguments = deleteNotMatching(arguments, args[currentLength]);
				return arguments;
			}
			if(args.length==2) {
				currentLength++;
				if(args[0].startsWith("•")) return nothing;
				if(args[1].startsWith("•")) return nothing;
				arguments.add(args[0]);
				return arguments;
			}
			
//			arguments = deleteNotMatching(arguments, args[currentLength OR ""]);
			return nothing;
		}
		return null;
	}
	
	private List<String> getGoodWorldsList() {
		List<String> allWorlds = getLoadedWorldsList();
		List<String> gooldWorlds = new ArrayList<>();
		for (String s : allWorlds) {
			if(BuildingServerOperations.buildingServerWorlds.contains(s)) {
				gooldWorlds.add(s);
			}
		}
		return gooldWorlds;
	}
	
	private List<String> getLoadedWorldsList() {
		List<String> worlds = new ArrayList<>();
		for (World w : Bukkit.getWorlds()) {
			worlds.add(w.getName());
		}
		return worlds;
	}
	
	private List<String> deleteNotMatching(List<String> list, String currentText) {
		if(!filterNotMatchingResults) {
			return list;
		}
		for (Iterator<String> it = list.iterator(); it.hasNext(); ) {
			String s = it.next();
		    if (currentText!=null && !s.startsWith(currentText)) {
		        it.remove();
		    }
		}
		return list;
	}
}
