package org.greenem.plugins.server.data;

import java.io.File;
import java.util.Calendar;

public class OutsideData {
	public static File configsDirectory;
	public static File mainConfigDirectory;
	
	public static long timeOfPluginStart;
	
	public static void init() {
		timeOfPluginStart = Calendar.getInstance().getTimeInMillis();
	}
}
