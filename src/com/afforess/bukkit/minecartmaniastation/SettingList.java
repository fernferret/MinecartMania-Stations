package com.afforess.bukkit.minecartmaniastation;

import org.bukkit.Material;

import com.afforess.bukkit.minecartmaniacore.config.Setting;

public class SettingList {
	public final static Setting[] config = {
		new Setting(
				"Station Block", 
				new Integer(Material.BRICK.getId()),
				"Intersection Station Block.",
				MinecartManiaStation.description.getName()
		),
		new Setting(
				"Intersection Prompts", 
				new Integer(0),
				"If set to 0, Will prompt users for their intended direction when a player reaches an intersection. If set to 1, will prompt users only if the intersection has a station block underneath. If set to 2 players will never be prompted at intersections.",
				MinecartManiaStation.description.getName()
		)
	};

}
