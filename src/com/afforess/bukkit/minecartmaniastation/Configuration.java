package com.afforess.bukkit.minecartmaniastation;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.bukkit.Material;

import com.afforess.bukkit.minecartmaniacore.MinecartManiaCore;
import com.afforess.bukkit.minecartmaniacore.MinecartManiaFlatFile;
import com.afforess.bukkit.minecartmaniacore.MinecartManiaWorld;

public class Configuration {
	/**
	 ** Initializes configuration values
	 ** 
	 **/
	public static void loadConfiguration() {
		readFile();
	}

	public static void readFile() {	
		
		File directory = new File("MinecartMania" + File.separator);
		if (!directory.exists())
			directory.mkdir();
		File options = new File("MinecartMania" + File.separator + "MinecartManiaStation.txt");
		if (!options.exists() || invalidFile(options))
		{
			WriteFile(options);
		}
		ReadFile(options);
	}
	
	private static boolean invalidFile(File file)
	{
		try {
			BufferedReader bufferedreader = new BufferedReader(new FileReader(file));
			for (String s = ""; (s = bufferedreader.readLine()) != null; )
			{
				if (s.indexOf(MinecartManiaStation.description.getVersion()) > -1)
				{
					return false;
				}

			}
			bufferedreader.close();
		}
		catch (IOException exception)
		{
			return true;
		}
		return true;
	}
	
	private static void WriteFile(File file)
	{
		try
		{
			file.createNewFile();
			BufferedWriter bufferedwriter = new BufferedWriter(new FileWriter(file));
			MinecartManiaFlatFile.createNewHeader(bufferedwriter, "Minecraft Mania Stations" + MinecartManiaStation.description.getVersion(), "Minecart Mania Station Config Settings", true);
			MinecartManiaFlatFile.createNewSetting(bufferedwriter, "Station Block", Material.BRICK.toString(), 
			"Intersection Station Block.");
			MinecartManiaFlatFile.createNewSetting(bufferedwriter, "Auto Intersection Prompts", "true", 
			"When a player reaches an intersection, they will be prompted for the direction they wish to go.");
			MinecartManiaFlatFile.createNewSetting(bufferedwriter, "Intersection Prompts Only at Station Blocks", "false", 
			"When a player reaches an intersection, they will onyl be prompted for the direction they wish to go if there is a station block underneath the intersection.");


			bufferedwriter.close();
		}
		catch (Exception exception)
		{
			MinecartManiaCore.log.severe("Failed to write Minecart Mania settings!");
			exception.printStackTrace();
		}
	}

	private static void ReadFile(File file)
	{
		try {
			MinecartManiaWorld.setConfigurationValue("station block", new Integer(
					Material.valueOf(MinecartManiaFlatFile.getValueFromSetting(file, "Station Block", Material.BRICK.toString())).getId()));
			
			MinecartManiaWorld.setConfigurationValue("Auto Intersection Prompts", new Boolean(
					MinecartManiaFlatFile.getValueFromSetting(file, "Auto Intersection Prompts", true)));
			
			MinecartManiaWorld.setConfigurationValue("Intersection Prompts Only at Station Blocks", new Boolean(
					MinecartManiaFlatFile.getValueFromSetting(file, "Intersection Prompts Only at Station Blocks", true)));

		}
		catch (Exception exception)
		{
			MinecartManiaCore.log.severe("Failed to read Minecart Mania settings!");
			exception.printStackTrace();
		}
	}

}
