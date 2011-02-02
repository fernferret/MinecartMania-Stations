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
				"Auto Intersection Prompts", 
				Boolean.TRUE, 
				"When a player reaches an intersection, they will be prompted for the direction they wish to go.",
				MinecartManiaStation.description.getName()
		),
		new Setting(
				"Intersection Prompts Only at Station Blocks", 
				Boolean.FALSE, 
				"When a player reaches an intersection, they will onyl be prompted for the direction they wish to go only if there is a station block underneath the intersection.",
				MinecartManiaStation.description.getName()
		)
	};

}
