package org.greenem.plugins.server.worldsVersionControl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.util.FileUtil;
import org.greenem.plugins.server.data.OutsideData;

public class BuildingServerOperations {
	public static File buildingServerLocation;
	private static File buildingServerConfigLocation;
	
	public static void init() {
		buildingServerConfigLocation = new File(buildingServerLocation, "cofigs/Building");
	}
	
	public static void test() {
		File sourceFile = new File(buildingServerLocation, "testworld_1");
		File to = new File(Bukkit.getWorldContainer(), "testfiles");
		if(!to.exists()) {
			try {
				to.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
		
		try {
			FileUtils.copyDirectory(sourceFile, to);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		/*FileChannel source = null;
	    FileChannel destination = null;
		
		try {
	        source = new FileInputStream(sourceFile).getChannel(); // think about other exceptions
	        destination = new FileOutputStream(to).getChannel();
	        destination.transferFrom(source, 0, source.size());
	    } catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	    finally {
	        if(source != null) {
	            try {
					source.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
	        }
	        if(destination != null) {
	            try {
					destination.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
	        }
	    }*/
		
		try {
			Files.copy(sourceFile.toPath(), to.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
