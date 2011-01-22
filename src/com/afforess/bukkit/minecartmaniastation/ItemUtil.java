package com.afforess.bukkit.minecartmaniastation;

import org.bukkit.Material;

public class ItemUtil {

	public static Material itemStringToMaterial(String str) {
		int wildcard = str.indexOf('*');
		if (wildcard > -1) {
			str = str.substring(0, wildcard-1);
		}
		if (str.indexOf('[') > -1) {
			str = str.substring(str.indexOf('[') + 1);
		}
		str = str.toLowerCase();
		Material e;
		

		for (int i = 0; i < Material.GREEN_RECORD.getId(); i++) {
			e = Material.getMaterial(i);
			if (e != null) {
				String item = e.toString().toLowerCase();
				if (item.indexOf(str) > -1) {
					
					return e;
				}
			}
		}
		return null;
	}
}
