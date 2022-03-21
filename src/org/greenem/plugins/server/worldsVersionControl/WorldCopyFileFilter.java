package org.greenem.plugins.server.worldsVersionControl;

import java.io.File;
import java.io.FileFilter;

public class WorldCopyFileFilter implements FileFilter {
	@Override
	public boolean accept(File pathname) {
		String s = pathname.getName();
		if(s.equals("session.lock") || s.equals("uid.dat")) {
			return false;
		}
		return true;
	}
}
