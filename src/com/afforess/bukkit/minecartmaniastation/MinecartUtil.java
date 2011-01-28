package com.afforess.bukkit.minecartmaniastation;

import org.bukkit.util.Vector;

import com.afforess.bukkit.minecartmaniacore.DirectionUtils;
import com.afforess.bukkit.minecartmaniacore.MinecartManiaMinecart;
import com.afforess.bukkit.minecartmaniacore.MinecartManiaWorld;

public class MinecartUtil {

	public static int getStationBlockID() {
		return MinecartManiaWorld.getIntValue(MinecartManiaWorld.getConfigurationValue("station block"));
	}
	
	public static boolean isAutoIntersectionPrompt() {
		Object o = MinecartManiaWorld.getConfigurationValue("Auto Intersection Prompts");
		if (o != null) {
			Boolean value = (Boolean)o;
			return value.booleanValue();
		}
		return false;
	}
	
	public static boolean isInQueue(MinecartManiaMinecart minecart) {
		return minecart.getDataValue("queued velocity") != null;
	}
	
	public static Vector alterMotionFromDirection(DirectionUtils.CompassDirection direction, Vector oldVelocity) {
		double speed = Math.abs(oldVelocity.getX()) > Math.abs(oldVelocity.getZ()) ? Math.abs(oldVelocity.getX()) : Math.abs(oldVelocity.getZ());
		
		if (direction.equals(DirectionUtils.CompassDirection.NORTH)) {
			return new Vector(-speed, 0, 0);
		}
		if (direction.equals(DirectionUtils.CompassDirection.SOUTH)) {
			return new Vector(speed, 0, 0);
		}
		if (direction.equals(DirectionUtils.CompassDirection.EAST)) {
			return new Vector(0, 0, -speed);
		}
		if (direction.equals(DirectionUtils.CompassDirection.WEST)) {
			return new Vector(0, 0, speed);
		}
		
		return null;
	}

	public static boolean isStationIntersectionPrompt() {
		Object o = MinecartManiaWorld.getConfigurationValue("Intersection Prompts Only at Station Blocks");
		if (o != null) {
			Boolean value = (Boolean)o;
			return value.booleanValue();
		}
		return false;
	}
}
