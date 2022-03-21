package org.greenem.plugins.server.util;

public class SmallUtils {
	public static String deleteMultilineNullsInString(String s) {
		s = s.trim();
		if(s.equals("null")) {
			return "";
		}
		String[] parts = s.split("\n");
		String newS = "";
		for (int i = 0; i < parts.length; i++) {
			if(i>0) {
				newS += "\n";
			}
			if(!parts[i].trim().equals("null")) {
				newS += parts[i].trim();
			}
		}
		return newS;
	}
}
