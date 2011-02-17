package com.afforess.minecartmaniastation;

import org.bukkit.Material;

import com.afforess.minecartmaniacore.config.Setting;

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
		),
		new Setting(
				"Station Sign Parsing Method", 
				new Integer(0),
				"0 - simple parsing with no pattern matching. 1 - simple pattern matching. 2 - full regex parsing.",
				MinecartManiaStation.description.getName()
		)
	};

}
