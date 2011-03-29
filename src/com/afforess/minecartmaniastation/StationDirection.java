package com.afforess.minecartmaniastation;

import com.afforess.minecartmaniacore.MinecartManiaMinecart;
import com.afforess.minecartmaniacore.utils.DirectionUtils;
import com.afforess.minecartmaniacore.utils.DirectionUtils.CompassDirection;

public enum StationDirection implements Direction {
	Straight {
		@Override
		public CompassDirection direction(MinecartManiaMinecart minecart, String str) {
			if (str.equals("STR") || str.toLowerCase().contains("straight"))
				return minecart.getPreviousFacingDir();
			return CompassDirection.NO_DIRECTION;
		}
	},
	North {
		@Override
		public CompassDirection direction(MinecartManiaMinecart minecart, String str) {
			if (str.equals("N") || str.toLowerCase().contains("north"))
				return CompassDirection.NORTH;
			return CompassDirection.NO_DIRECTION;
		}
	},
	East {
		@Override
		public CompassDirection direction(MinecartManiaMinecart minecart, String str) {
			if (str.equals("E") || str.toLowerCase().contains("east"))
				return CompassDirection.EAST;
			return CompassDirection.NO_DIRECTION;
		}
	},
	South {
		@Override
		public CompassDirection direction(MinecartManiaMinecart minecart, String str) {
			if (str.equals("S") || str.toLowerCase().contains("south"))
				return CompassDirection.SOUTH;
			return CompassDirection.NO_DIRECTION;
		}
	},
	West {
		@Override
		public CompassDirection direction(MinecartManiaMinecart minecart, String str) {
			if (str.equals("W") || str.toLowerCase().contains("west"))
				return CompassDirection.WEST;
			return CompassDirection.NO_DIRECTION;
		}
	},
	Left {
		@Override
		public CompassDirection direction(MinecartManiaMinecart minecart, String str) {
			if (str.equals("L") || str.toLowerCase().contains("left"))
				return DirectionUtils.getLeftDirection(minecart.getPreviousFacingDir());
			return CompassDirection.NO_DIRECTION;
		}
	},
	Right {
		@Override
		public CompassDirection direction(MinecartManiaMinecart minecart, String str) {
			if (str.equals("R") || str.toLowerCase().contains("right"))
				return DirectionUtils.getRightDirection(minecart.getPreviousFacingDir());
			return CompassDirection.NO_DIRECTION;
		}
	},
	Destroy {
		@Override
		public CompassDirection direction(MinecartManiaMinecart minecart, String str) {
			if (str.equals("D") || str.toLowerCase().contains("destroy"))
				return null;
			return CompassDirection.NO_DIRECTION;
		}
	},
	Prompt {
		@Override
		public CompassDirection direction(MinecartManiaMinecart minecart, String str) {
			if (str.equals("P") || str.toLowerCase().contains("prompt")) {
				if (minecart.hasPlayerPassenger()) {
					minecart.setDataValue("Prompt Override", true);
				}
			}
			return CompassDirection.NO_DIRECTION;
		}
	}
}
