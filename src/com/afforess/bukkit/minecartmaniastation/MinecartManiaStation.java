package com.afforess.bukkit.minecartmaniastation;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;


public class MinecartManiaStation extends JavaPlugin{
	
	public MinecartManiaStation(PluginLoader pluginLoader, Server instance,
			PluginDescriptionFile desc, File folder, File plugin,
			ClassLoader cLoader) {
		super(pluginLoader, instance, desc, folder, plugin, cLoader);
		server = instance;
		description = desc;
	}

	public static Logger log = Logger.getLogger("Minecraft");
	public static Server server;
	public static PluginDescriptionFile description;
	public static MinecartActionListener listener = new MinecartActionListener();
	public static MinecartManiaStationListener vehicleListener = new MinecartManiaStationListener();

	public void onDisable() {
		// TODO Auto-generated method stub
		
	}

	public void onEnable() {
		Plugin MinecartMania = server.getPluginManager().getPlugin("Minecart Mania Core");
		
		if (MinecartMania == null) {
			log.severe("Minecart Mania Station requires Minecart Mania Core to function!");
			log.severe("Minecart Mania Station is disabled!");
			this.setEnabled(false);
		}
		else {
			Configuration.loadConfiguration();
	        getServer().getPluginManager().registerEvent(Event.Type.CUSTOM_EVENT, listener, Priority.High, this);
	        getServer().getPluginManager().registerEvent(Event.Type.VEHICLE_DAMAGE, vehicleListener, Priority.High, this);
	        getServer().getPluginManager().registerEvent(Event.Type.VEHICLE_COLLISION_ENTITY, vehicleListener, Priority.Low, this);
	        
	        PluginDescriptionFile pdfFile = this.getDescription();
	        log.info( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
		}
	}
	
	public boolean onCommand(Player player, Command c, String s, String[] list) {
		if (s.contains("reloadconfig")) {
			Configuration.loadConfiguration();
		}
		return true;
	}
}
