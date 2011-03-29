package com.afforess.minecartmaniastation;

import com.afforess.minecartmaniacore.MinecartManiaMinecart;
import com.afforess.minecartmaniacore.utils.DirectionUtils.CompassDirection;

public interface Direction {
	CompassDirection direction(MinecartManiaMinecart input, String str);
}
